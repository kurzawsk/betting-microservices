package pl.kk.services.mdm.service.mapping.cleanse;

import java.util.Optional;

/**
 * Created by KK on 2017-07-28.
 */
@FunctionalInterface
public interface CleanseFunction {
    default String removeAdditionalSpaces(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }

    Optional<String> cleanse(String input);
}
