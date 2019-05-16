package pl.kk.services.mdm.config.mapping.cleanse;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.kk.services.mdm.service.mapping.cleanse.*;

import java.util.List;

@Configuration
public class CleanseFunctionsConfiguration {

    @Bean
    @Qualifier("full")
    List<CleanseFunction> fullCleanseFunctions() {
        return ImmutableList.of(
                new ReplaceSpecialSymbols(),
                new RemoveSpecialSymbols(),
                new StripAccents(),
                new UpperCase(),
                new ReplacePolishCitySynonyms()
        );
    }

    @Bean
    @Qualifier("basic")
    List<CleanseFunction> basicCleanseFunctions() {
        return ImmutableList.of(
                new UpperCase()
        );
    }
}
