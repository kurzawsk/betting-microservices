package pl.kk.services.gateway.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.kk.services.common.config.SecureEurekaClientConfiguration;

@Configuration
@Import({SecureEurekaClientConfiguration.class})
public class MainConfiguration {

}
