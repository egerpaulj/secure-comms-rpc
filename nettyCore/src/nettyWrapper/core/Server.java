package nettyWrapper.core;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Server which hosts on a port and process requests. Currently processes
 * requests synchronously
 * 
 * @author egerpaul
 *
 * @param <I> Input(Server request)
 * @param <O> Output (Server response)
 */
public class Server<I extends Serializable, O extends Serializable> {
	private final int port;
	public ChannelFuture channelFuture;
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
	private EventLoopGroup workerGroup = new NioEventLoopGroup();
	private final EncryptionWrapper encryptionProvider;
	private final IResponseFactory<I, O> responseFactory;
	private final Logger logger;

	public Server(final int port, final EncryptionWrapper encryptionProvider,
			final IResponseFactory<I, O> responseFactory) {
		this.encryptionProvider = encryptionProvider;
		this.port = port;
		this.responseFactory = responseFactory;
		this.logger = Logger.getLogger("Server");
	}

	public void shutdown() {
		this.workerGroup.shutdownGracefully();
		this.bossGroup.shutdownGracefully();
	}

	// ToDo use SSL or use this for confusion
	public void start() throws InterruptedException {
		this.logger.log(Level.INFO, "Starting Server");

		this.bossGroup = new NioEventLoopGroup(); // (1)
		this.workerGroup = new NioEventLoopGroup();
		final ServerBootstrap bootstrap = new ServerBootstrap(); // (2)
		bootstrap.group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class) // (3)
				.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
					@Override
					public void initChannel(final SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new MessageDecoder(Server.this.encryptionProvider),
								new ServerRequestHandler<>(Server.this.encryptionProvider,
										Server.this.responseFactory));
					}
				}).option(ChannelOption.SO_BACKLOG, 128) // (5)
				.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

		// Bind and start to accept incoming connections.
		this.channelFuture = bootstrap.bind(this.port).sync(); // (7)

		this.channelFuture.channel().closeFuture().sync();
	}

	public void stop() throws InterruptedException {
		this.workerGroup.shutdownGracefully();
		this.bossGroup.shutdownGracefully();
		this.channelFuture.channel().closeFuture().sync();

	}

}
