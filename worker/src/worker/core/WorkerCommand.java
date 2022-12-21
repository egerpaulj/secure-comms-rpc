package worker.core;

import java.io.Serializable;
import java.util.UUID;

import javax.crypto.SecretKey;

public class WorkerCommand implements Serializable {

	private static final long serialVersionUID = 1L;
	public SecretKey Secret;
	public byte[] Work;
	public UUID Guid;
	public IWorkerAction.WorkerId WorkerId;
	public boolean FireAndForget;

}
