package com.example.score.common;

public enum TechTypeEnum {
	
	 /* 5: "M5" 15: "M15" 30: "M30" 60: "M60" 101: "D" 102: "W" 103: "M" D: 101 M:
	 * 103 M5: 5 M15: 15 M30: 30 M60: 60 W: 102*/
	MIN5("5"),
	MIN15("15"),
	MIN30("30"),
	MIN60("60"),
	DAY("101"),
	WEEK("102"),
	MONTH("103");
	
	private final String type;
	
	private TechTypeEnum(String type) {
		this.type = type;
	}
@Override
public String toString() {
	return this.type;
}

}
