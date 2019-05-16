package pl.kk.services.common.config;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

@EnableSwagger2
@Configuration
@Profile("!prod")
public class SwaggerConfiguration extends ResourceServerConfigurerAdapter implements WebMvcConfigurer {

    @Resource
    private Environment environment;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs/**", "/webjars/**").permitAll();
    }

    @Bean
    @Autowired
    public Docket swaggerApi(@Qualifier("swaggerPackagesSupplier") Supplier<String> swaggerPackagesSupplier,
                             ApiInfo apiInfo) {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping(environment.getProperty("security.oauth2.client.client-id"))
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerPackagesSupplier.get()))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(ImmutableList.of(securityScheme()))
                .securityContexts(Collections.singletonList(securityContext()))
                .apiInfo(apiInfo);
    }

    private SecurityScheme securityScheme() {
        ResourceOwnerPasswordCredentialsGrant grantType = new ResourceOwnerPasswordCredentialsGrant(environment.getProperty("security.oauth2.client.access-token-uri"));
        return new OAuthBuilder().name("spring_oauth")
                .grantTypes(ImmutableList.of(grantType))
                .scopes(Collections.emptyList())
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(ImmutableList.of(new SecurityReference("spring_oauth", scopes())))
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }

    private AuthorizationScope[] scopes() {
        return new AuthorizationScope[0];
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                .clientId(environment.getProperty("security.oauth2.client.client-id"))
                .clientSecret(environment.getProperty("security.oauth2.client.client-secret"))
                .scopeSeparator("read, write").build();
    }
}
