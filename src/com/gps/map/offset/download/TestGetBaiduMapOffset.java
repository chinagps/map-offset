package com.gps.map.offset.download;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.Base64;

public class TestGetBaiduMapOffset {

	private static Logger logger = Logger.getLogger(TestGetBaiduMapOffset.class);
	
	public static void main(String[] args) {
		Point2D point = new Point2D.Double(111.000000,33.000000);
		point = new Point2D.Double(113.90,22.56);
		getBaiduMapOffset(point);
	}

	public static Point2D getBaiduMapOffset(Point2D point) {
		HttpClient httpClient = new HttpClient();

		/**
		 *  from=0 代表传入真实经纬度
			to=4 代表转换成百度纠偏后的经纬度
			输出json格式：
			{"error":0,"x":"MTEzLjU1MTgwNzMy","y":"MjMuNTIxMjMzOTEwNjQ2"}
		 */

		StringBuffer sb = new StringBuffer();
		Point2D p = new Point2D.Float();
		
		String url ="http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x="+point.getX()+"&y="+point.getY();
		System.out.println(url);
		PostMethod method = new PostMethod(url);
		InputStream strm = null;
		String response = "";
		try {

			int status = httpClient.executeMethod(method);

			if (status == 200) {
				SAXBuilder sab = new SAXBuilder();
				Document doc = null;
				try {
					strm = method.getResponseBodyAsStream();  
			        BufferedReader br = new BufferedReader(new InputStreamReader(strm));  
			        StringBuffer resBuffer = new StringBuffer();  
			        String resTemp = "";  
			        while((resTemp = br.readLine()) != null){  
			            resBuffer.append(resTemp);  
			        }  
			        response = resBuffer.toString();
			        System.out.println(response);
				} catch (Exception ex) {
					logger.error(method.getResponseBodyAsString());
					logger.error(sb.toString());
				}

				//解析返回的结果
				JSONObject json = JSON.parseObject(response);
				String err = json.getString("error");

				if("0".equals(err)){
					String x = json.getString("x");
					String y = json.getString("y");
					String.format("", "123", "");
					System.out.println(String.format("%s:%s", point.getX(), new String(Base64.decodeFast(x))));
					System.out.println(String.format("%s:%s", point.getY(), new String(Base64.decodeFast(y))));
					p.setLocation(Double.parseDouble(new String(Base64.decodeFast(x))), Double.parseDouble(new String(Base64.decodeFast(y))));
				}else{
					p.setLocation(0.0, 0.0);
				}
				System.out.println(p);
				return p;
			}

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (strm != null) {
				try {
					strm.close();
				} catch (IOException e) {
				}
			}
			method.releaseConnection();
		}
		
		return null;
	}
}
