package pl.kk.services.job.config;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
import pl.kk.services.common.config.BasicConfiguration;
import pl.kk.services.common.config.JpaConfiguration;
import pl.kk.services.common.config.OAuth2ResourceServerConfig;
import pl.kk.services.common.config.SwaggerConfiguration;
import pl.kk.services.common.oauth2.TokenBearerInterceptor;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;

import java.util.Arrays;
import java.util.function.Supplier;

@Configuration
@EnableJpaRepositories(basePackages = "pl.kk.services.job.repository")
@Import({BasicConfiguration.class, OAuth2ResourceServerConfig.class, SwaggerConfiguration.class, JpaConfiguration.class})
public class MainConfiguration {

    @Bean
    @Qualifier("packageToScanSupplier")
    Supplier<String> packageToScanSupplier() {
        return () -> "pl.kk.services.job.model";
    }

    @Bean
    @Qualifier("swaggerPackagesSupplier")
    Supplier<String> swaggerPackagesSupplier() {
        return () -> "pl.kk.services.job.controller";
    }

    @Bean
    @Qualifier("swaggerApiInfoSupplier")
    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .version("1.0")
                .title("Job API")
                .description("Documentation Job API v1.0")
                .build();
    }

}