package encryptionClient;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import encryptionLib.core.IEncryptionProvider;
import nettyWrapper.core.Client;
import nettyWrapper.core.EncryptionWrapper;
import nettyWrapper.core.ServerCommand;

public class ServerBasedEncryptionProvider implements IEncryptionProvider {

	private final Client<UUID, SecretKey> client;
	private final EncryptionWrapper encryptionWrapper;

	public ServerBasedEncryptionProvider(final int port, final String server,
			final EncryptionWrapper encryptionWrapper) {
		super();
		Logger.getLogger("ServerBasedKeyManager");
		this.encryptionWrapper = encryptionWrapper;

		this.client = new Client<>(server, port, encryptionWrapper);

		// For CI/CD always local. sends request to server (local). External keys to be
		// set as one-off in pipeline
		// and rotate hourly/daily/random
	}

	@Override
	public byte[] decrypt(final byte[] data, final SecretKey secret) throws Exception {
		return this.encryptionWrapper.decrypt(data, secret);
	}

	@Override
	public byte[] decrypt(final byte[] encryptedData, final UUID uuid) throws Exception {
		return this.encryptionWrapper.decrypt(encryptedData, getKey(uuid));
	}

	@Override
	public byte[] encrypt(final byte[] data, final SecretKey secret) throws Exception {
		return this.encryptionWrapper.encrypt(data, secret);
	}

	@Override
	public byte[] encrypt(final byte[] data, final UUID uuid) throws Exception {
		return encrypt(data, getKey(uuid));
	}

	private SecretKey getKey(final UUID uuid) throws InterruptedException, ExecutionException, Exception {
		return this.client.send(new ServerCommand<>(uuid)).get().getResponse();
	}
}
