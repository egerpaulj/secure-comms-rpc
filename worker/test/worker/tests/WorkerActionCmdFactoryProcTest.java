package worker.tests;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import worker.commands.WorkerActionCmdFactoryProc;
import worker.core.IWorkerActionContext;

public class WorkerActionCmdFactoryProcTest {

	@Test
	public void createCommandTest() {
		final WorkerActionCmdFactoryProc testee = new WorkerActionCmdFactoryProc();

		final String command = testee.createCommand(new IWorkerActionContext() {

			@Override
			public void clearWork() {
				// TODO Auto-generated method stub

			}

			@Override
			public byte[] getAttachments() throws Exception {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getBinPath() {
				return "/SomePath/AnotherPath/exec";
			}

			@Override
			public UUID getId() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getOutputPath() {
				return "/SomePath/AnotherPath/output";
			}

			@Override
			public byte[] getResult() throws Exception {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public byte[] getWork() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getWorkingPath() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isFireAndForget() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isOutputFolderReturned() {
				return true;
			}

			@Override
			public void setResult(final byte[] result) {
				// TODO Auto-generated method stub

			}
		});

		Assert.assertNotNull(command);
	}
}
