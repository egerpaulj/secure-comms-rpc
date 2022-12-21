package nettyWrapper.core;

import java.io.Serializable;

public class ServerResponse<T extends Serializable> implements Serializable {
	/**
	 * Serial Version Id.
	 */
	private static final long serialVersionUID = 1L;

	private final T serverResponse;

	public ServerResponse(final T serverResponse) {
		super();
		this.serverResponse = serverResponse;
	}

	public T getResponse() {
		return this.serverResponse;
	}

}
