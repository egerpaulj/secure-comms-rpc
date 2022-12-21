package nettyWrapper.core;

import java.io.Serializable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handles client responses in the netty pipeline.
 * 
 * @author egerpaul
 *
 * @param <T> The type of the response.
 */
public class ClientResponseHandler<T extends Serializable> extends ChannelInboundHandlerAdapter {
	private final Logger logger;
	private ServerResponse<T> response;
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final CyclicBarrier barrier;

	public ClientResponseHandler() {
		super();
		this.barrier = new CyclicBarrier(2);
		this.logger = LoggerFactory.getLogger(ClientResponseHandler.class);
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		this.logger.info("Got a message back from the server " + msg);

		this.response = (ServerResponse<T>) msg;

		this.logger.debug("Response is " + this.response.getResponse().toString());
		// Notify message was received
		this.barrier.await();
	}

	public Future<ServerResponse<T>> getResponse() {
		return this.executor.submit(() -> {
			this.barrier.await();

			if (this.response == null)
				throw new Exception("Incorrect response notification. Response is null");

			return this.response;
		});
	}
}
