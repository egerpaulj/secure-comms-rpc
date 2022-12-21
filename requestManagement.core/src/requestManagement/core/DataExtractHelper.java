package requestManagement.core;

import java.util.concurrent.ThreadLocalRandom;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DataExtractHelper {

	public static Element getNextPageAnchor(final Document doc) {
		final Element pagingDiv = doc.select("div[class=paging]").first();
		if (pagingDiv == null)
			return null;
		final Element nextPageAnchor = pagingDiv.select("a[title=Next]").first();
		return nextPageAnchor;
	}

	public static long getRandom(final int min, final int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}

	public static float parseFloat(String data) {
		data = data.trim();
		String cleanData = "";
		for (int charIndex = 0; charIndex < data.length(); charIndex++) {
			final String processingChar = "" + data.charAt(charIndex);
			// allow the negative number sign to be included at the start
			final boolean isMinusChar = processingChar.equals("-");
			final boolean isPeriod = processingChar.equals(".");
			if ((cleanData.length() == 0) && (isMinusChar || isPeriod)) {
				cleanData += processingChar;
			}
			// only add decimals or period
			else if (isPeriod || processingChar.matches("[\\d]")) {
				cleanData += processingChar;
			}
		}

		if (cleanData.length() == 0)
			return 0.0f;

		return Float.parseFloat(cleanData);
	}
}
