package worker.camel;

import utilsCore.converterLib.DataTransformation;
import worker.core.IWorkerAction;
import worker.core.IWorkerActionContext;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

public class WorkerActionCamel implements IWorkerAction {

	private final ICamelContextFactory camelFactory;

	public WorkerActionCamel(final ICamelContextFactory camelFactory) {
		super();
		this.camelFactory = camelFactory;
	}

	private RouteBuilder createRouteBuilder(final WorkerRouteDefinition routeDefinition) {

		if (routeDefinition.getParallelProcessingNo() > 1)
			return new RouteBuilder() {

				@Override
				public void configure() throws Exception {
					from(routeDefinition.getFrom())
							// ToDo Test generic case. Data has to be a list in the body. what if it's not.
							// Parallels forces it. Provide a split expression.
							.split(simple("${body}")).to("seda:data");

					from("seda:data?concurrentConsumers=" + routeDefinition.getParallelProcessingNo()).doTry()
							.to(routeDefinition.getTo()).log("To Successfull.")

							.doCatch(Exception.class).log("Error processing: ${body}");

				}

			};

		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from(routeDefinition.getFrom()).doTry().to(routeDefinition.getTo()).log("To Successfull.")

						.doCatch(Exception.class).log("Error processing: ${body}");

			}
		};

	}

	@Override
	public void Execute(final IWorkerActionContext context) throws Exception {
		final WorkerRouteDefinition routeDefinition = (WorkerRouteDefinition) DataTransformation
				.convert(context.getWork());

		final CamelContext camelContext = this.camelFactory.createContext(context.getId());

		final RouteBuilder routeBuilder = createRouteBuilder(routeDefinition);

		camelContext.addRoutes(routeBuilder);
		camelContext.start();

		context.setResult(DataTransformation.convert("Started Camel Context: " + context.getId()));
	}

}
