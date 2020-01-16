package com.skt.v2x.simulator;

import static org.toilelibre.libe.curl.Curl.$;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class KafkaDataCheckerMain {
	public static String params[]; //hook,payload.ts,clientID,topic
	
	public static final int HOOK_INDEX = 8;
	public static final int PAYLOAD_TS_INDEX = 5;
	public static final int CLIENTID_INDEX = 12;
	public static final int TOPIC_INDEX = 9;
	public static final int HITS_TOTAL_INDEX = 16;

	@PostConstruct
	public void appStart() throws Exception {
		if (params.length != 1) {
			System.err.println("Usage : kafkaDataChecker.sh filePath");

			System.exit(0);
		}
		
		//String filePath = "C:\\Users\\ff\\Desktop\\ela_data";
		//String indexName = "ewewe";
		
		String filePath = params[0];
		String tempString = "";
		
		long readLineCount = 0; // 읽은 라인 갯수
		long matchCount = 0; // 쿼리조회 매치갯수
		long notMatchCount = 0; // 쿼리조회 매치되지 않은 갯수
		long doubleMatchCount = 0; // 쿼리조회결과 2건 이상
		long queryFieldValueNotExistCount = 0; // 쿼리조회 조건값이 없는 경우 갯수
		
		log.info("================ execute args ================");
		log.info("filePath : {}", filePath);
		log.info("=========================================");

		BufferedReader br = null;
		
		//curl -XGET --insecure -u admin:sf.123$ https://172.27.100.17:9200/v2v-sf-2020.01.*/logs/_search?pretty -H "Content-Type:application/json;charset=utf-8" -d '{
		//	"query": {
		//	    "bool": {
		//	      "must": [
		//	        { "match": { "hook": "message.publish" } },
		//	        { "match": { "payload.ts": 1578991124283 } },
		//	        { "match": { "clientID": "e6468ba3-bdb8-46b0-a674-cf125cdc340a" } },
		//	        { "match": { "topic": "trelog-received" } }
		//	      ]
		//	    }
		//	  }
		//	}'
		
		try {// hook,payload.ts,clientID,topic
			br = new BufferedReader(new FileReader(filePath));
			int index = 0;
			int subIndex = 0;
			
			String url = "curl -XPOST --insecure -u admin:sf.123$ https://172.27.100.17:9200/v2v-sf-2020.*/logs/_search -H \"Content-Type:application/json;charset=utf-8\" -d ";
			String data = "'{\"query\":{\"bool\":{\"must\":[{\"match\":{\"hook\":\"<<hook>>\"}},{\"match\":{\"payload.ts\":<<payload.ts>>}},{\"match\":{\"clientID\":\"<<clientID>>\"}},{\"match\":{\"topic\":\"<<topic>>\"}}]}}}'";
			String command = url + data;
			
			String hook = "";
			String payloadTs = "";
			String clientId = "";
			String topic = "";
			String hitsTotal = "";

			while ((tempString = br.readLine()) != null) {
				log.info("================ row data parsing start ================");
				
				readLineCount++;
				
				// hook 필드값 추출
				index = tempString.indexOf("\"hook\"");
				
				if(index != -1) {
					subIndex = tempString.indexOf("\"", index + HOOK_INDEX);
					hook = tempString.substring(index + HOOK_INDEX, subIndex);
					
					log.info("hook : {}", hook);
				} else {
					queryFieldValueNotExistCount++;
					continue;
				}
				
				// clientID 필드값 추출
				index = tempString.indexOf("\"clientID\"", subIndex + 1);
				
				if(index != -1) {
					subIndex = tempString.indexOf("\"", index + CLIENTID_INDEX);
					clientId = tempString.substring(index + CLIENTID_INDEX, subIndex);
					
					log.info("clientID : {}", clientId);
				} else {
					queryFieldValueNotExistCount++;
					continue;
				}
				
				// topic 필드값 추출
				index = tempString.indexOf("\"topic\"", subIndex + 1);
				
				if(index != -1) {
					subIndex = tempString.indexOf("\"", index + TOPIC_INDEX);
					topic = tempString.substring(index + TOPIC_INDEX, subIndex);
					
					log.info("topic : {}", topic);
				} else {
					queryFieldValueNotExistCount++;
					continue;
				}
				
				// payload.ts 필드값 추출
				index = tempString.indexOf("\"payload\"", subIndex + 1);
				
				if(index != -1) {
					index = tempString.indexOf("\"ts\"", index + 1);
					
					if(index != -1) {
						subIndex = index + 18;
						payloadTs = tempString.substring(index + PAYLOAD_TS_INDEX, subIndex);
						
						log.info("payloadTs : {}", payloadTs);
					} else {
						queryFieldValueNotExistCount++;
						continue;
					}
				} else {
					queryFieldValueNotExistCount++;
					continue;
				}
				
				String curlCommand = command.replaceFirst("<<hook>>", hook).replaceFirst("<<payload.ts>>", payloadTs).replaceFirst("<<clientID>>", clientId).replaceFirst("<<topic>>", topic);
				
				log.info("http curlCommand : {}", curlCommand);
				
				String curlResult = $(curlCommand);
				
				log.info("curlResult : {}", curlResult);
				
				// hits.total 필드값 추출
				index = curlResult.indexOf("\"hits\":{\"total\":");
				
				if(index != -1) {
					subIndex = curlResult.indexOf(",", index + HITS_TOTAL_INDEX);
					hitsTotal = curlResult.substring(index + HITS_TOTAL_INDEX, subIndex).trim();
					
					log.info("hitsTotal : {}", hitsTotal);
					
					if(hitsTotal.equals("0")) {
						notMatchCount++;
					} else if(hitsTotal.equals("1")) {
						matchCount++;
					} else {
						doubleMatchCount++;
					}
				} else {
					log.error("curlResult parsing content not find!!!!!!!!!!!!!!!!");
					continue;
				}
				
				log.info("================ row data parsing end ================");
			}
		} catch (Exception e) {
			log.error("file parsing fali!!", e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		log.info("===============================================");
		log.info("readLineCount : {}", readLineCount);
		log.info("matchCount : {}", matchCount);
		log.info("notMatchCount : {}", notMatchCount);
		log.info("doubleMatchCount : {}", doubleMatchCount);
		log.info("queryFieldValueNotExistCount : {}", queryFieldValueNotExistCount);
		log.info("===============================================");
	}

	public static void main(String[] args) {
		params = args;
		SpringApplication.run(KafkaDataCheckerMain.class, args);
	}
}