package encryptionCore.external;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

import javax.crypto.SecretKey;

import encryptionLib.core.IKeyManager;
import utilsCore.converterLib.DataTransformation;

public class SingleKeyManager implements IKeyManager {

	public static SecretKey loadKey(final String path, final boolean deleteOnLoad) throws Exception {
		final File keyPath = new File(path);

		if (!keyPath.exists())
			throw new Exception("File does not exist: " + path);

		final SecretKey key = (SecretKey) DataTransformation.readFromFile(Paths.get(path));

		if (deleteOnLoad) {
			keyPath.delete();
		}

		return key;
	}

	private final SecretKey secretKey;

	public SingleKeyManager(final SecretKey secretKey) {
		super();
		this.secretKey = secretKey;
	}

	@Override
	public SecretKey getKey(final UUID uuid) throws Exception {
		return this.secretKey;
	}
}
