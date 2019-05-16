package pl.kk.services.mdm.service.mapping.cleanse;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RemoveSpecialSymbols implements CleanseFunction {

    private static final String SPECIAL_SYMBOLS = "!\"#$%'()*+,./:;<=>?@[\\]^_`{|}~\u007F";

    @Override
    public Optional<String> cleanse(String input) {
        for (char c : SPECIAL_SYMBOLS.toCharArray()) {
            input = input.replace("" + c, "");
        }
        return Optional.of(removeAdditionalSpaces(input));
    }

}
