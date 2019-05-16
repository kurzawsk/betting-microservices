package pl.kk.services.common.oauth2;

import feign.Request;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import pl.kk.services.common.misc.FeignBadResponseWrapper;

import java.util.Arrays;

public class FeignClientConfiguration {

    private static final int DEFAULT_TIMEOUT = 20000;

    @Value("${security.oauth2.client.access-token-uri}")
    private String accessTokenUri;
    @Value("${security.oauth2.client.client-id}")
    private String clientId;
    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;
    @Value("${security.oauth2.client.scope}")
    private String scope;

    @Bean
    RequestInterceptor oauth2FeignRequestInterceptor() {
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), resource());
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT);
    }

   @Bean
   @Qualifier("clientCredentialsResourceDetails")
    public OAuth2ProtectedResourceDetails resource() {
        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
        resourceDetails.setAccessTokenUri(accessTokenUri);
        resourceDetails.setClientId(clientId);
        resourceDetails.setClientSecret(clientSecret);
        resourceDetails.setScope(Arrays.asList(scope.split(",")));
        return resourceDetails;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            int status = response.status();
            String body = "Response content: ";
            try {
                body = IOUtils.toString(response.body().asReader());
            } catch (Exception ignored) {
                body = body + response.toString();
            }
            HttpHeaders httpHeaders = new HttpHeaders();
            response.headers().forEach((k, v) -> httpHeaders.add(k, StringUtils.join(v, ",")));
            return new FeignBadResponseWrapper(response.reason(), status, httpHeaders, body);
        };
    }

}
