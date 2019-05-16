package pl.kk.services.auth.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

public class CustomTokenService extends DefaultTokenServices {

    @Override
    public synchronized OAuth2AccessToken createAccessToken(OAuth2Authentication authentication)
            throws AuthenticationException {
        return super.createAccessToken(authentication);
    }

    @Override
    public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest)
            throws AuthenticationException {
        synchronized (refreshTokenValue.intern()) {
            return super.refreshAccessToken(refreshTokenValue, tokenRequest);
        }

    }
}
