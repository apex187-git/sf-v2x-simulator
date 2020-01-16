package com.skt.v2x.simulator.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class PayloadDomain {
	@Getter @Setter private String id;
	@Getter @Setter private String roadId;
	@Getter @Setter private double lon;
	@Getter @Setter private double lat;
	@Getter @Setter private int speed;
	@Getter @Setter private int speedLimit;
	@Getter @Setter private int level;
	@Getter @Setter private long ts;
}
