package com.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class DateUtils {

	/**
	 * 得到昨天的日期
	 * 
	 * @return
	 */
	public static String getYestoryDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String yestoday = sdf.format(calendar.getTime());
		return yestoday;
	}

	public static String getBeforeyesterday() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -2);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String yestoday = sdf.format(calendar.getTime());
		return yestoday;
	}

	/**
	 * 得到今天的日期
	 * 
	 * @return
	 */
	public static String getTodayDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String date = sdf.format(new Date());
		return date;
	}

	/**
	 * 得到日期 yyyy-MM-dd
	 * 
	 * @param timeStamp
	 *            时间戳
	 * @return
	 */
	public static String formatDate(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String date = sdf.format(timeStamp * 1000);
		return date;
	}

	/**
	 * 得到日期 yy-MM-dd
	 * 
	 * @param timeStamp
	 *            时间戳
	 * @return
	 */
	public static String formatDateNoYear(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String date = sdf.format(timeStamp * 1000);
		try {
			return date.substring(2, date.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String formatDateM(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
		String date = sdf.format(timeStamp * 1000);
		return date;
	}

	/**
	 * 得到时间 HH:mm:ss
	 * 
	 * @param timeStamp
	 *            时间戳
	 * @return
	 */
	public static String getTime(long timeStamp) {
		String time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		String date = sdf.format(timeStamp * 1000);
		String[] split = date.split("\\s");
		if (split.length > 1) {
			time = split[1];
		}
		return time;
	}

	public static String convertTime(long time) {
		if (isToday(time)) {
			return getTime(time);
		} else if (isYesterday(time)) {
			return "昨天" + getTime(time);
		} else if (isBeforeyesterday(time)) {
			return "前天" + getTime(time);
		} else {
			if (isToYear(time)) {
				return formatDateM(time) + " " + getTime(time);
			} else {
				return formatDateNoYear(time);
			}
		}
	}

	public static boolean isToYear(long time) {
		String toYear = getTodayDate();
		String convertYear = formatDate(time).substring(0, 4);
		if (toYear.startsWith(convertYear)) {
			return true;
		}
		return false;
	}

	private static boolean isYesterday(long time) {
		String old = formatDate(time);
		String today = getYestoryDate();
		if (old.contains(today)) {
			return true;
		}
		return false;
	}

	public static boolean isBeforeyesterday(long time) {
		String old = formatDate(time);
		String today = getBeforeyesterday();
		if (old.contains(today)) {
			return true;
		}
		return false;
	}

	private static boolean isToday(long time) {
		String old = formatDate(time);
		String today = getTodayDate();
		if (old.contains(today)) {
			return true;
		}
		return false;
	}
}
