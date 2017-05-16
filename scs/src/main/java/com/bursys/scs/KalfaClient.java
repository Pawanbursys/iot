package com.bursys.scs;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KalfaClient {
	
	private static Producer<Integer, String> producer;
    private static final String topic= "test";

    public static void initialize() {
    	
          Properties producerProps = new Properties();
          producerProps.put("metadata.broker.list", "localhost:9092");
          producerProps.put("serializer.class", "kafka.serializer.StringEncoder");
          producerProps.put("request.required.acks", "1");
          ProducerConfig producerConfig = new ProducerConfig(producerProps);
          producer = new Producer<Integer, String>(producerConfig);
    }
    public static void publishMesssage(String msg) throws Exception{            

        if(producer!=null){
	    	KeyedMessage<Integer, String> keyedMsg = new KeyedMessage<Integer, String>(topic, msg);
	        producer.send(keyedMsg); // This publishes message on given topic
        }
       
    }

}
