package pl.kk.services.common.oauth2;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.io.IOException;
import java.util.Optional;

public class TokenBearerInterceptor implements RequestInterceptor, ClientHttpRequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        getAuthorizationHeaderValue().ifPresent(headerValue -> requestTemplate.header(AUTHORIZATION_HEADER, headerValue));
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        getAuthorizationHeaderValue()
                .ifPresent(headerValue -> httpRequest.getHeaders().add(AUTHORIZATION_HEADER, headerValue));
        return clientHttpRequestExecution.execute(httpRequest, body);
    }

    private Optional<String> getAuthorizationHeaderValue() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional
                .ofNullable(authentication)
                .map(a -> "Bearer " + ((OAuth2AuthenticationDetails) a.getDetails()).getTokenValue());
    }
}