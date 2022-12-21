package worker.commands;

import worker.core.IWorkerActionCmdFactory;
import worker.core.IWorkerActionContext;

public class WorkerActionCmdFactoryProc implements IWorkerActionCmdFactory {

	private static String convertToExecute(final String binPath) {
		final int lastFolderIndex = binPath.lastIndexOf('/');
		final String startStr = binPath.substring(0, lastFolderIndex + 1);
		final String endStr = "./" + binPath.substring(lastFolderIndex + 1, binPath.length());

		return startStr + endStr;
	}

	@Override
	public String createCommand(final IWorkerActionContext workerContext) {
		return workerContext.isOutputFolderReturned()
				? WorkerActionCmdFactoryProc.convertToExecute(workerContext.getBinPath()) + " "
						+ workerContext.getOutputPath()
				: WorkerActionCmdFactoryProc.convertToExecute(workerContext.getBinPath());
	}

}
