package pl.kk.services.common.config;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.web.client.RestTemplate;
import pl.kk.services.common.oauth2.FeignClientConfiguration;
import pl.kk.services.common.oauth2.TokenBearerInterceptor;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = {"pl.kk.services.common.service.security", "pl.kk.services.common.service.exception"})
@Import(FeignClientConfiguration.class)
public class OAuth2ResourceServerConfig extends GlobalMethodSecurityConfiguration {

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new OAuth2MethodSecurityExpressionHandler();
    }

    @Bean
    @Qualifier("requestTokenBearerInterceptor")
    public TokenBearerInterceptor requestTokenBearerInterceptor() {
        return new TokenBearerInterceptor();
    }

    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
        methodInvokingFactoryBean.setTargetMethod("setStrategyName");
        methodInvokingFactoryBean.setArguments(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        return methodInvokingFactoryBean;
    }

    @LoadBalanced
    @Bean
    @Autowired
    RestTemplate restTemplate(@Qualifier("requestTokenBearerInterceptor") TokenBearerInterceptor tokenBearerInterceptor) {
        RestTemplate template = new RestTemplate();
        template.setInterceptors(ImmutableList.of(tokenBearerInterceptor));
        return template;
    }

    @Bean
    @Autowired
    public OAuth2RestOperations oauth2RestOperations(@Qualifier("clientCredentialsResourceDetails") OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails, RestTemplateCustomizer customizer) {
        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(oAuth2ProtectedResourceDetails, new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest()));
        customizer.customize(oAuth2RestTemplate);
        return oAuth2RestTemplate;
    }

}
