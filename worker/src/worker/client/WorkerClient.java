package worker.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.Future;

import encryptionClient.ServerBasedKeyManager;
import encryptionLib.core.IKeyManager;
import nettyWrapper.core.Client;
import nettyWrapper.core.EncryptionWrapper;
import nettyWrapper.core.ServerCommand;
import nettyWrapper.core.ServerResponse;
import utilsCore.converterLib.DataTransformation;
import worker.camel.WorkerRouteDefinition;
import worker.core.IWorkerAction;
import worker.core.WorkerCommand;
import worker.core.WorkerResult;

public class WorkerClient {

	public static void main(final String args[]) throws Exception {
		int port = 9090;
		String host = "localhost";
		String encServer = "localhost";
		int encPort = 6060;
		final String pathToCommKey = "/home/user/keys/e3b41dd4-fe87-4982-9b9c-06b6523ca98e";

		if (args.length == 4) {
			host = args[0];
			port = Integer.parseInt(args[1]);
			encServer = args[2];
			encPort = Integer.parseInt(args[3]);
		} else {
			System.out.println("arguments: hostname port encServer encServerport");
			return;
		}

		final EncryptionWrapper commWrapper = EncryptionWrapper.createSingleKeyEncryptionWrapper(pathToCommKey, false);

		final IKeyManager keyManager = new ServerBasedKeyManager(encPort, encServer, commWrapper);

		new WorkerClient(host, port, keyManager, commWrapper);

		// ToDo Several clients? easier to call

	}

	private final Client<WorkerCommand, WorkerResult> client;

	// Server based key manager. ALWAYS
	private final IKeyManager serverBasedKeyManager;

	private final EncryptionWrapper encWrapper;

	public WorkerClient(final String host, final int port, final IKeyManager encryptionManager, final EncryptionWrapper encryptionWrapper) {
		super();
		this.serverBasedKeyManager = encryptionManager;
		this.encWrapper = encryptionWrapper;

		this.client = new Client<>(host, port, this.encWrapper);
	}

	public void close() throws InterruptedException {
		this.client.close();
	}

	private byte[] convertToDataBytes(final Object obj) throws IOException {
		return DataTransformation.convert(obj);
	}

	private WorkerCommand createCommand(final IWorkerAction.WorkerId workerId, final boolean isFireForget) throws Exception {
		final WorkerCommand command = new WorkerCommand();
		command.Guid = UUID.randomUUID();
		command.WorkerId = workerId;
		command.Secret = this.serverBasedKeyManager.getKey(command.Guid);
		command.FireAndForget = isFireForget;
		return command;
	}

	public Result executeBinary(final byte[] binary, final boolean fireForget, final boolean withComplexOutput) throws Exception {
		final WorkerCommand command = withComplexOutput ? createCommand(IWorkerAction.WorkerId.binWithRes, fireForget)
				: createCommand(IWorkerAction.WorkerId.bin, fireForget);

		command.Work = binary;

		final Future<ServerResponse<WorkerResult>> response = this.client.send(new ServerCommand<>(command));
		final Result result = getResponse(response, command);

		this.client.close();

		return result;
	}

	public Result executeCommand(final String cmd, final boolean fireForget) throws Exception {

		final WorkerCommand command = createCommand(IWorkerAction.WorkerId.cmdStr, fireForget);
		command.Work = cmd.getBytes(Charset.defaultCharset());

		final Future<ServerResponse<WorkerResult>> response = this.client.send(new ServerCommand<>(command));
		final Result result = getResponse(response, command);

		this.client.close();
		return result;
	}

	public Result executeJar(final byte[] jar, final boolean fireForget) throws Exception {
		final WorkerCommand command = createCommand(IWorkerAction.WorkerId.j, fireForget);
		command.Work = jar;

		final Future<ServerResponse<WorkerResult>> response = this.client.send(new ServerCommand<>(command));
		final Result result = getResponse(response, command);

		this.client.close();

		return result;
	}

	public Result executeJar(final String filePath, final boolean fireForget, final boolean withComplexOutput) throws Exception {
		final File file = new File(filePath);

		if (!file.exists())
			throw new Exception("Jar file not found: " + filePath);

		final FileInputStream fin = new FileInputStream(file);

		final byte[] buffer = new byte[1024];
		int dataLength;

		final ByteArrayOutputStream bout = new ByteArrayOutputStream();

		while ((dataLength = fin.read(buffer)) != -1) {
			bout.write(buffer, 0, dataLength);
		}

		bout.close();
		fin.close();

		final WorkerCommand command = withComplexOutput ? createCommand(IWorkerAction.WorkerId.jWithRes, fireForget)
				: createCommand(IWorkerAction.WorkerId.j, fireForget);

		command.Work = bout.toByteArray();

		final Future<ServerResponse<WorkerResult>> response = this.client.send(new ServerCommand<>(command));
		final Result result = getResponse(response, command);

		this.client.close();

		return result;
	}

	private Result getResponse(final Future<ServerResponse<WorkerResult>> response, final WorkerCommand command) throws Exception {
		final WorkerResult workerResult = response.get().getResponse();
		final byte[] decryptedResult = this.encWrapper.decrypt(workerResult.Result, command.Secret);
		final byte[] decryptedAttachments = this.encWrapper.decrypt(workerResult.Attachments, command.Secret);
		return new Result(command.Guid, decryptedResult, decryptedAttachments);
	}

	public Result listCamelContexts() throws Exception {

		final WorkerCommand command = createCommand(IWorkerAction.WorkerId.cmlLs, true);

		final Future<ServerResponse<WorkerResult>> response = this.client.send(new ServerCommand<>(command));
		final Result result = getResponse(response, command);
		this.client.close();

		return result;
	}

	public Result startCamelContext(final String from, final String to, final int noParallelProcessors) throws Exception {

		final WorkerCommand command = createCommand(IWorkerAction.WorkerId.cml, true);

		command.Work = convertToDataBytes(new WorkerRouteDefinition(from, to, noParallelProcessors));

		final Future<ServerResponse<WorkerResult>> response = this.client.send(new ServerCommand<>(command));
		final Result result = getResponse(response, command);
		this.client.close();

		return result;
	}
	
	public Result stopCamelContext(UUID id) throws Exception {
		final WorkerCommand command = createCommand(IWorkerAction.WorkerId.cml, true);

		command.Work = convertToDataBytes(id);

		final Future<ServerResponse<WorkerResult>> response = this.client.send(new ServerCommand<>(command));
		
		final Result result = getResponse(response, command);
		this.client.close();

		return result;
	}
	
	public void stopAllCamelContexts() throws Exception {
		String[] runningContexts = listCamelContexts().getResultAsString().split("\n");
		
		if(runningContexts.length == 0) {
			return;
		}
		
		for(String contextStr : runningContexts) {
			UUID id = UUID.fromString(contextStr);
			
			stopCamelContext(id);
		}
	}
}
