package nettyWrapper.core;

import java.util.UUID;

import javax.crypto.SecretKey;

import encryptionCore.external.EncryptionProvider;
import encryptionCore.external.SingleKeyManager;
import encryptionLib.core.IEncryptionProvider;

public class EncryptionWrapper {

	public static EncryptionWrapper createSingleKeyEncryptionWrapper() throws Exception {
		return new EncryptionWrapper(new EncryptionProvider(new SingleKeyManager(
				SingleKeyManager.loadKey("/home/user/keys/e3b41dd4-fe87-4982-9b9c-06b6523ca98e", false))));
	}
	public static EncryptionWrapper createSingleKeyEncryptionWrapper(final String key, final boolean deleteOnLoad)
			throws Exception {
		return new EncryptionWrapper(
				new EncryptionProvider(new SingleKeyManager(SingleKeyManager.loadKey(key, deleteOnLoad))));
	}

	private final IEncryptionProvider encryptionProvider;

	private final UUID uuid;

	public EncryptionWrapper(final IEncryptionProvider encryptionProvider) {
		super();
		this.encryptionProvider = encryptionProvider;
		this.uuid = UUID.fromString("e3b41dd4-fe87-4982-9b9c-06b6523ca98e");
	}

	public EncryptionWrapper(final IEncryptionProvider encryptionProvider, final UUID uuid) {
		super();
		this.encryptionProvider = encryptionProvider;
		this.uuid = uuid;
	}

	public byte[] decrypt(final byte[] encryptedData) throws Exception {
		return this.encryptionProvider.decrypt(encryptedData, this.uuid);
	}

	public byte[] decrypt(final byte[] data, final SecretKey secret) throws Exception {
		return this.encryptionProvider.decrypt(data, secret);
	}

	public byte[] encrypt(final byte[] data) throws Exception {
		return this.encryptionProvider.encrypt(data, this.uuid);
	}

	public byte[] encrypt(final byte[] data, final SecretKey secret) throws Exception {
		return this.encryptionProvider.encrypt(data, secret);
	}
}
