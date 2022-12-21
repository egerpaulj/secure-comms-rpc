package worker.core;

import java.util.UUID;

public interface IWorkerActionContext {
	void clearWork();

	byte[] getAttachments() throws Exception;

	String getBinPath();

	UUID getId();

	String getOutputPath();

	byte[] getResult() throws Exception;

	byte[] getWork();

	String getWorkingPath();

	boolean isFireAndForget();

	boolean isOutputFolderReturned();

	void setResult(byte[] result);

}
