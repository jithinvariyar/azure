package com.jithin.iot.app;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;
import com.microsoft.azure.sdk.iot.service.devicetwin.Pair;
import com.microsoft.azure.sdk.iot.service.devicetwin.Query;
import com.microsoft.azure.sdk.iot.service.devicetwin.SqlQuery;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

public class App {
	public static final String iotHubConnectionString = "HostName=jithiniot.azure-devices.net;SharedAccessKeyName=serviceAndRegistryRead;SharedAccessKey=BSZ0UtML7PMwfB+i/GoinNwdKr8jEJk/8sHqGsFU6Bg=";
	public static final String deviceId = "device1";

	public static final String region = "East US";
	public static final String plant = "Redmond43";

	public static void main(String[] args) throws IOException {
		// Get the DeviceTwin and DeviceTwinDevice objects
		DeviceTwin twinClient = DeviceTwin.createFromConnectionString(iotHubConnectionString);
		DeviceTwinDevice device = new DeviceTwinDevice(deviceId);

		try {
			// Get the device twin from IoT Hub
			System.out.println("Device twin before update:");
			twinClient.getTwin(device);
			System.out.println(device);

			// Update device twin tags if they are different
			// from the existing values
			String currentTags = device.tagsToString();
			if ((!currentTags.contains("region=" + region) && !currentTags.contains("plant=" + plant))) {
				// Create the tags and attach them to the DeviceTwinDevice object
				Set<Pair> tags = new HashSet<Pair>();
				tags.add(new Pair("region", region));
				tags.add(new Pair("plant", plant));
				device.setTags(tags);

				// Update the device twin in IoT Hub
				System.out.println("Updating device twin");
				twinClient.updateTwin(device);
			}

			// Retrieve the device twin with the tag values from IoT Hub
			System.out.println("Device twin after update:");
			twinClient.getTwin(device);
			System.out.println(device);

			// Query the device twins in IoT Hub
			System.out.println("Devices in Redmond:");

			// Construct the query
			SqlQuery sqlQuery = SqlQuery.createSqlQuery("*", SqlQuery.FromType.DEVICES, "tags.plant='Redmond43'", null);

			// Run the query, returning a maximum of 100 devices
			Query twinQuery = twinClient.queryTwin(sqlQuery.getQuery(), 100);
			while (twinClient.hasNextDeviceTwin(twinQuery)) {
				DeviceTwinDevice d = twinClient.getNextDeviceTwin(twinQuery);
				System.out.println(d.getDeviceId());
			}

			System.out.println("Devices in Redmond using a cellular network:");

			// Construct the query
			sqlQuery = SqlQuery.createSqlQuery("*", SqlQuery.FromType.DEVICES,
					"tags.plant='Redmond43' AND properties.reported.connectivityType = 'cellular'", null);

			// Run the query, returning a maximum of 100 devices
			twinQuery = twinClient.queryTwin(sqlQuery.getQuery(), 3);
			while (twinClient.hasNextDeviceTwin(twinQuery)) {
				DeviceTwinDevice d = twinClient.getNextDeviceTwin(twinQuery);
				System.out.println(d.getDeviceId());
			}
		} catch (IotHubException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
