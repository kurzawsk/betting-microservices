package pl.kk.services.mdm.service.mapping.cleanse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StripAccents implements CleanseFunction {
    @Override
    public Optional<String> cleanse(String input) {
        return Optional.of(StringUtils.stripAccents(input));
    }
}
