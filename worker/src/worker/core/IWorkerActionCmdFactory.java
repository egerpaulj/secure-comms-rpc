package worker.core;

public interface IWorkerActionCmdFactory {
	String createCommand(IWorkerActionContext workerContext) throws Exception;

}
