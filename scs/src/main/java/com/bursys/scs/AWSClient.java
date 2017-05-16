package com.bursys.scs;

import java.nio.charset.Charset;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.util.AWSUtil;
import com.amazonaws.services.iot.client.util.CommandArguments;
import com.amazonaws.services.iot.client.util.AWSUtil.KeyStorePasswordPair;

public class AWSClient {
	
	 private static AWSIotMqttClient awsIotClient;
	 				
		public static void initClient(String[] args) throws Exception {
			
			CommandArguments arguments = CommandArguments.parse(args);
	        String clientEndpoint = arguments.getNotNull("clientEndpoint", AWSUtil.getConfig("clientEndpoint"));
	        String clientId = arguments.getNotNull("clientId", AWSUtil.getConfig("clientId"));

	        String certificateFile = arguments.get("certificateFile", AWSUtil.getConfig("certificateFile"));
	        String privateKeyFile = arguments.get("privateKeyFile", AWSUtil.getConfig("privateKeyFile"));
	        if (awsIotClient == null && certificateFile != null && privateKeyFile != null) {
	            String algorithm = arguments.get("keyAlgorithm", AWSUtil.getConfig("keyAlgorithm"));
	            KeyStorePasswordPair pair = AWSUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile, algorithm);

	            awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
	        }

	        if (awsIotClient == null) {
	            String awsAccessKeyId = arguments.get("awsAccessKeyId", AWSUtil.getConfig("awsAccessKeyId"));
	            String awsSecretAccessKey = arguments.get("awsSecretAccessKey", AWSUtil.getConfig("awsSecretAccessKey"));
	            String sessionToken = arguments.get("sessionToken", AWSUtil.getConfig("sessionToken"));

	            if (awsAccessKeyId != null && awsSecretAccessKey != null) {
	                awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey,
	                        sessionToken);
	            }
	        }

	        if (awsIotClient == null) {
	            throw new IllegalArgumentException("Failed to construct client due to missing certificate or credentials.");
	        }
	        	      
			awsIotClient.setWillMessage(new AWSIotMessage("client/disconnect", AWSIotQos.QOS0, awsIotClient.getClientId()));
	        awsIotClient.connect();
	    }
		
		
		
		public static void sendMessage(String line) {
			
			if(awsIotClient!=null){
				Sorter sorter = new Sorter("SCS_MESSAGE",AWSIotQos.QOS0);
				sorter.setPayload(Charset.forName("UTF-8").encode(line).array());
				try {
					awsIotClient.publish(sorter);
				} catch (AWSIotException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

}
