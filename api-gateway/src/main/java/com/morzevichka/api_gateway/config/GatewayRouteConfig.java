package com.morzevichka.api_gateway.config;

import com.morzevichka.api_gateway.filter.AddAuthenticationHeaderFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.*;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
@RequiredArgsConstructor
public class GatewayRouteConfig {

    private final AddAuthenticationHeaderFilter addAuthenticationHeaderFilter;

    @Bean
    RouterFunction<ServerResponse> route() {
        return GatewayRouterFunctions.route("api")
                .route(RequestPredicates.path("/api/**"), http())
                .before(uri("http://localhost:8082"))
                .filter(addAuthenticationHeaderFilter)
                .build();
    }
}
