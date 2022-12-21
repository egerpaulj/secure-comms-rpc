package worker.camel;

import java.util.UUID;

import org.apache.camel.CamelContext;

public interface ICamelContextFactory {
	CamelContext createContext(UUID id);

	String listAll();

	void stopAll();

	void stopContext(UUID id);

}
