package encryptionLib.local;

import java.security.spec.KeySpec;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import encryptionLib.core.IKeyFactory;

public class KeyFactory implements IKeyFactory {

	// ToDo these should be provided on startup - container security.
	private final String p = "REMOVED";
	private final String s = "REMOVED";

	@Override
	public SecretKey generateKey(final UUID uuid) throws Exception {
		final SecretKeyFactory factory = SecretKeyFactory.getInstance("REMOVED");
		final KeySpec spec = new PBEKeySpec(this.p.toCharArray(), this.s.getBytes(), 65536, 128);
		final SecretKey tmp = factory.generateSecret(spec);
		final SecretKey key = new SecretKeySpec(tmp.getEncoded(), "AES");

		return key;
	}
}
