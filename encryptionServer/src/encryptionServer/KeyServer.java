package encryptionServer;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import encryptionCore.external.EncryptionProvider;
import encryptionLib.core.IEncryptionProvider;
import encryptionLib.core.IKeyManager;
import encryptionLib.filebasedEncryptionLib.LocalFileBasedKeyManager;
import encryptionLib.local.KeyFactory;
import nettyWrapper.core.EncryptionWrapper;
import nettyWrapper.core.IResponseFactory;
import nettyWrapper.core.Server;

public class KeyServer {

	public static void main(final String[] args) throws InterruptedException, IOException {

		final Logger logger = Logger.getLogger("KeyServerMain");
		int port = 7080;
		String path = "/keys/";
		logger.log(Level.INFO, "Starting Server");

		try {
			if ((args != null) && (args.length == 2)) {
				logger.log(Level.INFO, "Parsing Arguments 1 (Port): " + args[0]);
				port = Integer.parseInt(args[0]);

				logger.log(Level.INFO, "Key Path: " + args[1]);
				path = args[1];
			}
		} catch (final Exception ex) {
			logger.log(Level.SEVERE, "Error parsing arguments:");
			logger.log(Level.SEVERE, "Usage: arg1: port(int), arg2: keyPath(string)");
			return;
		}

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

	private final Server<UUID, SecretKey> server;

	public KeyServer(final int port, final EncryptionWrapper encryptionWrapper,
			final IResponseFactory<UUID, SecretKey> responseFactory) {
		this.server = new Server<>(port, encryptionWrapper, responseFactory);
	}

	public void start() throws InterruptedException {
		this.server.start();
	}

	public void stop() throws InterruptedException {
		this.server.stop();
	}

}
