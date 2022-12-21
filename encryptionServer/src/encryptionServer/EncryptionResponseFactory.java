package encryptionServer;

import java.util.UUID;

import javax.crypto.SecretKey;

import encryptionLib.core.IKeyManager;
import nettyWrapper.core.IResponseFactory;
import nettyWrapper.core.ServerResponse;

public class EncryptionResponseFactory implements IResponseFactory<UUID, SecretKey> {

	private final IKeyManager keyManager;

	public EncryptionResponseFactory(final IKeyManager keyManager) {
		super();
		this.keyManager = keyManager;
	}

	@Override
	public ServerResponse<SecretKey> createResponse(final UUID request) throws Exception {
		return new ServerResponse<>(this.keyManager.getKey(request));
	}

}
