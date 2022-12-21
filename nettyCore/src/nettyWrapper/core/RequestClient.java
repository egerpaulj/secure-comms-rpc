package nettyWrapper.core;

import java.util.concurrent.Future;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RequestClient {
	private EventLoopGroup workerGroup;
	private final String host;
	private final int port;
	private Bootstrap bootstrap;
	private Channel channel;
	EventLoopGroup workGroup = new NioEventLoopGroup();
	private ClientResponseHandler responseHandler;
	private final EncryptionWrapper encryptionProvider;

	public RequestClient(final String host, final int port, final EncryptionWrapper encryptionProvider) {
		super();
		this.host = host;
		this.port = port;
		this.encryptionProvider = encryptionProvider;
	}

	public synchronized void close() throws InterruptedException {
		this.workerGroup.shutdownGracefully();
		this.workerGroup = null;

		if (this.channel != null) {
			this.channel.closeFuture().sync();
			this.channel.close();
		}
	}

	private void init() {
		this.responseHandler = new ClientResponseHandler();

		this.workerGroup = new NioEventLoopGroup();
		this.bootstrap = new Bootstrap(); // (1)
		this.bootstrap.group(this.workerGroup); // (2)
		this.bootstrap.channel(NioSocketChannel.class); // (3)
		this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true); // (4)
		this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(final SocketChannel ch) throws Exception {
				ch.pipeline().addFirst(new MessageEncoder(RequestClient.this.encryptionProvider));
				ch.pipeline().addLast(new MessageDecoder(RequestClient.this.encryptionProvider),
						RequestClient.this.responseHandler);
			}
		});
	}

	public synchronized Future<ServerResponse> send(final ServerCommand serverCommand) throws Exception {
		init();
		if (this.workerGroup == null) {
			this.workerGroup = new NioEventLoopGroup();
		}

		// Start the client.

		if (this.channel == null) {
			final ChannelFuture channelFuture = this.bootstrap.connect(this.host, this.port).sync();
			channelFuture.sync();

			this.channel = channelFuture.channel(); // (5)
		}

		final ChannelFuture future = this.channel.writeAndFlush(serverCommand);
		future.sync();

		return this.responseHandler.getResponse();
	}

	public void shutdown() {
		this.workGroup.shutdownGracefully();
	}

}
