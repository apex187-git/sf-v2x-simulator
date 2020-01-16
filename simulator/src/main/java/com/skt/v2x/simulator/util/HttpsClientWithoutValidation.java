package com.skt.v2x.simulator.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

public class HttpsClientWithoutValidation {  
   
 /** 
  *  
  * @param urlString 
  * @throws IOException 
  * @throws NoSuchAlgorithmException 
  * @throws KeyManagementException 
  */  
 public void getHttps(String urlString) throws IOException, NoSuchAlgorithmException, KeyManagementException {  
    
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
  context.init(null, null, null);  // No validation for now  
  conn.setSSLSocketFactory(context.getSocketFactory());  
    
  conn.setRequestMethod("POST");
  conn.setRequestProperty("Content-Type", "application/json");
	
  String param = "{\"header\":{\"cnt\":1},\"body\":[{\"seq\":1,\"dsrSeq\":\"TEST111111111111111\",\"fstDsrDtime\":\"20171012000121\",\"statEndDtime\":\"\",\"dsrKndCd\":\"0040003\",\"dsrKndCdName\":\"구급\",\"dsrLatitude\":37.408845,\"dsrLongitude\":127.11561,\"highwayYn\":\"Y\"}]}";
 
  conn.setDoOutput(true);
  DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
  wr.write(param.getBytes("UTF-8"));
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
   
 /** 
  *  
  * @param args 
  * @throws Exception 
  */  
 public static void main(String[] args) throws Exception {
	HttpsClientWithoutValidation test = new HttpsClientWithoutValidation();
	
	try {
		test.getHttps("https://smartfleet.sktelecom.com:19999/api/vse/warning");  
  	//test.getHttps(URLEncoder.encode("https://smartfleet.sktelecom.com:19999/api/vse/warning", "UTF-8"));  
	} catch(Exception e) {
		e.printStackTrace();
	}
 }  
}  