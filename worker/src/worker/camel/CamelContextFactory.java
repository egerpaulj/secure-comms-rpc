package worker.camel;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelContextFactory implements ICamelContextFactory {
	private final HashMap<UUID, CamelContext> activeContexts;

	private final ReentrantLock lock = new ReentrantLock();

	public CamelContextFactory() {
		this.activeContexts = new HashMap<>();
	}

	@Override
	public CamelContext createContext(final UUID id) {
		this.lock.lock();
		final CamelContext context = new DefaultCamelContext();
		try {
			stop(id);

			this.activeContexts.put(id, context);

		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			this.lock.unlock();
		}

		return context;
	}

	@Override
	public String listAll() {
		this.lock.lock();

		final StringBuffer buffer = new StringBuffer();

		for (final UUID id : this.activeContexts.keySet()) {
			buffer.append(id.toString());
			buffer.append("\n");
		}

		if (buffer.length() == 0) {
			buffer.append("** No Camel Contexts are Running");
		}

		return buffer.toString();

	}

	private void stop(final UUID id) {
		if (this.activeContexts.containsKey(id)) {

			try {
				this.activeContexts.get(id).stop();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}

			this.activeContexts.remove(id);
		}
	}

	@Override
	public void stopAll() {
		this.lock.lock();

		for (final UUID id : this.activeContexts.keySet()) {
			stop(id);
		}

		this.lock.unlock();
	}

	@Override
	public void stopContext(final UUID id) {
		this.lock.lock();

		stop(id);

		this.lock.unlock();

	}

}
