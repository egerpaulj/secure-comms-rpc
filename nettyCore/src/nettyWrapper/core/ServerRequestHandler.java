package nettyWrapper.core;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Netty Server Request Handler which creates a response from the request.
 * 
 * @author egerpaul
 *
 * @param <I> Input (Request)
 * @param <O> OUput (Response)
 */
public class ServerRequestHandler<I extends Serializable, O extends Serializable> extends ChannelInboundHandlerAdapter {

	private final EncryptionWrapper encryptionProvider;
	private final Logger logger;
	private final IResponseFactory<I, O> responseFactory;

	public ServerRequestHandler(final EncryptionWrapper encryptionProvider,
			final IResponseFactory<I, O> responseFactory) {
		super();
		this.encryptionProvider = encryptionProvider;
		this.responseFactory = responseFactory;

		this.logger = LoggerFactory.getLogger(ServerRequestHandler.class);
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {

		final ServerCommand<I> command = (ServerCommand<I>) msg;

		this.logger.info("Received Message : " + command.getCommand().toString());

		final ChannelPipeline pipeline = ctx.channel().pipeline().addFirst(new MessageEncoder(this.encryptionProvider))
				.addFirst(new ChunkedWriteHandler());

		// ToDo DOS Attack?
		// ToDo Handle multiple calls with sleeps. Connection lost, response lost
		final ServerResponse<O> response = this.responseFactory.createResponse(command.getCommand());
		this.logger.debug("Sending response back to client " + response.getResponse());

		final ChannelFuture future = pipeline.channel().writeAndFlush(response);

		future.sync();
	}

	@Override
	public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
