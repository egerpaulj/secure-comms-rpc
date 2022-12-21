package worker.client;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.InvalidPathException;
import java.util.UUID;

import utilsCore.converterLib.DataTransformation;

public class Result {
	private final UUID id;
	private final byte[] result;
	private final byte[] attachments;

	public Result(final UUID uuid, final byte[] result, final byte[] attachments) {
		super();
		this.id = uuid;
		this.result = result;
		this.attachments = attachments;
	}

	public File extractAttachments() throws IOException {
		final File file = new File(this.id.toString());

		return extractAttachmentsTo(file);
	}

	public File extractAttachmentsTo(final File targetDirectory) throws IOException {
		if (!targetDirectory.isDirectory())
			throw new InvalidPathException(targetDirectory.getAbsolutePath(), "Path is not a directory");

		return DataTransformation.unzip(targetDirectory.toPath(), this.attachments);
	}

	public byte[] getAttachments() {
		return this.attachments;
	}

	public byte[] getRestul() {
		return this.result;
	}

	public String getResultAsString() {
		return new String(this.result, Charset.defaultCharset());
	}

}
