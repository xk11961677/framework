/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.common.location;


import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
public class LocationUtils {

	private static final double EARTH_RADIUS = 6378.137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 通过经纬度获取距离(单位：米)
	 * <p>
	 * 说明（如：高德地图，重庆市政府坐标）<br>
	 * <code>106.550464,29.563761</code><br>
	 * 106.550464 经度<br>
	 * 29.563761 纬度<br>
	 * 注：lng 经度<br>
	 * 注：lat 纬度
	 * @param location1 位置1
	 * @param location2 位置1
	 * @return 距离
	 */
	public static double getDistance(Location location1, Location location2) {
		double lng1 = location1.getLng();
		double lat1 = location1.getLat();
		double lng2 = location2.getLng();
		double lat2 = location2.getLat();

		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000d) / 10000d;
		s = s * 1000;
		return s;
	}
	
	/**
	 * 通过经纬度获取距离(单位：米)
	 * @param Location 位置
	 * @param LocationList 位置数组
	 * @return 距离数组
	 */
	public static List<Double> getDistance(Location Location, List<Location> LocationList) {
		List<Double> list = new ArrayList<>();
		for (Location location : LocationList) {
			list.add(getDistance(Location, location));
		}
		
		return list;
	}
	
	/**
	 * 获得距离当前位置最近的经纬度
	 * <p>
	 * 返回locations数组中最小值的下标
	 * @param Location 位置
	 * @param LocationList 位置数组
	 * @return minIndex
	 */
	public static int getNearestLngAndLat(Location Location, List<Location> LocationList) {
		int minIndex = 0;
		
		List<Double> list = getDistance(Location, LocationList);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) < list.get(minIndex)) {
				minIndex = i;
			}
		}
		
		return minIndex;
	}
	
}
