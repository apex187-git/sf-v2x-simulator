package com.skt.v2x.simulator.util;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import com.skt.v2x.simulator.domain.HealthInfoDomain;
import com.skt.v2x.simulator.domain.InterfaceStatDomain;
import com.skt.v2x.simulator.domain.InterfaceStatListDomain;

import lombok.extern.slf4j.Slf4j;

// https://www.baeldung.com/java-influxdb
// https://github.com/influxdata/influxdb-java#synchronous-writes

// create database health;
// create retention policy health_rp on health duration 60d replication 1 default;

@Slf4j
public class InfluxDbUtil {
	public InfluxDB getInfluxDB(String url, String username, String password) {
		InfluxDB influxDb = null;
		
		try {
			influxDb = InfluxDBFactory.connect(url, username, password);
		} catch(Exception e) {
			log.error("InfluxDBFactory.connect error!!", e);
		}
		
		return influxDb;
	}
	
	public BatchPoints getTsdBatchPoints(String database, String retentionPolicyName) {
		BatchPoints batchPoints = null;
		
		try {
			batchPoints = BatchPoints
				  .database(database)
				  .retentionPolicy(retentionPolicyName)
				  .consistency(ConsistencyLevel.ANY)
				  .build();
		} catch(Exception e) {
			log.error("getTsdBatchPoints error!!", e);
		}
		
		return batchPoints;
	}
	
	public void makeRetentionPolicy(InfluxDB influxDb, String rpName, String database, String duration) {
		influxDb.query(new Query("CREATE RETENTION POLICY " + rpName + " ON " + database + " DURATION " + duration  + " REPLICATION 1 DEFAULT"));
	}
	
	public void insertHealthCheckData(String url, String username, String password, String database, String retentionPolicyName,
			String measurement, String service, HealthInfoDomain healthInfo) {
		InfluxDB influxDb = null;
		List<Point> pointList = new ArrayList<>();
		
		try {
			influxDb = getInfluxDB(url, username, password);
			
			//influxDb.setDatabase(database);
			//influxDb.setRetentionPolicy(retentionPolicyName);
			influxDb.enableBatch(BatchOptions.DEFAULTS);
			//influxDb.enableBatch(1, 10, TimeUnit.MILLISECONDS);
			
			String hostName = InetAddress.getLocalHost().getHostName();
			
			pointList.add(Point.measurement(measurement)
					  .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					  .tag("service", service)
					  .tag("host", hostName)
					  .tag("kind", "health")
					  .addField("state", healthInfo.getHealth())
					  .build());
			
			pointList.add(Point.measurement(measurement)
					  .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					  .tag("service", service)
					  .tag("host", hostName)
					  .tag("kind", "redis")
					  .addField("state", healthInfo.getRedis())
					  .build());
			
			pointList.add(Point.measurement(measurement)
					  .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					  .tag("service", service)
					  .tag("host", hostName)
					  .tag("kind", "rpApi")
					  .addField("state", healthInfo.getRpApi())
					  .build());
			
			pointList.add(Point.measurement(measurement)
					  .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					  .tag("service", service)
					  .tag("host", hostName)
					  .tag("kind", "mapMatch")
					  .addField("state", healthInfo.getMapMatch())
					  .build());
			
			pointList.add(Point.measurement(measurement)
					  .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					  .tag("service", service)
					  .tag("host", hostName)
					  .tag("kind", "roadApi")
					  .addField("state", healthInfo.getRoadApi())
					  .build());
			
			pointList.add(Point.measurement(measurement)
					  .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					  .tag("service", service)
					  .tag("host", hostName)
					  .tag("kind", "mqttPush")
					  .addField("state", healthInfo.getMqttPush())
					  .build());
			
			BatchPoints batchPoints = null;
			
			for (Point point : pointList) {
				batchPoints = getTsdBatchPoints(database, retentionPolicyName);
				
				batchPoints.point(point);
				influxDb.write(batchPoints);
			}
			
			influxDb.flush();
			 
			log.info("influxdb insert finish!!");
		} catch(Exception e) {
			log.error("insertHealthCheckData exception occur!!", e);
		} finally {
			try{if(influxDb != null){influxDb.close();}}catch(Exception e){log.error("insertHealthCheckData exception occur!!", e);}
		}
	}
	
	public void insertStatCheckData(String url, String username, String password, String database, String retentionPolicyName,
			String measurement, String service, InterfaceStatListDomain interfaceStatListDomain) {
		InfluxDB influxDb = null;
		
		try {
			influxDb = getInfluxDB(url, username, password);
			
			influxDb.enableBatch(BatchOptions.DEFAULTS);
			
			String hostName = InetAddress.getLocalHost().getHostName();
			
			BatchPoints batchPoints = null;
			
			for (InterfaceStatDomain domain : interfaceStatListDomain.getStatList()) {
				Point statPoint = Point.measurement(measurement)
						.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
						.tag("service", service)
						.tag("host", hostName)
						.tag("interfaceId", domain.getInterfaceId())
						.addField("cnt", domain.getCnt())
						.build();
				
				batchPoints = getTsdBatchPoints(database, retentionPolicyName);
				
				batchPoints.point(statPoint);
				influxDb.write(batchPoints);
			}
			
			influxDb.flush();
		} catch(Exception e) {
			log.error("insertStatCheckData exception occur!!", e);
		} finally {
			try{if(influxDb != null){influxDb.close();}}catch(Exception e){log.error("insertStatCheckData exception occur!!", e);}
		}
	}
	
	public static void main(String[] args) {
		InfluxDB influxDb = null;
		InfluxDbUtil util = new InfluxDbUtil();
		
		try {
			influxDb = util.getInfluxDB("http://192.168.1.167:8086", "root", "root");
			
			String dbName = "health";
			String rpName = "health_rp";
			
			BatchPoints batchPoints = util.getTsdBatchPoints(dbName, rpName);
			
			Point point = Point.measurement("health_info")
					  .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					  .tag("kind", "service-extension")
					  .addField("chechState", "OK")
					  .build();
			
			batchPoints.point(point);
			influxDb.write(batchPoints);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(influxDb != null) {influxDb.close();}
		}
	}
}