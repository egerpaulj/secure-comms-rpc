package encryptionCore.external;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import encryptionLib.core.IEncryptionProvider;
import encryptionLib.core.IKeyManager;

public class EncryptionProvider implements IEncryptionProvider {

	private static Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException {
		final Cipher cipher = Cipher.getInstance("AES");
		return cipher;
	}

	private final HashMap<UUID, SecretKey> uuidToKeyMap = new HashMap<>();

	IKeyManager keyManager = null;

	public EncryptionProvider(final IKeyManager keyManager) {
		this.keyManager = keyManager;
	}

	@Override
	public byte[] decrypt(final byte[] data, final SecretKey secret) throws Exception {
		final Cipher cipher = EncryptionProvider.getCipher();
		cipher.init(Cipher.DECRYPT_MODE, secret);
		return cipher.doFinal(data);
	}

	@Override
	public byte[] decrypt(final byte[] encryptedData, final UUID uuid) throws Exception {
		if (!this.uuidToKeyMap.containsKey(uuid)) {
			this.uuidToKeyMap.put(uuid, this.keyManager.getKey(uuid));
		}

		final Cipher cipher = EncryptionProvider.getCipher();
		cipher.init(Cipher.DECRYPT_MODE, this.uuidToKeyMap.get(uuid));
		return cipher.doFinal(encryptedData);
	}

	@Override
	public byte[] encrypt(final byte[] data, final SecretKey secret) throws Exception {
		final Cipher cipher = EncryptionProvider.getCipher();

		cipher.init(Cipher.ENCRYPT_MODE, secret);
		return cipher.doFinal(data);
	}

	@Override
	public byte[] encrypt(final byte[] data, final UUID uuid) throws Exception {
		if (!this.uuidToKeyMap.containsKey(uuid)) {
			SecretKey key = null;

			// If the key is does not exist or corrupt will throw an error.
			key = this.keyManager.getKey(uuid);
			this.uuidToKeyMap.put(uuid, key);
		}

		final Cipher cipher = EncryptionProvider.getCipher();

		cipher.init(Cipher.ENCRYPT_MODE, this.uuidToKeyMap.get(uuid));
		return cipher.doFinal(data);

	}

}
