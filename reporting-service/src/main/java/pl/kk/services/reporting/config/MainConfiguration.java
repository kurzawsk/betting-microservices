package pl.kk.services.reporting.config;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.web.client.RestTemplate;
import pl.kk.services.common.config.BasicConfiguration;
import pl.kk.services.common.config.JpaConfiguration;
import pl.kk.services.common.config.OAuth2ResourceServerConfig;
import pl.kk.services.common.config.SwaggerConfiguration;
import pl.kk.services.common.oauth2.TokenBearerInterceptor;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;

import java.util.function.Supplier;

@Configuration
@EnableJpaRepositories(basePackages = "pl.kk.services.reporting.repository")
@Import({BasicConfiguration.class, OAuth2ResourceServerConfig.class, SwaggerConfiguration.class, JpaConfiguration.class})
public class MainConfiguration {

    @Bean
    @Qualifier("packageToScanSupplier")
    Supplier<String> packageToScanSupplier() {
        return () -> "pl.kk.services.reporting.model";
    }

    @Bean
    @Qualifier("swaggerPackagesSupplier")
    Supplier<String> swaggerPackagesSupplier() {
        return () -> "pl.kk.services.reporting.controller";
    }

    @Bean
    @Qualifier("swaggerApiInfoSupplier")
    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .version("1.0")
                .title("reporting API")
                .description("Documentation reporting API v1.0")
                .build();
    }

}