package worker.tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class WorkerActionExecProcessTest {
	@Test
	public void ExecuteProcess() throws IOException, InterruptedException, ClassNotFoundException {
		final Process process = Runtime.getRuntime().exec(new String[] { "ls", "-l" });

		process.waitFor();

		final InputStream stdOut = process.getInputStream();

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		final byte[] buffer = new byte[1024];

		int len;

		while ((len = stdOut.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}

		outputStream.flush();
		// byte[] output = outputStream.toByteArray();

		System.out.println("Result is: \n" + outputStream.toString());

	}

}
