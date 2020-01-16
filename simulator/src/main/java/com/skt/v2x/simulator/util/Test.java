package com.skt.v2x.simulator.util;

public class Test {
	public static void main(String[] args) {
		String str = "\"hits\":{\"total\":    123,\"max\"";
		
		//System.out.println(str.length());
		System.out.println(str.indexOf("\"hits\":{\"total\":"));
		//System.out.println(str.indexOf("hook"));
		//System.out.println(str.indexOf("\"", 1+7));
		System.out.println(str.substring(0 + 16, str.indexOf(",", 0 + 16)).trim());
		System.out.println();
	}
}
