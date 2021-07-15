package com.jithin.iot.app;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Device;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Property;

public class App {
	private static String connString = "HostName=jithiniot.azure-devices.net;DeviceId=device1;SharedAccessKey=20b8+JXZb9xGGiXtHAVAhIeOi5ONxlWf6UBGic2L/aE=";
	private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
	private static String deviceId = "device1";

	public static void main(String[] args) throws URISyntaxException, IOException {
		DeviceClient client = new DeviceClient(connString, protocol);

		// Create a Device object to store the device twin properties
		Device dataCollector = new Device() {
			// Print details when a property value changes
			public void PropertyCall(String propertyKey, Object propertyValue, Object context) {
				System.out.println(propertyKey + " changed to " + propertyValue);
			}
		};

		try {
			// Open the DeviceClient and start the device twin services.
			client.open();
			client.startDeviceTwin(new DeviceTwinStatusCallBack(), null, dataCollector, null);

			// Create a reported property and send it to your IoT hub.
			dataCollector.setReportedProp(new Property("connectivityType", "cellular"));
			client.sendReportedProperties(dataCollector.getReportedProp());
		} catch (Exception e) {
			System.out.println("On exception, shutting down \n" + " Cause: " + e.getCause() + " \n" + e.getMessage());
			dataCollector.clean();
			client.closeNow();
			System.out.println("Shutting down...");
		}

		System.out.println("Press any key to exit...");

		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();

		dataCollector.clean();
		client.close();
	}

	protected static class DeviceTwinStatusCallBack implements IotHubEventCallback {
		public void execute(IotHubStatusCode responseStatus, Object callbackContext) {
			System.out.println("IoT Hub responded to device twin operation with status " + responseStatus.name());
		}
	}
}
