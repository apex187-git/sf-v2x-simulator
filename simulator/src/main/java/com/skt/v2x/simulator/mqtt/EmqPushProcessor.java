package com.skt.v2x.simulator.mqtt;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.skt.v2x.simulator.domain.V2xDomain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmqPushProcessor implements Runnable {
	@Value("${mqtt.broker}") private String brokerUrl;
	@Value("${mqtt.client.use.limit.count}") private int mqttClientLimitCount;
	@Value("${mqtt.qos}") private int qos;
	@Value("${mqtt.username}") private String username;
	@Value("${mqtt.password}") private String password;
	@Value("${mqtt.ca.file.path}") private String caFilePath;
	@Value("${mqtt.client.crtfile.path}") private String crtFilePath;
	@Value("${mqtt.client.key.file.path}") private String keyFilePath;
	@Value("${mqtt.ssl}") private String sslEnable;
    
	@Getter @Setter private LinkedBlockingQueue<String> dataQueue;
    
    String topic = "";
    String content = "";
    
    MemoryPersistence persistence = new MemoryPersistence();
    
    public void run() {
    	String sendData = "";
        
    	try {
        	Gson gson = new Gson();
        	MqttClient client = getMqttClient();
        	int mqttUseCount = 0;
        	
            while(true) {
                if((sendData = dataQueue.take().toString()) != null) {
                	V2xDomain domain = gson.fromJson(sendData, V2xDomain.class);
                	
                	if(mqttUseCount >= mqttClientLimitCount) {
                		closeClient(client);
                		
                		mqttUseCount = 0;
                		client = getMqttClient();
                	}
                	
                	try {
	                	if("session.subscribed".equals(domain.getHook())) {
	                		client.unsubscribe(domain.getTopic());
	                		client.subscribe(domain.getTopic(), qos);
	                	} else if("message.publish".equals(domain.getHook())) {
	                		client.subscribe(domain.getTopic(), qos);
	                		
	                		MqttMessage message = new MqttMessage(gson.toJson(domain.getPayload()).getBytes());
	                		
	                		message.setQos(qos);
	                        client.publish(domain.getTopic(), message);
	                	} else if("session.unsubscribed".equals(domain.getHook())) {
	                		client.subscribe(domain.getTopic(), qos);
	                		client.unsubscribe(domain.getTopic());
	                	}
                	} catch(Exception e) {
                        log.error("error data : " + sendData);
                	}
                	
                	if(!"client.connected".equals(domain.getHook()) && !"client.disconnected".equals(domain.getHook())) {
                		log.info("data push : {}", domain);
                		mqttUseCount++;
                	}
                }
            }
        } catch (Exception e) {
            log.error("error data : " + sendData);
        }
    }
    
    public MqttClient getMqttClient() {
    	MqttClient client = null;
    	
    	try {
    		client = new MqttClient(brokerUrl, UUID.randomUUID().toString(), persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            
            connOpts.setCleanSession(true);
            
            if(sslEnable.equals("Y")) {
            	connOpts.setUserName(username);
            	connOpts.setPassword(password.toCharArray());
            	connOpts.setConnectionTimeout(60);
            	connOpts.setKeepAliveInterval(60);
            	connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            	
            	SSLSocketFactory socketFactory = getSocketFactory(caFilePath,
            			crtFilePath, keyFilePath, "");
            	connOpts.setSocketFactory(socketFactory);
            }
    		
            client.connect(connOpts);
    	} catch(Exception e) {
    		log.error("mqtt client create fail!!", e);
    	}
    	
    	return client;
    }
    
    public void closeClient(MqttClient client) {
    	try {client.disconnect();}catch(Exception e){e.printStackTrace();}
    	try {client.close();}catch(Exception e){e.printStackTrace();}
    	
    	client = null;
    }
    
    private static SSLSocketFactory getSocketFactory(final String caCrtFile,
			final String crtFile, final String keyFile, final String password)
			throws Exception {
		Security.addProvider(new BouncyCastleProvider());

		// load CA certificate
		X509Certificate caCert = null;

		FileInputStream fis = new FileInputStream(caCrtFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		while (bis.available() > 0) {
			caCert = (X509Certificate) cf.generateCertificate(bis);
			// System.out.println(caCert.toString());
		}

		// load client certificate
		bis = new BufferedInputStream(new FileInputStream(crtFile));
		X509Certificate cert = null;
		while (bis.available() > 0) {
			cert = (X509Certificate) cf.generateCertificate(bis);
			// System.out.println(caCert.toString());
		}

		// load client private key
		PEMParser pemParser = new PEMParser(new FileReader(keyFile));
		Object object = pemParser.readObject();
		PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder()
				.build(password.toCharArray());
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter()
				.setProvider("BC");
		KeyPair key;
		if (object instanceof PEMEncryptedKeyPair) {
			System.out.println("Encrypted key - we will use provided password");
			key = converter.getKeyPair(((PEMEncryptedKeyPair) object)
					.decryptKeyPair(decProv));
		} else {
			System.out.println("Unencrypted key - no password needed");
			key = converter.getKeyPair((PEMKeyPair) object);
		}
		pemParser.close();

		// CA certificate is used to authenticate server
		KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
		caKs.load(null, null);
		caKs.setCertificateEntry("ca-certificate", caCert);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
		tmf.init(caKs);

		// client key and certificates are sent to server so it can authenticate
		// us
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, null);
		ks.setCertificateEntry("certificate", cert);
		ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
				new java.security.cert.Certificate[] { cert });
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
				.getDefaultAlgorithm());
		kmf.init(ks, password.toCharArray());

		// finally, create SSL socket factory
		SSLContext context = SSLContext.getInstance("TLSv1.2");
		context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		return context.getSocketFactory();
	}
}
