package worker.camel;

import java.nio.charset.Charset;

import worker.core.IWorkerAction;
import worker.core.IWorkerActionContext;

public class WorkerActionCamelLs implements IWorkerAction {

	private final ICamelContextFactory camelContextFactory;

	public WorkerActionCamelLs(final ICamelContextFactory camelContextFactory) {
		super();
		this.camelContextFactory = camelContextFactory;
	}

	@Override
	public void Execute(final IWorkerActionContext context) throws Exception {
		context.setResult(this.camelContextFactory.listAll().getBytes(Charset.defaultCharset()));
	}

}
