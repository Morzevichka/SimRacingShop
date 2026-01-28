package com.morzevichka.api_gateway.config;

import com.morzevichka.api_gateway.dto.ErrorDto;
import com.morzevichka.api_gateway.filter.AddAuthorizationHeaderFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GatewayRouteConfig {

    private final AddAuthorizationHeaderFilter addAuthorizationHeaderFilter;

    @Bean
    RouterFunction<ServerResponse> routerFunction() {
//        return route()
//                .path("/api/**", b -> b
//                        .filter(lb("CHAT-SERVICE"))
//                        .filter(addAuthorizationHeaderFilter)
//                        .build()
//                )
//                .path("/oauth2/**", b -> b
//                        .filter(lb("AUTH-SERVICE"))
//                )
//                .build();
        return
                route("api")
                        .onError(Exception.class, this::handleException)
                        .route(path("/api/**"), http())
                        .filter(lb("CHAT-SERVICE"))
                        .filter(addAuthorizationHeaderFilter)
                        .build()
                .and(route("oauth2")
                        .onError(Exception.class, this::handleException)
                        .route(path("/oauth2/**"), http())
                        .filter(lb("AUTH-SERVICE"))
                        .build()
                );
    }

    private ServerResponse handleException(Throwable throwable, ServerRequest request) {
        log.error("#handleException - failed to run request {}", request.uri(), throwable);
        ErrorDto errorDto = ErrorDto.builder()
                .errorDetails(List.of(request.uri().toString(), throwable.getMessage()))
                .build();
        return ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDto);
    }
}

