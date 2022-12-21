package worker.camel;

import java.util.UUID;

import utilsCore.converterLib.DataTransformation;
import worker.core.IWorkerAction;
import worker.core.IWorkerActionContext;

public class WorkerActionCamelStop implements IWorkerAction {

	private final ICamelContextFactory camelContextFactory;

	public WorkerActionCamelStop(final ICamelContextFactory camelContextFactory) {
		super();
		this.camelContextFactory = camelContextFactory;
	}

	@Override
	public void Execute(final IWorkerActionContext context) throws Exception {
		final UUID camelContextId = (UUID) DataTransformation.convert(context.getWork());

		this.camelContextFactory.stopContext(camelContextId);

	}

}
