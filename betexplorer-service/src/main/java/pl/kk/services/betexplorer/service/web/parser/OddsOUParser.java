package pl.kk.services.betexplorer.service.web.parser;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.springframework.stereotype.Component;
import pl.kk.services.common.misc.RegexUtil;

@Component
public class OddsOUParser extends HtmlParser<Table<String, String, String[]>> {

    private static final String OU_TYPE_PATTERN = ".*?<td class=.*?table-main__doubleparameter.*?>(.*?)<.*?";
    private static final String ODDS_VALUE_PATTERN = ".*?table-main__detail-odds.*?data-odd=.\"(.*?).\".*?";
    private static final String BOOKMAKER_PATTERN = ".*?href=.\"./bookmaker./(.*?)./http.*?";
    private static final String AVERAGE_PATTERN = ".*?only.\" colspan=.\"\\d.\">(Average odds).*?";
    private static final String AVERAGE_BOOKMAKER_NAME = "Average";

    @Override
    public Table<String, String, String[]> parse(String content) {
        Table<String, String, String[]> result = HashBasedTable.create();
        String[] lines = content.split("td>");

        for (int i = 0; i < lines.length - 4; i++) {
            String[] ouType = RegexUtil.extractValuesFromRegex(OU_TYPE_PATTERN, lines[i]);
            if (ouType.length == 1) {
                String[] oddOver = RegexUtil.extractValuesFromRegex(ODDS_VALUE_PATTERN, lines[i + 1]);
                String[] oddUnder = RegexUtil.extractValuesFromRegex(ODDS_VALUE_PATTERN, lines[i + 2]);

                if (oddOver.length == 1 && oddUnder.length == 1) {
                    String[] bookmaker = RegexUtil.extractValuesFromRegex(BOOKMAKER_PATTERN, lines[i + 3]);
                    String[] average = RegexUtil.extractValuesFromRegex(AVERAGE_PATTERN, lines[i + 3]);
                    if (bookmaker.length == 1) {
                        result.put(bookmaker[0], ouType[0], new String[]{oddOver[0], oddUnder[0]});
                    } else if (average.length == 1) {
                        result.put(AVERAGE_BOOKMAKER_NAME, ouType[0], new String[]{oddOver[0], oddUnder[0]});
                    }
                }
            }
        }
        return result;
    }

}
