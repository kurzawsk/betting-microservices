package pl.kk.services.common.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	public static String[] extractValuesFromRegex(String regex, String sourceStr) {

		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(sourceStr);

		if (matcher.matches()) {
			String[] result = new String[matcher.groupCount()];

			for (int i = 0; i < matcher.groupCount(); i++) {
				result[i] = matcher.group(i + 1);
			}
			return result;

		} else {
			return new String[0];
		}

	}
}
