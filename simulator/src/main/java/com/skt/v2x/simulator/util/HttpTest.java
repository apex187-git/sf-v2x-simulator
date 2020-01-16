package com.skt.v2x.simulator.util;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

public class HttpTest {
	public static void getHttps(String urlString) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		// Get HTTPS URL connection
		URL url = new URL(urlString);
		HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
		// Set Hostname verification
		conn.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				// Ignore host name verification. It always returns true.
				return true;
			}
		});
		// SSL setting
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, null, null);
		// No validation for now
		conn.setSSLSocketFactory(context.getSocketFactory());
		// Connect to host
		
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		
		String param = "'{\r\n" + 
				"    \"header\" : {\r\n" + 
				"        \"interfaceId\" : \"IF1005\",\r\n" + 
				"        \"cnt\" : 1\r\n" + 
				"    },\r\n" + 
				"    \"body\" : [\r\n" + 
				"        {\r\n" + 
				"            \"seq\" : 1,\r\n" + 
				"           \"centerId\" : \"CT0000000001\",\r\n" + 
				"           \"centerName\" : \"도공\",\r\n" + 
				"            \"dsrSeq\" : \"DSR00011112211\",\r\n" + 
				"            \"eventType\" : \"9001\",\r\n" + 
				"            \"eventStartTime\" : \"20181011201020\",\r\n" + 
				"            \"eventEndTime\" : \"\",\r\n" + 
				"           \"dsrLatitude\" : 37.408845,\r\n" + 
				"           \"dsrLongitude\" : 127.11561,\r\n" + 
				"           \"highwayYn\" : \"Y\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		
		//System.out.println(conn.getHeaderFields());
		
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	    wr.writeBytes(param);
	    wr.flush();
	    wr.close();
		
		conn.connect();
		conn.setInstanceFollowRedirects(true);
		// Print response from host
		InputStream in = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.printf("%s\n", line);
		}
		reader.close();
	}
	
	public static void getHttp(String urlString) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		//String param = new String("{\"header\":{\"interfaceId\":\"IF1005\",\"cnt\":1},\"body\":[{\"seq\":1,\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"dsrSeq\" : \"DSR0001111221\",\"eventType\" : \"9001\",\"eventStartTime\" : \"20181011201020\",\"eventEndTime\" : \"\",\"dsrLatitude\" : 37.408845,\"dsrLongitude\" : 127.11561,\"highwayYn\" :\"Y\"}]}".getBytes(), "UTF-8");

		//param = param.replaceAll(" ", "");
		/*
		String param = "{\r\n" + 
				"    \"header\" : {\r\n" + 
				"        \"interfaceId\" : \"IF1005\",\r\n" + 
				"        \"cnt\" : 1\r\n" + 
				"    },\r\n" + 
				"    \"body\" : [\r\n" + 
				"        {\r\n" + 
				"            \"seq\" : 1,\r\n" + 
				"           \"centerId\" : \"CT0000000001\",\r\n" + 
				"           \"centerName\" : \"한국 도로 공사\",\r\n" + 
				"            \"dsrSeq\" : \"DSR00011112211\",\r\n" + 
				"            \"eventType\" : \"9001\",\r\n" + 
				"            \"eventStartTime\" : \"20181011201020\",\r\n" + 
				"            \"eventEndTime\" : \"\",\r\n" + 
				"           \"dsrLatitude\" : 37.408845,\r\n" + 
				"           \"dsrLongitude\" : 127.11561,\r\n" + 
				"           \"highwayYn\" : \"Y\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
				*/
		String param = "{\"header\":{\"interfaceId\":\"IF1001\",\"cnt\":8},\"body\":[{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0004\",\"eventType\":1793,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.295934,\"eventLongitude\":127.103479,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.295843,\"eventLongitude\":127.103484,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.295689,\"eventLongitude\":127.103484,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0005\",\"eventType\":534,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.271203,\"eventLongitude\":127.103594,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.271157,\"eventLongitude\":127.103616,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.270856,\"eventLongitude\":127.103654,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0006\",\"eventType\":1286,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.247837,\"eventLongitude\":127.103742,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.2478,\"eventLongitude\":127.10374,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.247736,\"eventLongitude\":127.103732,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0009\",\"eventType\":258,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.303084,\"eventLongitude\":127.10374,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.303352,\"eventLongitude\":127.103716,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.303437,\"eventLongitude\":127.103716,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0010\",\"eventType\":1793,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.329199,\"eventLongitude\":127.103637,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.329412,\"eventLongitude\":127.103628,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.329524,\"eventLongitude\":127.103634,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0011\",\"eventType\":534,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.351909,\"eventLongitude\":127.103495,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.352159,\"eventLongitude\":127.103496,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.352289,\"eventLongitude\":127.103496,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0012\",\"eventType\":1286,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.373487,\"eventLongitude\":127.103336,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.373611,\"eventLongitude\":127.103331,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.373677,\"eventLongitude\":127.10335,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100T0007\",\"eventType\":258,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.324769,\"eventLongitude\":127.103322,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.324707,\"eventLongitude\":127.103326,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.324631,\"eventLongitude\":127.103343,\"eventElevation\":37.7585,\"eventHeading\":126.78}]}]}";
		
		// Get HTTPS URL connection
		URL url = new URL(urlString);
		//HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		//conn.setRequestProperty("Connection", "close");
		//conn.setRequestProperty("Accept", "application/json");
		//conn.setRequestProperty("Content-Length", param.length() + "");
		//conn.setUseCaches(false);
		
		// 연결 타임아웃 설정 
        //conn.setConnectTimeout(3000); // 3초
        // 읽기 타임아웃 설정 
       //conn.setReadTimeout(3000); // 3초
		
		//System.out.println("Response Code was " + conn.getResponseCode());
        
		//System.out.println(param);
		conn.setDoOutput(true);
		//conn.setDoInput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.write(param.getBytes("UTF-8"));
	    //wr.writeBytes(param);
	    wr.flush();
	    
	    try {if(wr != null) wr.close();} catch(Exception e) {e.printStackTrace();}
		
	    //System.out.println(conn.getHeaderFields());
	    
	    conn.connect();
		conn.setInstanceFollowRedirects(true);
		
		// Print response from host
		InputStream in;
		
		if (conn.getResponseCode() >= 400) {
			in = conn.getErrorStream();
		} else {
			in = conn.getInputStream();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			System.out.printf("getHttp : %s\n", line);
		}
		
		try {if(reader != null) reader.close();} catch(Exception e) {e.printStackTrace();}
		try {if(in != null) in.close();} catch(Exception e) {e.printStackTrace();}
		try {if(conn != null) conn.disconnect();} catch(Exception e) {e.printStackTrace();}
	}
	
	public static void getHttpByURLConnection(String urlString) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		//String param = "{\"header\":{\"interfaceId\":\"IF1001\",\"cnt\":8},\"body\":[{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0004\",\"eventType\":1793,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.295934,\"eventLongitude\":127.103479,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.295843,\"eventLongitude\":127.103484,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.295689,\"eventLongitude\":127.103484,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0005\",\"eventType\":534,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.271203,\"eventLongitude\":127.103594,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.271157,\"eventLongitude\":127.103616,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.270856,\"eventLongitude\":127.103654,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0006\",\"eventType\":1286,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.247837,\"eventLongitude\":127.103742,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.2478,\"eventLongitude\":127.10374,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.247736,\"eventLongitude\":127.103732,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0009\",\"eventType\":258,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.303084,\"eventLongitude\":127.10374,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.303352,\"eventLongitude\":127.103716,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.303437,\"eventLongitude\":127.103716,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0010\",\"eventType\":1793,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.329199,\"eventLongitude\":127.103637,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.329412,\"eventLongitude\":127.103628,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.329524,\"eventLongitude\":127.103634,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0011\",\"eventType\":534,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.351909,\"eventLongitude\":127.103495,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.352159,\"eventLongitude\":127.103496,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.352289,\"eventLongitude\":127.103496,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0012\",\"eventType\":1286,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.373487,\"eventLongitude\":127.103336,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.373611,\"eventLongitude\":127.103331,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.373677,\"eventLongitude\":127.10335,\"eventElevation\":37.7585,\"eventHeading\":126.78}]},{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100T0007\",\"eventType\":258,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.324769,\"eventLongitude\":127.103322,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.324707,\"eventLongitude\":127.103326,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.324631,\"eventLongitude\":127.103343,\"eventElevation\":37.7585,\"eventHeading\":126.78}]}]}";
		String param = "{\"header\":{\"interfaceId\":\"IF1001\",\"cnt\":1},\"body\":[{\"centerId\":\"CT0000000001\",\"centerName\":\"한국 도로 공사\",\"eventId\":\"EI100N0004\",\"eventType\":1793,\"eventTime\":\"20190306121020\",\"eventLocation\":[{\"eventLatitude\":37.295934,\"eventLongitude\":127.103479,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.295843,\"eventLongitude\":127.103484,\"eventElevation\":37.7585,\"eventHeading\":126.78},{\"eventLatitude\":37.295689,\"eventLongitude\":127.103484,\"eventElevation\":37.7585,\"eventHeading\":126.78}]}]}";
		
		URL url = new URL(urlString);
		//URLConnection connection = url.openConnection();
		URLConnection connection = url.openConnection();
		
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/json");
		
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(),"UTF-8");
		out.write(param);
		out.close();
		
		//System.out.println(connection.getHeaderFields());
		
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
		String responsedata = in.readLine();
		System.out.println(new Date() + " URLConnection response data : " + responsedata);
		in.close();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(System.currentTimeMillis());
		try {
			 while(true) {
				 Thread.sleep(1000);
				 
				 //getHttpByURLConnection("http://192.168.203.101:29999/api/vse/warning");
				//getHttpByURLConnection("http://211.188.149.5:29999/api/vse/warning");
				 //getHttpByURLConnection("http://smartfleet.sktelecom.com:39999/api/vse/warning");
				 //getHttpByURLConnection("http://192.168.203.101:29999/api/vse/warning");
				 //getHttpByURLConnection("http://smartfleet.sktelecom.com:19999/api/vse/warning");
				 //getHttpByURLConnection("http://211.188.149.5:29999/api/vse/warning");
				 //header = getHttpByURLConnection("http://192.168.203.101:29999/api/vse/warning", header);
				 //getHttpByURLConnection("http://211.188.149.5:29999/api/vse/warning");
				 //getHttp("http://192.168.203.101:29999/api/vse/warning");
				 //getHttp("http://211.188.148.44:29999/api/vse/warning");
			 }
			 
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}