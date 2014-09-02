package com.gps.map.offset;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.lang.Math;

/**
 * 无意中发现了谷歌、腾讯、高德地图纠偏算法 
 * http://blog.csdn.net/gatr/article/details/37905779
 * 大家知道，天朝的地图都是加偏的，也就是GPS设备接收到的坐标和电子地图坐标是不一样的，有一定的偏移，如果直接把GPS坐标显示到电子地图上，有几百米的误差，
 * 必须把GPS坐标加上一定的偏移再显示到电子地图上，才和实际相符，理论上，每种地图偏移量不一样的，且都不是线性的，
 * 供应商提供在线的接口，把GPS坐标转换成地图坐标，但算法是保密的，我们可以自己创建纠偏库（参见百度谷歌等地图纠偏库)，
 * 但部分地方不使用数据库，也不适合网上调用，最合适的办法就是通过算法直接计算，我在网上无意看到了用java写的谷歌地图纠偏算法,稍作修改,变成C#的了,
 * 测试了一下,居然腾讯/高德地图纠偏算法也是完全一样,因此,有了该算法,再也不用为纠偏发愁了,
 * 不知道该算法是否有泄密的嫌疑,我也是网上抄的，解决了谷歌、腾讯、高德地图纠偏问题，虽然谷歌地图已经谈出视野了。
 * @author Administrator
 *
 */
public class GDMapOffset {
	private static double pi = 3.14159265358979324;
	private static double a = 6378245.0;
	private static double ee = 0.00669342162296594323;

	//地球坐标转火星坐标
	public static Point2D transform(Point2D p) {
		return transform(p.getX(), p.getY());
	}
	public static Point2D transform(double lng, double lat) {
		if (outOfChina(lat, lng)) {
			return new Point2D.Double(lng, lat);
		}
		double dLat = transformLat(lng - 105.0, lat - 35.0);
		double dLon = transformLon(lng - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * pi;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
		return new Point2D.Double(lng + dLon, lat + dLat);
	}

	private static boolean outOfChina(double lat, double lon) {
		if (lon < 72.004 || lon > 137.8347) {
			return true;
		}
		if (lat < 0.8293 || lat > 55.8271) {
			return true;
		}
		return false;
	}

	private static double transformLat(double x, double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	private static double transformLon(double x, double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
		return ret;
	}
	
	private static final double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

	/**
	 * 火星坐标到百度坐标
	 * 将 GCJ-02 坐标转换成 BD-09 坐标
	 * @param gg_lat
	 * @param gg_lon
	 * @param bd_lat
	 * @param bd_lon
	 * @return
	 */
	private static Point2D bd_encrypt(double gg_lat, double gg_lon, double bd_lat, double bd_lon) {
		double x = gg_lon, y = gg_lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		bd_lon = z * Math.cos(theta) + 0.0065;
		bd_lat = z * Math.sin(theta) + 0.006;
		
		Point2D p = new Point2D.Double();
		p.setLocation(bd_lon, bd_lat);
		return p;
	}
	private static Point2D bd_encrypt(double gg_lat, double gg_lon) {
		Point2D p = new Point2D.Double();
		double x = gg_lon, y = gg_lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		
		double bd_lon = z * Math.cos(theta) + 0.0065;
		double bd_lat = z * Math.sin(theta) + 0.006;
		
		p.setLocation(bd_lon, bd_lat);
		return p;
	}	
	private static Point2D bd_encrypt(Point2D point) {
		double x = point.getX()/1000000, y = point.getY()/1000000;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		
		Point2D p = new Point2D.Double();
		p.setLocation(z * Math.cos(theta) + 0.0065, z * Math.sin(theta) + 0.006);
		return p;
	}

	/**
	 * 百度坐标到火星坐标
	 * 将 BD-09 坐标转换成 GCJ-02坐标 
	 * @param bd_lat
	 * @param bd_lon
	 * @param gg_lat
	 * @param gg_lon
	 * @return
	 */
	private static Point2D bd_decrypt(double bd_lat, double bd_lon, double gg_lat, double gg_lon) {
		double x = bd_lon - 0.0065, y = bd_lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		gg_lon = z * Math.cos(theta);
		gg_lat = z * Math.sin(theta);
		
		Point2D p = new Point2D.Double();
		p.setLocation(gg_lon, gg_lat);
		return p;
	}
	/**
	 * 百度坐标到火星坐标
	 * 将 BD-09 坐标转换成 GCJ-02坐标 
	 * @param bd_lat
	 * @param bd_lon
	 * @return
	 */
	private static Point2D bd_decrypt(double bd_lat, double bd_lon) {
		double x = bd_lon - 0.0065, y = bd_lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		
		Point2D p = new Point2D.Double();
		p.setLocation(z * Math.cos(theta), z * Math.sin(theta));
		return p;
	}
	/**
	 * 百度坐标到火星坐标
	 * 将 BD-09 坐标转换成 GCJ-02坐标 
	 * @param point
	 * @return
	 */
	private static Point2D bd_decrypt(Point2D point) {
		double x = point.getX() - 0.0065, y = point.getY() - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		
		Point2D p = new Point2D.Double();
		p.setLocation(z * Math.cos(theta), z * Math.sin(theta));
		return p;
	}
	
	//火星坐标转地球坐标
	public static Point W2E(int lng, int lat) {
		return null;
	}
	
	public static void main(String[] args) {
		Point2D pt = new Point2D.Double(123.000000,33.000000);
		pt = transform(113.900000,22.560000); // Point2D.Float[111.01199, 33.00439]
		System.out.println(pt);
		//pt = transform(120123456, 28123456);
		//Point2D p = bd_encrypt(transform(111000000,33000000));
		//System.out.println(p);
		
		// 百度坐标转地球真实坐标 113.900011, 22.560009
		Point2D pp = bd_decrypt(new Point2D.Double(113.911354, 22.563046));
		System.out.println("百度坐标(111.01199, 33.00439)转地球火星坐标："+pp);
		pt = transform((int)(pp.getX()*1000000),(int)(pp.getY()*1000000));
		Point2D zhenshi = new Point2D.Double(pp.getX()*2-pt.getX(), pp.getY()*2-pt.getY());
		System.out.println("火星坐标转地球坐标："+zhenshi);
		
		Point2D p = bd_encrypt(transform(new Point2D.Double(110.999975, 32.999992)));
		System.out.println("地球坐标(110.99997509390708, 32.99999225492098)到百度坐标："+p);
	}
}
