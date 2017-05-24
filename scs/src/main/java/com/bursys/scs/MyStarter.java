package com.bursys.scs;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.util.AWSUtil;
import com.amazonaws.services.iot.client.util.AWSUtil.KeyStorePasswordPair;
import com.amazonaws.services.iot.client.util.CommandArguments;

public class MyStarter {
	
	
	
	public static void main(String[] args) {
		try {
			
			//AWSClient.initClient(args);
			KalfaClient.initialize();
			while(true){
				loadFile("C:\\Users\\admin\\Desktop\\AWS\\log\\dhl3.log");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static void loadFile(String fileName) {
	
		
		try(BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
		
			String line = null;
			String msg[]=null;
			
			while ((line = reader.readLine()) != null) {
				if(line!="") {
					msg = line.split("From SCS -");
				}
				if(msg.length>1) {
					line = msg[1].trim();
					/*line = msg[1].substring(msg[1].indexOf("LENGTH"));
					
					
					Thread.sleep(1000);
					line=line.replaceAll("=", "\":");
					line=line.replaceAll(",", ",\"");
					line="{\""+line.trim()+"}";*/
					Thread.sleep(100);
					line=convertToJson(line).replaceAll("[^a-zA-Z0-9\\s,\":\\}\\{+]", "");
					System.out.println(line+",");		
					
					AWSClient.sendMessage(line);
					KalfaClient.publishMesssage(line);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String convertToJson(String line) {	
		String tokens[]=line.split(",");
		String message="{\"TYPE\":\""+tokens[0]+"\"";
		message+=",\"COUNTER\":\""+tokens[1]+"\"";
		message+=",\"TIME_STAMP\":\""+tokens[2]+"\"";
		
		for(int i=3;i<tokens.length;i++){
			String object[]=tokens[i].split("=");
			object[0]=(object[0].startsWith("2"))?"COL"+object[0]:object[0];
			
			String data="\""+object[0]+"\"";
			
			
			if (!object[1].startsWith("0") && object[1].matches("[0-9.]+")){
				data+=":"+object[1];
			}else{
				data+=":\""+object[1]+"\"";
			}
			message+=","+data;
		}
		
		return message+"}";
	}

}
