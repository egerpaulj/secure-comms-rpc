package encryptionLib.core;

import java.util.UUID;

import javax.crypto.SecretKey;

public interface IKeyFactory {

	SecretKey generateKey(UUID uuid) throws Exception;

}