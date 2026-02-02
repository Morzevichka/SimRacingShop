package com.morzevichka.api_gateway.config;

import com.morzevichka.api_gateway.dto.ErrorDto;
import com.morzevichka.api_gateway.filter.AddAuthorizationHeaderFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ClientProperties.class)
public class GatewayRouteConfig {

    private final AddAuthorizationHeaderFilter addAuthorizationHeaderFilter;

    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return route("api")
                .onError(Exception.class, this::handleException)
                .route(path("/api/**"), http())
                .filter(lb("chat-service"))
                .filter(addAuthorizationHeaderFilter)
                .build()
                .and(route("verify-email")
                        .route(path("/verify-email"), request -> {

                            URI modifiedUri = UriComponentsBuilder.fromUri(request.uri())
                                    .host("localhost")
                                    .port(8081)
                                    .build()
                                    .toUri();

                            System.out.println(modifiedUri);
                            return ServerResponse.temporaryRedirect(modifiedUri).build();
                        })
                        .build());

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

