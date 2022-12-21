package encryptionClient;

import java.util.UUID;

import javax.crypto.SecretKey;

import encryptionLib.core.IKeyManager;
import nettyWrapper.core.Client;
import nettyWrapper.core.EncryptionWrapper;
import nettyWrapper.core.ServerCommand;

public class ServerBasedKeyManager implements IKeyManager {

	private final Client<UUID, SecretKey> client;

	public ServerBasedKeyManager(final int port, final String host, final EncryptionWrapper encryptionWrapper) {
		super();

		this.client = new Client<>(host, port, encryptionWrapper);
	}

	@Override
	public SecretKey getKey(final UUID uuid) throws Exception {
		return this.client.send(new ServerCommand<>(uuid)).get().getResponse();
	}

}
