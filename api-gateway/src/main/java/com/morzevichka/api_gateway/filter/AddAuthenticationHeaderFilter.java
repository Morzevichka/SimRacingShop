package com.morzevichka.api_gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddAuthenticationHeaderFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final OAuth2AuthorizedClientRepository auth2AuthorizedClientRepository;

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction next) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication)) {
            return next.handle(request);
        }

        OAuth2AuthorizedClient client = auth2AuthorizedClientRepository
                .loadAuthorizedClient("auth-server", authentication, request.servletRequest());

        if (Objects.nonNull(client)) {
            log.info("Set Auth Header to Request");
            request = ServerRequest.from(request)
                    .header("Authorization", "Bearer " + client.getAccessToken().getTokenValue())
                    .build();
        }

        return next.handle(request);
    }
}
