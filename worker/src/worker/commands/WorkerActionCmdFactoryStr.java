package worker.commands;

import java.nio.charset.Charset;

import worker.core.IWorkerActionCmdFactory;
import worker.core.IWorkerActionContext;

public class WorkerActionCmdFactoryStr implements IWorkerActionCmdFactory {

	@Override
	public String createCommand(final IWorkerActionContext workerContext) throws Exception {
		try {
			return new String(workerContext.getWork(), Charset.defaultCharset());
		} catch (final Exception e) {
			throw new Exception("Error parsing command. Not a String", e);
		}
	}

}
