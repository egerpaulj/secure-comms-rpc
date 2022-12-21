package nettWrapper.core.tests;

import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.springframework.util.Assert;

import nettyWrapper.core.Client;
import nettyWrapper.core.EncryptionWrapper;
import nettyWrapper.core.ServerCommand;
import nettyWrapper.core.ServerResponse;

public class ClientTest {
	@Test
	public void sendStringMessageToStringServer() throws Exception {
		final Client<String, String> client = new Client<>("localhost", 7080,
				EncryptionWrapper.createSingleKeyEncryptionWrapper());

		final Future<ServerResponse<String>> response = client.send(new ServerCommand<>("** REQUEST **"));

		// response.wait();

		final String responseStr = response.get().getResponse();

		Logger.getLogger("Unit test logger").log(Level.INFO, "Test response is: " + responseStr);

		Assert.isTrue(responseStr != null);
	}

	@Test
	public void loadTest() throws Exception {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		StopWatch parentSw = StopWatch.createStarted();
		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(
			new Runnable() {
				
				@Override
				public void run() {
					try {
						StopWatch sw = StopWatch.createStarted();
						sendStringMessageToStringServer();
						System.out.println("Duration in ms: " + sw.getTime());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
			
			t.run();
			
			threads.add(t);
		}
		
		for(Thread t : threads) {
			//t.wait();
			if(t.isAlive())
				t.wait();
		}
		
		System.out.println("Duration for all to complete in ms: " + parentSw.getTime());

	}
}
