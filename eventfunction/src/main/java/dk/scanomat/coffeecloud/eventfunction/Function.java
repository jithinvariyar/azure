package dk.scanomat.coffeecloud.eventfunction;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.Cardinality;
import com.microsoft.azure.functions.annotation.EventHubTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.SendGridOutput;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

	@FunctionName("ehprocessor")
	public void eventHubProcessor(
			@EventHubTrigger(name = "msg", eventHubName = "", connection = "eventhubConnString", cardinality = Cardinality.ONE) String eventHubMessage,
			@SendGridOutput(name = "message", dataType = "String", apiKey = "sendGridAPIKey", to = "jithin@mailinator.com", from = "jithin@vinnovatelabz.com", subject = "Azure Functions email with SendGrid", text = "Sent from Azure Functions") OutputBinding<String> message,
			final ExecutionContext context) {

		final String toAddress = "jithin@mailinator.com";
		final String toAddressMail = "jishnu@mailinator.com";
		final String value = "Sent from Azure Functions-->" + eventHubMessage;

		StringBuilder builder = new StringBuilder().append("{")
				.append("\"personalizations\": [{ \"to\": [{ \"email\": \"%s\"},{ \"email\": \"%s\"}]}],")
				.append("\"content\": [{\"type\": \"text/plain\", \"value\": \"%s\"}]").append("}");

		final String body = String.format(builder.toString(), toAddress,toAddressMail, value);

		message.setValue(body);
	}
}
