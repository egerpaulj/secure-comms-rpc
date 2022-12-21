package nettyWrapper.core;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import utilsCore.converterLib.DataTransformation;

public class MessageDecoder extends ByteToMessageDecoder {

	private static long getMessageSize(final ByteBuf in) throws Exception {
		final int sizeOfLong = Long.BYTES / Byte.BYTES;

		final byte[] messageSize = new byte[sizeOfLong];
		in.readBytes(messageSize, 0, sizeOfLong);

		return DataTransformation.getLong(messageSize);
	}

	private final EncryptionWrapper encryptionProvider;

	private final Logger logger;

	public MessageDecoder(final EncryptionWrapper encryptionProvider) {
		super();
		this.encryptionProvider = encryptionProvider;

		setSingleDecode(true);

		this.logger = LoggerFactory.getLogger(MessageDecoder.class);
	}

	@Override
	protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
		try {
			final long messageSize = MessageDecoder.getMessageSize(in);

			if (in.readableBytes() < (messageSize - 8)) {
				this.logger.info("..........");
				in.resetReaderIndex();
				return;
			}

			this.logger.info("Message Received. Decoding..");
			final byte[] data = new byte[(int) messageSize];

			in.readBytes(data, 0, (int) messageSize); // blocking until message read

			final Object message = DataTransformation.convert(this.encryptionProvider.decrypt(data));

			out.add(message);
		} catch (final Exception ex) {
			ex.printStackTrace();
			this.logger.error("Failed to decode message");
			this.logger.warn("Message Decoder - resetting reader index - to collect all frame data. Capacity: "
					+ in.capacity() + " readableBytes: " + in.readableBytes());
			throw ex;
		}
	}

}
