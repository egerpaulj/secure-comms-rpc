package encryptionServer.tests;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import org.junit.Test;

import encryptionCore.external.EncryptionProvider;
import encryptionLib.core.IEncryptionProvider;
import encryptionLib.core.IKeyManager;
import encryptionLib.filebasedEncryptionLib.LocalFileBasedKeyManager;
import encryptionLib.local.KeyFactory;
import encryptionServer.EncryptionResponseFactory;
import encryptionServer.KeyServer;
import nettyWrapper.core.Client;
import nettyWrapper.core.EncryptionWrapper;
import nettyWrapper.core.IResponseFactory;
import nettyWrapper.core.ServerCommand;
import nettyWrapper.core.ServerResponse;

public class EncryptionServerTests {

	@Test
	public void requestKey() throws Exception {
		final Logger logger = Logger.getLogger("KeyServerTest");
		final String path = "/keys/";

		final IKeyManager keyManager = new LocalFileBasedKeyManager(path, new KeyFactory());
		new EncryptionProvider(keyManager);

		final Client<UUID, SecretKey> client = new Client<>("localhost", 7080,
				new EncryptionWrapper(new EncryptionProvider(new LocalFileBasedKeyManager(new KeyFactory()))));

		final UUID guid = UUID.fromString("ca5765d6-e8cd-42e3-b62a-bc608fae49ca");
		final Future<ServerResponse<SecretKey>> response = client.send(new ServerCommand<>(guid));

		// response.wait();

		final SecretKey secretResponse = response.get().getResponse();

		logger.log(Level.INFO, "Key returned from server: " + secretResponse.toString());
	}

	@Test
	public void startServer() throws InterruptedException, IOException {
		final Logger logger = Logger.getLogger("KeyServerTest");
		final int port = 7080;
		final String path = "/keys/";

		final IKeyManager keyManager = new LocalFileBasedKeyManager(path, new KeyFactory());
		final IEncryptionProvider encryptionProvider = new EncryptionProvider(keyManager);
		final EncryptionWrapper encryptionWrapper = new EncryptionWrapper(encryptionProvider);
		final IResponseFactory<UUID, SecretKey> responseFactory = new EncryptionResponseFactory(keyManager);

		final KeyServer server = new KeyServer(port, encryptionWrapper, responseFactory);
		server.start();

		logger.log(Level.INFO, "Hit any key to exit server");

		System.in.read();

		server.stop();
	}
}
