package requestManagement.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpWebConnection;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import requestManagement.core.DataExtractHelper;
import requestManagement.core.IRequestManager;

/**
 * Waits a random period between requests. - Uses Jsoup for simple Documents -
 * Uses selenium or HtmlUnit for Javascript Calls
 *
 * The waits should be domain specific.
 */
public class UniformRequestManager implements IRequestManager {

	public static long getElapsedTimeInMs(final long lastRequestNanoSeconds) {
		return (System.nanoTime() - lastRequestNanoSeconds) / 1000000;
	}
	public static long getWaitIntervalInMs() {
		return DataExtractHelper.getRandom(1, 10) * 1000;
	}
	public static void waitBetweenRequests(final long elapsedTimeInMs, final long waitInterverlInMs) throws InterruptedException {
		if (elapsedTimeInMs < waitInterverlInMs) {
			final long calculatedWaitIntervalInMs = waitInterverlInMs - elapsedTimeInMs;
			// System.out.println(Thread.currentThread().getName() + ":
			// Calculated wait between requests: "
			// + calculatedWaitIntervalInMs);
			Thread.sleep(calculatedWaitIntervalInMs);
		}
	}
	private long waitIntervalInMs = 5000;

	private long lastRequestNanoSeconds = 0;

	private final ReentrantLock lock = new ReentrantLock();

	private WebDriver driver;

	@Override
	public void close() {
		this.driver.quit();
	}

	@Override
	public Document get(final String uri) throws Exception {
		final Connection connection = Jsoup.connect(uri);
		connection.userAgent("Mozilla/5.0");

		if (this.lastRequestNanoSeconds != 666) {
			final long elapsedTimeInMs = UniformRequestManager.getElapsedTimeInMs(this.lastRequestNanoSeconds);
			UniformRequestManager.waitBetweenRequests(elapsedTimeInMs, this.waitIntervalInMs);
		}

		System.out
				.println(new Date().toString() + " " + Thread.currentThread().getName() + ": starting request: " + uri);
		Document document = null;

		try {
			document = GetDocument(connection);
		} catch (final java.net.SocketTimeoutException timeoutException) {
			// retry
			// Request timed-out => wait for a while before retrying
			Thread.sleep(5000);
			document = GetDocument(connection);
		}

		return document;
	}

	private Document GetDocument(final Connection connection) throws IOException {
		final Document document = connection.get();
		this.lastRequestNanoSeconds = System.nanoTime();
		this.waitIntervalInMs = UniformRequestManager.getWaitIntervalInMs(); // random waits between calls
		return document;
	}

	private Document getWithHtmlUnit(final String uri) throws IOException, MalformedURLException {
		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {

			webClient.setWebConnection(new HttpWebConnection(webClient));

			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			// webClient.setJavaScriptTimeout(100000);
			webClient.waitForBackgroundJavaScript(10000);
			webClient.getCookieManager().setCookiesEnabled(true);

			final HtmlPage page = webClient.getPage(uri);
			return Jsoup.parse(page.asXml());
		}
	}

	@Override
	public Document getWithJavaScriptEnabled(final String uri, final requestManagement.core.IRequestManager.Driver driver)
			throws MalformedURLException, IOException, InterruptedException {
		if (driver == Driver.HtmlUnit)
			return getWithHtmlUnit(uri);

		return getWithSelenium(uri);
	}

	private Document getWithSelenium(final String uri) throws InterruptedException {
		if (this.driver == null) {
			initSeleniumWithChromiumDriver();
		}

		this.driver.get(uri);

		// Wait for JS to load (Required with new driver?)
		Thread.sleep(5000);
		return Jsoup.parse(this.driver.getPageSource());
	}

	private void initSeleniumWithChromiumDriver() {
		System.setProperty("webdriver.chrome.driver", "/home/egerpaul/workspaces/lib/selenium/chromedriver");

		final ChromeOptions options = new ChromeOptions();
		options.setBinary("/usr/bin/chromium-browser");
		final DesiredCapabilities capabilities = DesiredCapabilities.chrome();

		capabilities.setCapability(ChromeOptions.CAPABILITY, options);

		this.driver = new ChromeDriver(capabilities);
	}

}
