package nettyWrapper.core;

import java.io.Serializable;

public interface IResponseFactory<I extends Serializable, O extends Serializable> {
	ServerResponse<O> createResponse(I request) throws Exception;
}
