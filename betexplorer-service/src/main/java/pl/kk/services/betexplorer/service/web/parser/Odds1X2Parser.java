package pl.kk.services.betexplorer.service.web.parser;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;
import pl.kk.services.common.misc.RegexUtil;

import java.util.Map;

@Component
public class Odds1X2Parser extends HtmlParser<Map<String, String[]>> {

    private static final String PATTERN_BOOKMAKERS = ".*?href=.*?bookmaker\\\\/(\\d+)\\\\/https{0,1}:\\\\/\\\\/(.*?)[/\\\\?].*?data-odd=\\\\\"(.{4,7}?)\\\\\".*?data-odd=\\\\\"(.{4,7}?)\\\\\".*?data-odd=\\\\\"(.{4,7}?)\\\\\".*?";
    private static final String PATTERN_BOOKMAKER_AVERAGE = ".*?Average odds.*?data-odd=\\\\\"(.{4,7}?)\\\\\".*?data-odd=\\\\\"(.{4,7}?)\\\\\".*?data-odd=\\\\\"(.{4,7}?)\\\\\".*?";
    private static final String AVERAGE_BOOKMAKER_NAME = "Average";

    @Override
    public Map<String, String[]> parse(String content) {
        Map<String, String[]> result = Maps.newHashMap();
        String[] pageContent = content.split("<a ");
        for (String row : pageContent) {

            String[] matchValues = RegexUtil.extractValuesFromRegex(PATTERN_BOOKMAKERS, row);
            String[] avgMatchValues = RegexUtil.extractValuesFromRegex(PATTERN_BOOKMAKER_AVERAGE, row);

            if (matchValues.length == 5) {
                result.put(matchValues[0], new String[]{matchValues[2],
                        matchValues[3], matchValues[4]});
            }
            if (avgMatchValues.length == 3) {
                result.put(AVERAGE_BOOKMAKER_NAME,
                        new String[]{avgMatchValues[0], avgMatchValues[1], avgMatchValues[2]});
            }

        }

        return result;
    }

}
