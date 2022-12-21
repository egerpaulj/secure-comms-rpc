package worker.camel;

import java.io.Serializable;

public class WorkerRouteDefinition implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String from;
	private final String to;

	/**
	 * The number of parallel sedas to run whilst processing this route.
	 */
	private final int parallelProcessingNo;

	public WorkerRouteDefinition(final String from, final String to, final int parallelProcessingNo) {
		super();
		this.from = from;
		this.to = to;
		this.parallelProcessingNo = parallelProcessingNo;
	}

	public String getFrom() {
		return this.from;
	}

	public int getParallelProcessingNo() {
		return this.parallelProcessingNo;
	}

	public String getTo() {
		return this.to;
	}
}
