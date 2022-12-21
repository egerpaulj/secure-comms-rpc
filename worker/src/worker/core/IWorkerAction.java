package worker.core;

public interface IWorkerAction {
	public enum WorkerId {
		j,

		cml, cmlStop, cmlLs,

		cmdStr, bin,

		jWithRes, binWithRes
	}

	void Execute(IWorkerActionContext context) throws Exception;

}
