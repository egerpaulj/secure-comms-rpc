package encryptionLib.core;

import java.util.UUID;

import javax.crypto.SecretKey;

public interface IKeyManager {
	SecretKey getKey(UUID uuid) throws Exception;
}
