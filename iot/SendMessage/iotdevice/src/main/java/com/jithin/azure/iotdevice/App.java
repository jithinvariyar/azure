package com.jithin.azure.iotdevice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;

/**
 * Hello world!
 *
 */
public class App 
{
	private static String connString = "HostName=jithin-iot-hub-demo.azure-devices.net;DeviceId=device1;SharedAccessKey=Xtv19lPcJzB1ogLaQzGAuTwDewu273WkqOMxw2PywDc=";
	private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
	private static String deviceId = "device1";
    public static void main( String[] args ) throws IllegalArgumentException, URISyntaxException, IOException
    {
		DeviceClient client = new DeviceClient(connString, protocol);
		client.open();
		sendSingleMessage(client);
    }
    
    private static void sendSingleMessage(DeviceClient deviceClient) throws UnsupportedEncodingException, JsonProcessingException
	{
	    String jsonData = "{ \"single\":true }";
	    
	    Message msg = new Message(jsonData.getBytes());
	    msg.setProperty("level", "storage");
	 
	    deviceClient.sendEventAsync(msg, new DeviceStatusCallBack(), null);
	 
	    System.out.println("A single message is sent");
	}
    
    protected static class DeviceStatusCallBack implements IotHubEventCallback {
		public void execute(IotHubStatusCode responseStatus, Object callbackContext) {
			System.out.println("IoT Hub responded to device message with status " + responseStatus.name());
		}
	}
}
