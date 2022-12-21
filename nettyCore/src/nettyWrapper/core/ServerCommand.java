package nettyWrapper.core;

import java.io.Serializable;

public class ServerCommand<T extends Serializable> implements Serializable {
	/**
	 * Serial Version Id.
	 */
	private static final long serialVersionUID = 1L;

	private final T serverCommand;

	public ServerCommand(final T serverCommand) {
		super();
		this.serverCommand = serverCommand;
	}

	public T getCommand() {
		return this.serverCommand;
	}

}
