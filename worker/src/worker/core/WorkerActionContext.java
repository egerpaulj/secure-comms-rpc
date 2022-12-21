package worker.core;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import utilsCore.converterLib.DataTransformation;

public class WorkerActionContext implements IWorkerActionContext {

	private String rootPath = null;
	private String workingPath;
	private String binPath;
	private String outputPath;
	private UUID guid;
	private boolean isFireAndForget;
	private byte[] result;
	private byte[] exec;
	private IWorkerAction.WorkerId workerId;

	public WorkerActionContext(final String rootPath, final WorkerCommand command) throws IOException {
		this.rootPath = rootPath;
		initContext(command);
	}

	public WorkerActionContext(final WorkerCommand command) throws IOException {
		initContext(command);
	}

	@Override
	public void clearWork() {
		final File workingDirectory = new File(this.workingPath);

		if (!workingDirectory.exists())
			return;

		DataTransformation.purge(workingDirectory);
	}

	@Override
	public byte[] getAttachments() throws Exception {
		final File outputDir = new File(this.outputPath);

		if (!outputDir.exists())
			return new byte[0];

		final boolean hasAnyOutput = outputDir.list().length > 0;

		if (!hasAnyOutput)
			return new byte[0];

		return DataTransformation.zipFolder(outputDir.toPath());
	}

	@Override
	public String getBinPath() {
		return this.binPath;
	}

	@Override
	public UUID getId() {
		return this.guid;
	}

	@Override
	public String getOutputPath() {
		return this.outputPath;
	}

	@Override
	public byte[] getResult() throws Exception {
		return this.result;
	}

	@Override
	public byte[] getWork() {
		return this.exec;
	}

	@Override
	public String getWorkingPath() {
		return this.workingPath;
	}

	private void initContext(final WorkerCommand command) throws IOException {
		this.workerId = command.WorkerId;
		this.guid = command.Guid;
		this.isFireAndForget = command.FireAndForget;
		this.exec = command.Work;

		final String pathStr = this.rootPath == null ? command.Guid.toString()
				: this.rootPath + "/" + command.Guid.toString();
		final Path path = Paths.get(pathStr);

		final File directory = path.toFile();
		this.workingPath = directory.getAbsolutePath();

		DataTransformation.purge(directory);

		// working directory
		directory.mkdir();

		// OUTPUT
		final File outputDirectory = Paths.get(path.toAbsolutePath() + "/output").toFile();
		outputDirectory.mkdir();

		this.outputPath = outputDirectory.getAbsolutePath();

		if (command.Work != null) {
			// EXEC File
			final Path binPath = Paths.get(path.toAbsolutePath() + "/exec");
			DataTransformation.writeToFile(binPath, command.Work);

			binPath.toFile().setExecutable(true);

			this.binPath = binPath.toAbsolutePath().toString();
		}

	}

	@Override
	public boolean isFireAndForget() {
		return this.isFireAndForget;
	}

	@Override
	public boolean isOutputFolderReturned() {
		return (this.workerId == IWorkerAction.WorkerId.binWithRes)
				|| (this.workerId == IWorkerAction.WorkerId.jWithRes);
	}

	@Override
	public void setResult(final byte[] result) {
		this.result = result;

	}

}
