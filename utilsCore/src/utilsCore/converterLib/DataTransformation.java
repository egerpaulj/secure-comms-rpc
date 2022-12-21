package utilsCore.converterLib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.file.SimpleFileVisitor;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DataTransformation {
	public static Object convert(final byte[] data) throws ClassNotFoundException, IOException {
		final ByteArrayInputStream bos = new ByteArrayInputStream(data);

		final ObjectInput in = new ObjectInputStream(bos);
		return in.readObject();
	}

	public static byte[] convert(final Object object) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		final ObjectOutput out = new ObjectOutputStream(bos);
		out.writeObject(object);
		out.flush();
		return bos.toByteArray();
	}

	public static long getLong(final byte[] data) {
		final int sizeOfLong = Long.BYTES / Byte.BYTES;

		final ByteBuffer buf = ByteBuffer.allocate(sizeOfLong).put(data, 0, sizeOfLong);
		buf.rewind();

		return buf.getLong();
	}

	public static void purge(final File file) {
		if (!file.exists())
			return;

		if (file.isFile()) {
			file.delete();
			return;
		}

		for (final File fileInDir : file.listFiles()) {
			if (fileInDir.isDirectory()) {
				DataTransformation.purge(fileInDir);
			}

			fileInDir.delete();
		}

		file.delete();
	}

	public static Object readFromFile(final Path filePath) throws Exception {
		final File readFile = filePath.toFile();

		if (!readFile.exists() || !readFile.canRead())
			throw new IOException("Unable to read file: " + filePath);

		return DataTransformation.convert(Files.readAllBytes(filePath));
	}

	public static File unzip(final Path target, final byte[] zipData) throws IOException {
		if (target.toFile().exists())
			throw new IOException("Unable to unzip, target file already exists: " + target.toString());

		final ByteArrayInputStream bin = new ByteArrayInputStream(zipData);
		final ZipInputStream zin = new ZipInputStream(bin);

		try {
			ZipEntry zipEntry = zin.getNextEntry();
			final byte[] buffer = new byte[1024];
			while (zipEntry != null) {
				final File file = new File(target.toFile(), zipEntry.getName());
				final File directory = file.getParentFile();
				directory.mkdirs();
				file.createNewFile();

				final FileOutputStream fout = new FileOutputStream(file);

				int len;
				while ((len = zin.read(buffer)) > 0) {
					fout.write(buffer, 0, len);
				}

				fout.close();
				zipEntry = zin.getNextEntry();
			}

			return target.toFile();
		} finally {
			zin.close();
			bin.close();
		}
	}

	public static void writeToFile(final Path filePath, final byte[] contents) throws IOException {
		final File outFile = filePath.toFile();

		if (outFile.exists()) {
			outFile.delete();
		}

		Files.write(filePath, contents, StandardOpenOption.CREATE_NEW);
	}

	public static void writeToFile(final Path filePath, final Object object) throws IOException {
		final File outFile = filePath.toFile();

		if (outFile.exists()) {
			outFile.delete();
		}

		Files.write(filePath, DataTransformation.convert(object), StandardOpenOption.CREATE_NEW);
	}

	public static byte[] zipFolder(final Path source) throws Exception {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final ZipOutputStream zout = new ZipOutputStream(bos);

		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attr) throws IOException {
				zout.putNextEntry(new ZipEntry(source.relativize(file).toString()));
				Files.copy(file, zout);
				zout.closeEntry();
				return FileVisitResult.CONTINUE;
			}
		});

		zout.flush();
		zout.close();

		return bos.toByteArray();
	}
}
