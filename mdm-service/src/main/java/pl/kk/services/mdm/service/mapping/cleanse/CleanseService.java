package pl.kk.services.mdm.service.mapping.cleanse;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CleanseService {

    private final List<CleanseFunction> basicCleanseFunctions;
    private final List<CleanseFunction> fullCleanseFunctions;

    public CleanseService(@Qualifier("basic") List<CleanseFunction> basicCleanseFunctions,
                          @Qualifier("full") List<CleanseFunction> fullCleanseFunctions) {
        this.basicCleanseFunctions = basicCleanseFunctions;
        this.fullCleanseFunctions = fullCleanseFunctions;
    }

    public String cleanseBasic(String input) {
        return cleanse(input, basicCleanseFunctions);
    }

    public Set<String> cleanseFullAndTokenize(String input) {
        return tokenize(cleanseFull(input));
    }

    private Set<String> tokenize(String input) {
        return ImmutableSet.copyOf(StringUtils.split(input));
    }

    private String cleanseFull(String input) {
        return cleanse(input, fullCleanseFunctions);
    }

    private String cleanse(String input, List<CleanseFunction> cleanseFunctions) {
        Optional<String> result = Optional.of(input);
        for (CleanseFunction cf : cleanseFunctions) {
            result = result.flatMap(cf::cleanse);
        }
        return result.orElseThrow(() -> new IllegalArgumentException("Team name cannot be null"));
    }


}
