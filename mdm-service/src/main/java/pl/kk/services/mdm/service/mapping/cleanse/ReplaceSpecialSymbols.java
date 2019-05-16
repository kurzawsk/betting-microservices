package pl.kk.services.mdm.service.mapping.cleanse;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class ReplaceSpecialSymbols implements CleanseFunction {

    private static final Map<String, String> DICTIONARY = ImmutableMap.<String, String>builder()
            .put("&", " AND ")
            .put("-", " ")
            .build();


    @Override
    public Optional<String> cleanse(String input) {
        for (Map.Entry<String, String> e : DICTIONARY.entrySet()) {
            input = input.replace(e.getKey(), e.getValue());

        }
        return Optional.of(removeAdditionalSpaces(input));
    }
}
