package worker.server;

import java.util.HashMap;

import nettyWrapper.core.EncryptionWrapper;
import nettyWrapper.core.IResponseFactory;
import nettyWrapper.core.ServerResponse;
import worker.camel.ICamelContextFactory;
import worker.camel.WorkerActionCamel;
import worker.camel.WorkerActionCamelLs;
import worker.camel.WorkerActionCamelStop;
import worker.core.WorkerCommand;
import worker.core.WorkerResult;
import worker.core.IWorkerAction;
import worker.core.IWorkerActionContext;
import worker.core.WorkerActionContext;
import worker.commands.*;

public class ServerResponseFactory implements IResponseFactory<WorkerCommand, WorkerResult> {
	private final HashMap<IWorkerAction.WorkerId, IWorkerAction> workerActionMapper;

	private final EncryptionWrapper encryptionProvider;

	public ServerResponseFactory(final ICamelContextFactory camelContextFactory, final EncryptionWrapper encryptionProvider) {
		super();
		this.encryptionProvider = encryptionProvider;

		this.workerActionMapper = new HashMap<>();
		this.workerActionMapper.put(IWorkerAction.WorkerId.j,
				new WorkerActionExecProcess(new WorkerActionCmdFactoryJar()));
		this.workerActionMapper.put(IWorkerAction.WorkerId.jWithRes,
				new WorkerActionExecProcess(new WorkerActionCmdFactoryJar()));
		this.workerActionMapper.put(IWorkerAction.WorkerId.bin,
				new WorkerActionExecProcess(new WorkerActionCmdFactoryProc()));
		this.workerActionMapper.put(IWorkerAction.WorkerId.binWithRes,
				new WorkerActionExecProcess(new WorkerActionCmdFactoryProc()));
		this.workerActionMapper.put(IWorkerAction.WorkerId.cmdStr,
				new WorkerActionExecProcess(new WorkerActionCmdFactoryStr()));
		this.workerActionMapper.put(IWorkerAction.WorkerId.cml, new WorkerActionCamel(camelContextFactory));
		this.workerActionMapper.put(IWorkerAction.WorkerId.cmlStop, new WorkerActionCamelStop(camelContextFactory));
		this.workerActionMapper.put(IWorkerAction.WorkerId.cmlLs, new WorkerActionCamelLs(camelContextFactory));
	}

	@Override
	public ServerResponse<WorkerResult> createResponse(final WorkerCommand request) throws Exception {
		final IWorkerActionContext executionContext = new WorkerActionContext(request);

		try {
			if (!this.workerActionMapper.containsKey(request.WorkerId))
				throw new Exception("Unknown KEY. Key not mapped: " + request.WorkerId);

			this.workerActionMapper.get(request.WorkerId).Execute(executionContext);

			final WorkerResult result = new WorkerResult();

			result.Guid = request.Guid;

			result.Result = this.encryptionProvider.encrypt(executionContext.getResult(), request.Secret);
			result.Attachments = this.encryptionProvider.encrypt(executionContext.getAttachments(), request.Secret);

			return new ServerResponse<>(result);
		} finally {
			executionContext.clearWork();
		}
	}

}
