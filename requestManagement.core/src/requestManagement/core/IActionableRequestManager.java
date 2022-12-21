package requestManagement.core;

import java.io.Serializable;
import java.util.function.Function;

import requestManagement.core.IRequestManager.Driver;

/**
 * Perform a request on a web agent and apply functions to the page. The
 * function should collect all the required data and return the data.
 * 
 * @author egerpaul
 *
 * @param <I> The input from the Uri (e.g. Html page, specific Driver Objects)
 * @param <O> The collected data O - Should be serializable
 */
public interface IActionableRequestManager<I, O extends Serializable> {
	O getWithJavaScriptEnabled(String uri, Driver driver, Function<I, O> actionFunction);
}
