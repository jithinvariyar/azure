package com.jithin.iot.app;

import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.MethodResult;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class App {
	public static final String iotHubConnectionString = "HostName=jithin-iot-hub-demo.azure-devices.net;SharedAccessKeyName=service;SharedAccessKey=CYCV/X8iIZNNeiOBBzK6i3jXaLB6/kaPDS7m6Y6nKLM=";
	public static final String deviceId = "myDeviceId";

	private static final String methodName = "reboot";
	private static final Long responseTimeout = TimeUnit.SECONDS.toSeconds(30);
	private static final Long connectTimeout = TimeUnit.SECONDS.toSeconds(5);

	public static void main(String[] args) throws IOException{
		System.out.println("Starting sample...");
		DeviceMethod methodClient = DeviceMethod.createFromConnectionString(iotHubConnectionString);

		try
		{
		  System.out.println("Invoke reboot direct method");
		  MethodResult result = methodClient.invoke(deviceId, methodName, responseTimeout, connectTimeout, null);

		  if(result == null)
		  {
		    throw new IOException("Invoke direct method reboot returns null");
		  }
		  System.out.println("Invoked reboot on device");
		  System.out.println("Status for device:   " + result.getStatus());
		  System.out.println("Message from device: " + result.getPayload());
		}
		catch (IotHubException e)
		{
		    System.out.println(e.getMessage());
		}
		
		ShowReportedProperties showReportedProperties = new ShowReportedProperties();
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(showReportedProperties);
		
		System.out.println("Press ENTER to exit.");
		System.in.read();
		executor.shutdownNow();
		System.out.println("Shutting down sample...");
	}

	private static class ShowReportedProperties implements Runnable {
		public void run() {
			try {
				DeviceTwin deviceTwins = DeviceTwin.createFromConnectionString(iotHubConnectionString);
				DeviceTwinDevice twinDevice = new DeviceTwinDevice(deviceId);
				while (true) {
					System.out.println("Get reported properties from device twin");
					deviceTwins.getTwin(twinDevice);
					System.out.println(twinDevice.reportedPropertiesToString());
					Thread.sleep(10000);
				}
			} catch (Exception ex) {
				System.out.println("Exception reading reported properties: " + ex.getMessage());
			}
		}
	}
}
