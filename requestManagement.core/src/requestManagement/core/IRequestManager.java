package requestManagement.core;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jsoup.nodes.Document;

public interface IRequestManager {
	public enum Driver {
		HtmlUnit, SeleniumChromium, SeleniumFirefox
	}

	/**
	 * Close all connection and stop the request management instance
	 */
	void close();

	/**
	 * Gets a basic Html Document from a uri
	 */
	Document get(String uri) throws Exception;

	/**
	 * Gets a Html Document by enabling Java script and loading the page with all
	 * java script Override with the possibility to perform some action(s) on the
	 * page - browse tabs, tables and collect data.
	 */
	Document getWithJavaScriptEnabled(String uri, Driver driver)
			throws MalformedURLException, IOException, InterruptedException;

}
