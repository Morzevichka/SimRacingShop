package com.morzevichka.api_gateway.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class AddAuthorizationHeaderFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            return next.handle(request);
        }

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("auth-server")
                .principal(authentication)
                .attribute(HttpServletRequest.class.getName(), request.servletRequest())
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if (authorizedClient != null) {
            log.info("Set Auth Header to Request");
            request = ServerRequest.from(request)
                    .header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                    .build();
        }

        return next.handle(request);
    }
}
