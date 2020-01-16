package com.skt.v2x.simulator.domain;

public class InterfaceStatDomain {
	private String interfaceId;
	private long cnt;
	
	public InterfaceStatDomain(String interfaceId, long cnt) {
		this.interfaceId = interfaceId;
		this.cnt = cnt;
	}
	
	public InterfaceStatDomain() {
	}

	public String getInterfaceId() {
		return interfaceId;
	}
	
	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}
	
	public long getCnt() {
		return cnt;
	}
	
	public void setCnt(long cnt) {
		this.cnt = cnt;
	}
}