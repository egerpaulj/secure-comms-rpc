package worker.commands;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import utilsCore.converterLib.DataTransformation;
import worker.core.IWorkerAction;
import worker.core.IWorkerActionCmdFactory;
import worker.core.IWorkerActionContext;

public class WorkerActionExecProcess implements IWorkerAction {

	private final IWorkerActionCmdFactory commandFactory;

	public WorkerActionExecProcess(final IWorkerActionCmdFactory commandFactory) {
		this.commandFactory = commandFactory;
	}

	@Override
	public void Execute(final IWorkerActionContext context) throws Exception {
		try {
			final Process process = Runtime.getRuntime().exec(this.commandFactory.createCommand(context), new String[0],
					new File(context.getWorkingPath()));

			process.waitFor();

			if (process.exitValue() == 0) {
				setStdOutAsResult(context, process);
			} else {
				setStdErrAsResult(context, process);
			}

		} catch (final IOException e) {
			e.printStackTrace();
			setExceptionAsResult(context, e);
		} catch (final InterruptedException e) {
			e.printStackTrace();
			setExceptionAsResult(context, e);
		}
	}

	private void setExceptionAsResult(final IWorkerActionContext contextManager, final Exception exception) {
		try {
			contextManager
					.setResult(DataTransformation.convert(exception.getMessage() + "\n" + exception.getStackTrace()));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setStdErrAsResult(final IWorkerActionContext contextManager, final Process process)
			throws IOException {
		final InputStream errOut = process.getErrorStream();

		writeStreamAsResult(contextManager, errOut);
	}

	private void setStdOutAsResult(final IWorkerActionContext contextManager, final Process process)
			throws IOException {
		final InputStream stdOut = process.getInputStream();

		writeStreamAsResult(contextManager, stdOut);
	}

	private void writeStreamAsResult(final IWorkerActionContext contextManager, final InputStream stdOut)
			throws IOException {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		final byte[] buffer = new byte[1024];

		int len;

		while ((len = stdOut.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}

		outputStream.flush();
		final String outStr = new String(outputStream.toByteArray(), Charset.defaultCharset());

		contextManager.setResult(outStr.getBytes());
	}

}
