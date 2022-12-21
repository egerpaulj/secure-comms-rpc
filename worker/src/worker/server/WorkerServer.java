package worker.server;

import nettyWrapper.core.EncryptionWrapper;
import nettyWrapper.core.Server;
import worker.camel.CamelContextFactory;
import worker.camel.ICamelContextFactory;
import worker.core.WorkerCommand;
import worker.core.WorkerResult;

public class WorkerServer {
	public static void main(final String[] args) throws Exception {
		int port = 9090;
		String pathToKey = "/home/user/keys/e3b41dd4-fe87-4982-9b9c-06b6523ca98e";

		if (args.length == 2) {
			port = Integer.parseInt(args[0]);
			pathToKey = args[1];
		}

		final WorkerServer server = new WorkerServer(port,
				EncryptionWrapper.createSingleKeyEncryptionWrapper(pathToKey, true), new CamelContextFactory());

		try {
			server.start();
			// System.out.println("Press any key to quit");
			System.in.read();

		} catch (final InterruptedException e) {
			System.out.println("Failed to start server");
			e.printStackTrace();
		} finally {
			try {
				server.stop();
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Failed to stop server");
				e.printStackTrace();
			}
		}
	}

	private final Server<WorkerCommand, WorkerResult> server;

	private final ICamelContextFactory camelContextFactory;

	public WorkerServer(final int port, final EncryptionWrapper encryptionWrapper,
			final ICamelContextFactory camelContextFactory) {
		this.camelContextFactory = camelContextFactory;
		this.server = new Server<>(port, encryptionWrapper,
				new ServerResponseFactory(camelContextFactory, encryptionWrapper));
	}

	public void start() throws InterruptedException {
		this.server.start();

	}

	public void stop() throws InterruptedException {
		this.camelContextFactory.stopAll();
		this.server.stop();

	}
}
