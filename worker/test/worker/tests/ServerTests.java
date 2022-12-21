package worker.tests;

import org.junit.Test;

import nettyWrapper.core.EncryptionWrapper;
import worker.camel.CamelContextFactory;
import worker.server.WorkerServer;

public class ServerTests {

	private WorkerServer createServer() throws Exception {
		return new WorkerServer(7080, EncryptionWrapper.createSingleKeyEncryptionWrapper(), new CamelContextFactory());
	}

	@Test
	public void startServer() throws Exception {
		final WorkerServer server = createServer();

		server.start();

		//System.out.println("Press any key to end.");
		//System.in.read();

		server.stop();
	}

	public static void main(String [] args) throws Exception{
		new ServerTests().startServer();
	}

}
