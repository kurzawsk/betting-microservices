package pl.kk.services.betexplorer.service.web.parser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import org.springframework.stereotype.Component;
import pl.kk.services.common.datamodel.dto.mdm.SetMatchResultDTO;
import pl.kk.services.common.misc.RegexUtil;

import java.util.List;
import java.util.Map;

@Component
public class MatchResultParser extends HtmlParser<Map<String, String[]>> {

    private static final String PATTERN_MATCH_RESULT = ".*?href=\"(.*?)\">.*?result\"><strong>(.*?)</strong>.*?colspan=\"3\">\\({0,1}(.*?)\\){0,1}<.*?";
    private static final String PATTERN_MATCH_RESULT_PRE = "^<td class=\"table-main__[t].*?$";
    private static final String PATTERN_NORMAL_RESULT = "^(\\d+):(\\d+)$";
    private static final String PATTERN_PENALTIES = "After Penalties";
    private static final String PATTERN_EXTRA_TIME = "After Extra Time";
    private static final String PATTERN_POSTPONED = "Postponed";
    private static final List<String> PATTERNS_CANCELED = ImmutableList.of("Canceled", "Awarded", "Abandoned", "Interrupted", "Delayed");

    @Override
    public Map<String, String[]> parse(String content) {
        Map<String, String[]> result = Maps.newHashMap();

        String[] allLines = content.split("\n");
        List<String> filteredLines = Lists.newArrayList();
        for (int i = 0; i < allLines.length; i++) {
            if (allLines[i].matches(PATTERN_MATCH_RESULT_PRE)) {
                filteredLines.add(allLines[i] +
                        allLines[i + 1] +
                        allLines[i + 2] +
                        allLines[i + 3]);
            }
        }
        for (String line : filteredLines) {

            String[] matchValues = RegexUtil.extractValuesFromRegex(PATTERN_MATCH_RESULT, line);

            if (matchValues.length == 3) {
                String[] identifierValues = matchValues[0].substring(0, matchValues[0].length() - 1).split("/");
                String identifier = identifierValues[identifierValues.length - 1];
                String finalResult = matchValues[1];
                String[] normalScore = RegexUtil.extractValuesFromRegex(PATTERN_NORMAL_RESULT, finalResult);
                if (normalScore.length == 2) {
                    result.put(identifier, new String[]{normalScore[0], normalScore[1]});
                } else if (finalResult.contains(PATTERN_POSTPONED)) {
                    result.put(identifier, new String[]{SetMatchResultDTO.AbnormalMatchFinishReason.POSTPONED.toString()});
                } else if (finalResult.contains(PATTERN_PENALTIES) || finalResult.contains(PATTERN_EXTRA_TIME)) {

                    String[] halfResults = matchValues[2].split(", ");
                    if (halfResults.length >= 2) {

                        String[] firstHalfResult = RegexUtil.extractValuesFromRegex(PATTERN_NORMAL_RESULT, halfResults[0]);
                        String[] secondHalfResult = RegexUtil.extractValuesFromRegex(PATTERN_NORMAL_RESULT, halfResults[1]);

                        Integer homeFirstHalfScore = Ints.tryParse(firstHalfResult[0]);
                        Integer homeSecondHalfScore = Ints.tryParse(secondHalfResult[0]);
                        Integer awayFirstHalfScore = Ints.tryParse(firstHalfResult[1]);
                        Integer awaySecondHalfScore = Ints.tryParse(secondHalfResult[1]);

                        if (homeFirstHalfScore != null && homeSecondHalfScore != null && awayFirstHalfScore != null
                                && awaySecondHalfScore != null) {
                            result.put(identifier, new String[]{homeFirstHalfScore + homeSecondHalfScore + "", awayFirstHalfScore + awaySecondHalfScore + ""});
                        }
                    }
                } else if (PATTERNS_CANCELED.stream().anyMatch(finalResult::contains)) {
                    result.put(identifier, new String[]{SetMatchResultDTO.AbnormalMatchFinishReason.CANCELLED.toString()});
                }
            }
        }

        return result;
    }

}
