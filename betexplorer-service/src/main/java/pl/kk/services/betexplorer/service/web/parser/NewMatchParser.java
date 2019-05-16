package pl.kk.services.betexplorer.service.web.parser;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;
import pl.kk.services.common.misc.RegexUtil;

import java.util.Map;

@Component
public class NewMatchParser extends HtmlParser<Map<String, String[]>> {

    private static final String PATTERN_TEAM_NAMES = ".*?table-main__time\">(.*?)</span><a href=\"(.*?)/\">(.*?) - (.*?)</a></td>.*?";
    private static final String PATTERN_MATCH_DAY = ".*?\"table-main__date\">(.*?)</th></tr>.*?";

    @Override
    public Map<String, String[]> parse(String content) {
        Map<String, String[]> result = Maps.newHashMap();
        String currentMatchDay = null;
        for (String line : content.split("\n")) {
            String[] matchValues = RegexUtil.extractValuesFromRegex(PATTERN_TEAM_NAMES, line);
            String[] matchDay = RegexUtil.extractValuesFromRegex(PATTERN_MATCH_DAY, line);

            if (matchDay.length == 1) {
                currentMatchDay = matchDay[0];
            }

            if (matchValues.length == 4 && currentMatchDay != null) {
                String[] timeArray = matchValues[0].split(":");
                String[] dateArray = currentMatchDay.split("\\.");
                String matchIdentifier = matchValues[1].substring(matchValues[1].lastIndexOf("/") + 1,
                        matchValues[1].length());
                result.put(matchIdentifier, new String[]{matchValues[2], matchValues[3], dateArray[0],
                        dateArray[1], dateArray[2], timeArray[0], timeArray[1]});
            }


        }
        return result;
    }

}
