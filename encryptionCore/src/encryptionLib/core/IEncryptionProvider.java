package encryptionLib.core;

import java.util.UUID;

import javax.crypto.SecretKey;

public interface IEncryptionProvider {
	byte[] decrypt(byte[] data, SecretKey secret) throws Exception;

	byte[] decrypt(byte[] encryptedData, UUID uuid) throws Exception;

	byte[] encrypt(byte[] data, SecretKey secret) throws Exception;

	byte[] encrypt(byte[] data, UUID uuid) throws Exception;
}
