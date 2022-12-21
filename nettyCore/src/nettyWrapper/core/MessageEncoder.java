package nettyWrapper.core;

import java.nio.ByteBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import utilsCore.converterLib.DataTransformation;

public class MessageEncoder extends MessageToByteEncoder<Object> {

	private final EncryptionWrapper encryptionProvider;

	public MessageEncoder(final EncryptionWrapper encryptionProvider) {
		super();
		this.encryptionProvider = encryptionProvider;
	}

	@Override
	protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
		byte[] data;

		if (msg instanceof byte[]) {
			data = (byte[]) msg;
		} else {
			data = DataTransformation.convert(msg);
		}

		final byte[] encryptedData = this.encryptionProvider.encrypt(data);

		final long sizeOfData = encryptedData.length;
		final byte[] messageSize = ByteBuffer.allocate(Long.BYTES / Byte.BYTES).putLong(sizeOfData).array();
		out.writeBytes(messageSize);
		out.writeBytes(encryptedData);
	}
}