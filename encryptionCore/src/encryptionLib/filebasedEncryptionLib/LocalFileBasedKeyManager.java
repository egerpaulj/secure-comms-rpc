package encryptionLib.filebasedEncryptionLib;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.crypto.SecretKey;
import encryptionLib.core.IKeyFactory;
import encryptionLib.core.IKeyManager;
import utilsCore.converterLib.DataTransformation;

public class LocalFileBasedKeyManager implements IKeyManager {

	// ToDo Configuration service
	private final String keyStoragePath;

	private final IKeyFactory keyFactory;

	public LocalFileBasedKeyManager(final IKeyFactory keyFactory) {
		this("/keys/", keyFactory);
	}

	public LocalFileBasedKeyManager(final String path, final IKeyFactory keyFactory) {
		this.keyStoragePath = path;
		this.keyFactory = keyFactory;
	}

	@Override
	public SecretKey getKey(final UUID uuid) throws Exception {
		final Path path = getKeyPath(uuid);
		SecretKey key;

		if (!path.toFile().exists()) {
			key = this.keyFactory.generateKey(uuid);
			DataTransformation.writeToFile(getKeyPath(uuid), key);
		} else {
			key = (SecretKey) DataTransformation.readFromFile(getKeyPath(uuid));
		}

		return key;
	}

	private Path getKeyPath(final UUID uuid) {
		return Paths.get(this.keyStoragePath + uuid.toString());
	}

}
