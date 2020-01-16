package com.skt.v2x.simulator;

public class Test {

	public static void main(String[] args) {
		String str = "curl https://smartfleet.sktelecom.com:29999/api/vse/car/stop -H \"Content-Type:application/json;charset=utf-8\" -d  '{sdfsdfsdfsdfend";
		
		System.out.println(":" + str.substring(str.indexOf("http"), str.indexOf("-H")-1) + ":");
		System.out.println(":" + str.substring(str.indexOf("-d") + 4, str.length()));
	}

}
