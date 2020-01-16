package com.skt.v2x.simulator.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class V2xDomain {
	@Getter @Setter private String broker;
	@Getter @Setter private String hook;
	@Getter @Setter private long timestamp;
	@Getter @Setter private String clientID;
	@Getter @Setter private String topic;
	@Getter @Setter private PayloadDomain payload;
}
