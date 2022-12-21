package nettWrapper.core.tests;

import org.junit.Test;

import nettyWrapper.core.EncryptionWrapper;
import nettyWrapper.core.Server;
import nettyWrapper.core.ServerResponse;

public class ServerTest {
	@Test
	public void StartServer() throws Exception {
		final EncryptionWrapper encWrapper = EncryptionWrapper.createSingleKeyEncryptionWrapper();
		// Server which creates a string response from request
		final Server<String, String> server = new Server<>(7080, encWrapper,
				request -> new ServerResponse<>("I am the response for request: " + request));

		server.start();

		System.in.read();

		server.shutdown();
	}
}
