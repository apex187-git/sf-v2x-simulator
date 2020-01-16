package com.skt.v2x.simulator;

import static org.toilelibre.libe.curl.Curl.$;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skt.v2x.simulator.domain.HealthInfoDomain;
import com.skt.v2x.simulator.domain.InterfaceStatListDomain;
import com.skt.v2x.simulator.util.InfluxDbUtil;

import lombok.extern.slf4j.Slf4j;

// https://github.com/libetl/curl

@Slf4j
//@SpringBootApplication
public class HealthCheckerMain {
	@Value("${influx.db.url}") private String influxDbUrl;
	@Value("${influx.db.user.name}") private String influxUserName;
	@Value("${influx.db.password}") private String influxDbPassword;
	@Value("${influx.db.database}") private String influxDbDatabase;
	@Value("${influx.db.health.measurement}") private String influxDbHealthMeasurement;
	@Value("${influx.db.stat.measurement}") private String influxDbStatMeasurement;
	@Value("${influx.db.retention.policy}") private String influxDbRetentionPolicy;
	
	@Value("${vse.health.check.url}") private String vseCheckUrl;
	@Value("${vse.health.check.param}") private String vseCheckParam;
	
	@Value("${cits.kec.check.url}") private String citsKecCheckUrl;
	@Value("${cits.kec.check.param}") private String citsKecCheckParam;
	
	@Value("${vse.stat.check.url}") private String vseStatCheckUrl;
	@Value("${cits.kec.stat.check.url}") private String citsKecStatCheckUrl;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private static String[] appArgs;

	@PostConstruct
	public void appStart() throws Exception {
		if(appArgs[0].equals("health")) {
			if(appArgs[1].equals("service-extension")) {
				doHealthCheck("service-extension", vseCheckUrl, vseCheckParam);
			} else if(appArgs[1].equals("cits-kec")) {
				doHealthCheck("cits-kec", citsKecCheckUrl, citsKecCheckParam);
			}
		} else {
			if(appArgs[1].equals("service-extension")) {
				doInterfaceCheck("service-extension", vseStatCheckUrl);
			} else if(appArgs[1].equals("cits-kec")) {
				doInterfaceCheck("cits-kec", citsKecStatCheckUrl);
			}
		}
	}
	
	private void doInterfaceCheck(String service, String url) {
		InfluxDbUtil influxUtil = new InfluxDbUtil();
		InterfaceStatListDomain interfaceStatListDomain = null;
		String result = "";
		
		try {
			// vse health check
			String command = "curl " + url + " -X POST -H \"Content-Type:application/json;charset=utf-8\"";
			
			log.info("{} stat check command : {}", service, command);
			
			result = $(command);
			
			log.info("result : {}", result);
			
			if(result != null && !result.equals("")) {
				interfaceStatListDomain = objectMapper.readValue(result, InterfaceStatListDomain.class);
			}
		} catch(Exception e) {
			log.error(service + " stat check curl error!!", e);
		}
		
		if(interfaceStatListDomain != null) {
			influxUtil.insertStatCheckData(influxDbUrl, influxUserName, influxDbPassword, influxDbDatabase, 
					influxDbRetentionPolicy, influxDbStatMeasurement, service, interfaceStatListDomain);
		}
	}
	
	private void doHealthCheck(String service, String url, String param) {
		InfluxDbUtil influxUtil = new InfluxDbUtil();
		HealthInfoDomain healthInfo;
		String result = "";
		
		try {
			// vse health check
			String command = "curl " + url + " -H \"Content-Type:application/json;charset=utf-8\" -d '" + param + "'";
			
			log.info("{} health check command : {}", service, command);
			
			result = $(command);
			
			log.info("result : {}", result);
			
			if(result != null && !result.equals("")) {
				healthInfo = objectMapper.readValue(result, HealthInfoDomain.class);
			} else {
				healthInfo = new HealthInfoDomain();
			}
		} catch(Exception e) {
			healthInfo = new HealthInfoDomain();
			
			log.error(service + " health check curl error!!", e);
		}
		
		influxUtil.insertHealthCheckData(influxDbUrl, influxUserName, influxDbPassword, influxDbDatabase, 
				influxDbRetentionPolicy, influxDbHealthMeasurement, service, healthInfo);
	}

	public static void main(String[] args) {
		appArgs = args;
		//appArgs = new String[] {"stat"};
		//appArgs = new String[] {"health"};
		
		if(appArgs == null || appArgs.length < 2) {
			log.error("app argument required!!");
			System.exit(1);
		}
		
		SpringApplication.run(HealthCheckerMain.class, args);
	}
}