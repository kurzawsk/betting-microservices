package pl.kk.services.mdm.service.mapping.cleanse;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UpperCase implements CleanseFunction {
    @Override
    public Optional<String> cleanse(String input) {
        return Optional.of(input.toUpperCase());
    }
}
