package utilsCore.test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Assert;
import org.junit.Test;

import utilsCore.converterLib.DataTransformation;

public class DataTransformationTest {
	private final String zipFile = "result.zip";

	@Test
	public void unzipFolderTest() throws Exception {
		zipFolderTest();

		final Path unzipPath = Paths.get("unzipTest");
		final File targetDirectory = unzipPath.toFile();
		DataTransformation.purge(targetDirectory);

		targetDirectory.mkdir();

		final File unzipped = DataTransformation.unzip(unzipPath, Files.readAllBytes(Paths.get(this.zipFile)));

		final File[] files = unzipped.listFiles();

		Assert.assertEquals(2, files.length);
		Assert.assertEquals("subFolder", files[0].getName());
		Assert.assertEquals("ZipMePlease.txt", files[1].getName());
		Assert.assertEquals("ZipMePlease2.txt", files[0].listFiles()[0].getName());

		DataTransformation.purge(targetDirectory);
	}

	@Test
	public void zipFolderTest() throws Exception {
		final byte[] zippedData = DataTransformation.zipFolder(Paths.get("test", "testData"));

		Files.write(Paths.get(this.zipFile), zippedData, StandardOpenOption.CREATE);
	}
}
