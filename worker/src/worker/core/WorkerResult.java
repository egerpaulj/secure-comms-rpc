package worker.core;

import java.io.Serializable;
import java.util.UUID;

public class WorkerResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public byte[] Result;
	public byte[] Attachments;
	public UUID Guid;

}
