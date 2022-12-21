package worker.tests;

import java.nio.file.Paths;
import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Test;

import encryptionCore.external.SingleKeyManager;
import encryptionLib.core.IKeyManager;
import nettyWrapper.core.EncryptionWrapper;
import utilsCore.converterLib.DataTransformation;
import worker.client.Result;
import worker.client.WorkerClient;

public class ClientTests {

	private final EncryptionWrapper encProvider;
	private final IKeyManager keyManager;

	public ClientTests() throws Exception {

		final SecretKey key = (SecretKey) DataTransformation
				.readFromFile(Paths.get("/home/user/keys/e3b41dd4-fe87-4982-9b9c-06b6523ca98e.bck"));

		this.keyManager = new SingleKeyManager(key);
		this.encProvider = EncryptionWrapper
				.createSingleKeyEncryptionWrapper("/home/user/keys/e3b41dd4-fe87-4982-9b9c-06b6523ca98e.bck", false);
	}

	private WorkerClient createClient() {
		return new WorkerClient("localhost", 9090, this.keyManager, this.encProvider);
	}

	@Test
	public void executeJar() throws Exception {
		final WorkerClient client = createClient();
		final Result result = client.executeJar("/home/user/workspaces/networkAnalyser/CamServeillance.jar", false,
				true);

		Assert.assertNotNull(result);
		System.out.println(result.getResultAsString());
	}

	@Test
	public void executeStrCommand() throws Exception {
		final WorkerClient client = createClient();

		final String result = client.executeCommand("netstat -atp", true).getResultAsString();

		System.out.println(result);
		Assert.assertNotNull(result);
	}

	@Test
	public void executeStrCommand_lsof() throws Exception {
		final WorkerClient client = createClient();

		final String result = client.executeCommand("lsof -i", true).getResultAsString();

		Assert.assertNotNull(result);
		System.out.println(result);

	}

	@Test
	public void executeStrCommandWithAttachments() throws Exception {
		final String command = "python -c \"file=open('output/test','w')\"";

		final WorkerClient client = createClient();

		final Result result = client.executeBinary(command.getBytes(), false, true);

		Assert.assertNotNull(result);
		System.out.println(result.getResultAsString());
		Assert.assertNotNull(result.getAttachments());
		Assert.assertFalse(result.getAttachments().length == 0);
	}

	@Test
	public void executeStrCommandWithOutput() throws Exception {
		final WorkerClient client = createClient();

		final Result result = client.executeCommand("echo 'testing' > test", true);

		Assert.assertNotNull(result);
		System.out.println(result.getResultAsString());
	}

	@Test
	public void listCamels() throws Exception {
		final WorkerClient client = createClient();
		final String result = client.listCamelContexts().getResultAsString();
		System.out.println(result);
	}

	@Test
	public void startCamelContextTest() throws Exception {
		final WorkerClient client = createClient();
		client.startCamelContext(
				// "file:/home/user/workspaces/vendettaCore/worker/test/testData/camel/in?delay=1000",
				// "file:/home/user/workspaces/vendettaCore/worker/test/testData/camel/out", 1);
				"file:camel/in?delay=1000", "file:camel/out", 1);

	}
	
	@Test
	public void stopAllCamelContextsTest() throws Exception {
		final WorkerClient client = createClient();
		client.stopAllCamelContexts();
	}

}