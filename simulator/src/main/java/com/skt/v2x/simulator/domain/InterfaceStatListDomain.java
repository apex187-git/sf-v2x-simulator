package com.skt.v2x.simulator.domain;

import java.util.List;

public class InterfaceStatListDomain {
	private List<InterfaceStatDomain> statList;

	public List<InterfaceStatDomain> getStatList() {
		return statList;
	}

	public void setStatList(List<InterfaceStatDomain> statList) {
		this.statList = statList;
	}

	@Override
	public String toString() {
		return "InterfaceStatListEntity [statList=" + statList + "]";
	}
}