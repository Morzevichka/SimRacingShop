package com.morzevichka.auth_service.security;

import com.morzevichka.auth_service.exception.user.UserAccountLockedException;
import com.morzevichka.auth_service.exception.user.UserEmailNotVerifiedException;
import com.morzevichka.auth_service.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String email = authentication.getName();
        final String password = authentication.getCredentials().toString();

        CustomUserDetails userDetails;
        try {
            userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        } catch (UserNotFoundException e) {
            throw new BadCredentialsException("Invalid email or password");
        } catch (UserEmailNotVerifiedException e) {
            throw new DisabledException("Email is not verified");
        } catch (UserAccountLockedException e) {
            throw new LockedException("Account is locked. Contact our support team");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
