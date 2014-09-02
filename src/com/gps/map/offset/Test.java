package com.gps.map.offset;

public class Test {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 纠偏前的经度
		double lon = 109.485928;
		// 纠偏前的纬度
		double lat = 37.445713;
		double point[] = MapFix.getInstance().fix(lon, lat);
		// 纠偏后的经度
		double correctLon = point[0];
		// 纠偏后的纬度
		double correctLat = point[1];
		System.out.println("纠偏前经度：" + lon + "，纬度：" + lat);
		System.out.println("纠偏后经度：" + correctLon + "，纬度：" + correctLat);
	}

}
