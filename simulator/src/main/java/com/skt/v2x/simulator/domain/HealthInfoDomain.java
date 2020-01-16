package com.skt.v2x.simulator.domain;

public class HealthInfoDomain {
	private int health;
	private int redis;
	private int rpApi;
	private int mapMatch;
	private int roadApi;
	private int mqttPush;
	
	public int getHealth() {
		return health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public int getRedis() {
		return redis;
	}
	
	public void setRedis(int redis) {
		this.redis = redis;
	}
	
	public int getRpApi() {
		return rpApi;
	}
	
	public void setRpApi(int rpApi) {
		this.rpApi = rpApi;
	}
	
	public int getMapMatch() {
		return mapMatch;
	}
	
	public void setMapMatch(int mapMatch) {
		this.mapMatch = mapMatch;
	}
	
	public int getRoadApi() {
		return roadApi;
	}
	
	public void setRoadApi(int roadApi) {
		this.roadApi = roadApi;
	}
	
	public int getMqttPush() {
		return mqttPush;
	}
	
	public void setMqttPush(int mqttPush) {
		this.mqttPush = mqttPush;
	}
}
