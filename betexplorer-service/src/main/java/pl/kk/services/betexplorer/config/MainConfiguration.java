package pl.kk.services.betexplorer.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.kk.services.common.config.*;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;

import java.util.function.Supplier;

@Configuration
@EnableJpaRepositories(basePackages = "pl.kk.services.betexplorer.repository")
@Import({BasicConfiguration.class, OAuth2ResourceServerConfig.class, SwaggerConfiguration.class, JpaConfiguration.class, JobRunnerConfiguration.class})
public class MainConfiguration {

    @Bean
    @Qualifier("packageToScanSupplier")
    Supplier<String> packageToScanSupplier() {
        return () -> "pl.kk.services.betexplorer.model";
    }

    @Bean
    @Qualifier("swaggerPackagesSupplier")
    Supplier<String> swaggerPackagesSupplier() {
        return () -> "pl.kk.services.betexplorer.controller";
    }

    @Bean
    @Qualifier("swaggerApiInfoSupplier")
    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .version("1.0")
                .title("betexplorer API")
                .description("Documentation betexplorer API v1.0")
                .build();
    }
}