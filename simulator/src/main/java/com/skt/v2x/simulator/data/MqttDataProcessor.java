package com.skt.v2x.simulator.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MqttDataProcessor {
	@Value("${mqtt.data.path}") private String dataFilePath;
	@Value("${mqtt.data.queue.put.sleep.milli}") private int sleepTimeMilli;
	
	public void execute(LinkedBlockingQueue<String> dataQueue) {
        long readStart = System.currentTimeMillis();
        
        //Gson gson = new Gson();

        BufferedReader br = null;
        List<String> v2XDataList = new ArrayList<String>();
        
        try {
            br = new BufferedReader(new FileReader(dataFilePath));

            while(true) {
                String line = br.readLine();
                
                if(line == null) break;
                
                //System.out.println("line : " + line);
                
                v2XDataList.add(line);
            }

        }catch (Exception e) {
            log.error("file parsing fali!!", e);
        }finally {
            try {
                if (br != null) br.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        long readEnd = System.currentTimeMillis();
        
        log.info("data file read time(sec) : {}", (readEnd - readStart) /1000);
        //log.info("v2XDataList : {}", v2XDataList);
        
        try {
            while (true) {
                int dataSize = v2XDataList.size();

                for (int i = 0; i < dataSize; i++) {
                	dataQueue.put(v2XDataList.get(i).replaceFirst("\\d{13}", new Date().getTime() + ""));
                	
                	Thread.sleep(sleepTimeMilli);
                }
            }
        }catch (Exception e){
        	log.error("data queue input fali!!", e);
        }
	}
}
