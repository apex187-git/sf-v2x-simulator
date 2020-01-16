package com.skt.v2x.simulator;

import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.skt.v2x.simulator.data.MqttDataProcessor;
import com.skt.v2x.simulator.mqtt.EmqPushProcessor;

//@SpringBootApplication
public class EmqPushMain {
	@Autowired private MqttDataProcessor mqttDataProcessor;
	@Autowired private EmqPushProcessor emqPushProcessor;
	
	@PostConstruct
	public void appStart() {
		LinkedBlockingQueue<String> dataQueue = new LinkedBlockingQueue<>();
		
		emqPushProcessor.setDataQueue(dataQueue);
		
		new Thread(emqPushProcessor).start();
		
		mqttDataProcessor.execute(dataQueue);
	}
	
    public static void main(String[] args) {
    	SpringApplication.run(EmqPushMain.class, args);
    }
}