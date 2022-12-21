package worker.commands;

import worker.core.IWorkerActionCmdFactory;
import worker.core.IWorkerActionContext;

public class WorkerActionCmdFactoryJar implements IWorkerActionCmdFactory {

	@Override
	public String createCommand(final IWorkerActionContext workerContext) {
		return workerContext.isOutputFolderReturned()
				? "java -jar " + workerContext.getBinPath() + " " + workerContext.getOutputPath()
				: "java -jar " + workerContext.getBinPath();
	}

}
