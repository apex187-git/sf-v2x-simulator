package com.skt.v2x.simulator;

import static org.toilelibre.libe.curl.Curl.$;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

// https://github.com/libetl/curl

@Slf4j
//@SpringBootApplication
public class CitsCurlMain {
	@Value("${cits.curl.file.path}")
	private String dataFilePath;
	public static String params[];

	@PostConstruct
	public void appStart() throws Exception {
		if (params.length != 2) {
			System.err.println("Usage : citsCurl.start fileName sleepMilli");

			System.exit(0);
		}

		String fileName = params[0];
		long sleepMilli = Long.parseLong(params[1]);
		
		log.info("fileName : {}, sleepMilli : {}", fileName, sleepMilli);

		BufferedReader br = null;
		StringBuilder curlContents = new StringBuilder(1024);

		try {
			br = new BufferedReader(new FileReader(dataFilePath + fileName));

			while (true) {
				String line = br.readLine();

				if (line == null)
					break;
				if (line.charAt(0) == '#')
					continue;

				// System.out.println("line : " + line);
				// System.out.println("line : " + line.trim());

				curlContents.append(line.trim());
			}
		} catch (Exception e) {
			log.error("file parsing fali!!", e);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String url = "";
		String content = "";
		String fullContent = curlContents.toString();

		url = fullContent.substring(fullContent.indexOf("http"), fullContent.indexOf("-H") - 1);
		content = fullContent.substring(fullContent.indexOf("-d") + 3, fullContent.length());

		String command = "curl " + url + " -H \"Content-Type:application/json;charset=utf-8\" -d " + content;
		
		log.info("command : {}", command);
		
		while(true) {
			try {
				log.info("command execute!!");
				
				$(command);
				
				Thread.sleep(sleepMilli);
			} catch(Exception e) {
				log.error("curl error!!", e);
			}
		}
	}

	public static void main(String[] args) {
		params = args;
		SpringApplication.run(CitsCurlMain.class, args);
	}
}

/*
 * 
 * // System.out.println("fullContent : " + fullContent);

		url = fullContent.substring(fullContent.indexOf("http"), fullContent.indexOf("-H") - 1);
		content = fullContent.substring(fullContent.indexOf("-d") + 4, fullContent.length());

		// System.out.println("curl " + url + " -H
		// \"Content-Type:application/json;charset=utf-8\" -d " + content);

		// String command = "curl " + url + " -H
		// \"Content-Type:application/json;charset=utf-8\" -d '" + content + "'";
		// String command = "curl " + url + " -v -X POST -H
		// \"Content-Type:application/json;charset=utf-8\" -d " +
		// content.replaceAll("\\r\\n", "");
		String command = "curl https://smartfleet.sktelecom.com:29999/api/vse/car/stop -H \"Content-Type:application/json;charset=utf-8\" -d '{\"header\":{\"interfaceId\":\"IF1003\",\"cnt\":1},\"body\":[{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI1000111134\",\"eventType\":534,\"eventTime\":\"20181011231020\",\"eventLocation\":[{\"eventLatitude\":37.385486,\"eventLongitude\":127.102738,\"eventElevation\":37.758500,\"eventHeading\":126.780000},{\"eventLatitude\":37.385486,\"eventLongitude\":127.102738,\"eventElevation\":37.758500,\"eventHeading\":126.780000},{\"eventLatitude\":37.385486,\"eventLongitude\":127.102738,\"eventElevation\":37.758500,\"eventHeading\":126.780000}]}]}'";

		// System.out.println("url : " + url);
		// System.out.println("content : " + content);
		// System.out.println("command : " + command.replaceAll("\n",
		// "").replaceAll("\t", ""));
		log.info("command : {}", command);
		// log.info("command : {}, content : {}", command, content);
 * int exitCode = -12345;
 * 
 * ByteArrayOutputStream baos = null; PumpStreamHandler streamHandler = null;
 * 
 * try { DefaultExecutor executor = new DefaultExecutor(); baos = new
 * ByteArrayOutputStream();
 * 
 * streamHandler = new PumpStreamHandler(baos);
 * 
 * executor.setStreamHandler(streamHandler);
 * 
 * //CommandLine cmdLine = CommandLine.parse("netstat"); CommandLine cmdLine =
 * CommandLine.parse(command);
 * 
 * 
 * cmdLine.addArgument(url); cmdLine.addArgument("-X");
 * cmdLine.addArgument("POST"); cmdLine.addArgument("-H");
 * cmdLine.addArgument("\"Content-Type:application/json;charset=utf-8\"");
 * cmdLine.addArgument("-d"); cmdLine.addArgument(content);
 * 
 * 
 * exitCode = executor.execute(cmdLine); } catch(Exception e) {
 * e.printStackTrace(); }
 * 
 * log.info("exitCode : {}, result : {}", exitCode, baos.toString());
 * 
 * try {if(streamHandler != null) streamHandler.stop();}catch(Exception e) {}
 * try {if(baos != null) baos.close();}catch(Exception e) {}
 */