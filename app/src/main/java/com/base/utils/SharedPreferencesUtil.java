package com.base.utils;

import java.util.Map;
import java.util.Set;

import com.base.host.HostApplication;
import com.heihei.model.msg.due.DueMessageUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences工具类
 */
public class SharedPreferencesUtil {

	private SharedPreferences sp;
	private Editor editor;
	private static SharedPreferencesUtil mSharedPreferences;

	public static SharedPreferencesUtil getInstance() {
		if (mSharedPreferences == null) {
			synchronized (SharedPreferencesUtil.class) {
				if (mSharedPreferences == null) {
					mSharedPreferences = new SharedPreferencesUtil(HostApplication.getInstance());
				}
			}
		}
		return mSharedPreferences;
	}

	public final static String name = "vmlives_heihei";
	@SuppressWarnings("deprecation")
	public static int mode = Context.MODE_MULTI_PROCESS;// 进程间数据共享(代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容)

	@SuppressLint("CommitPrefEdits")
	public SharedPreferencesUtil(Context context) {
		this.sp = context.getSharedPreferences(name, mode);
		this.editor = sp.edit();
	}

	/**
	 * 创建一个工具类，默认打开名字为name的SharedPreferences实例
	 * 
	 * @param context
	 * @param name
	 *            唯一标识
	 * @param mode
	 *            权限标识
	 */
	@SuppressLint("CommitPrefEdits")
	public SharedPreferencesUtil(Context context, String name, int mode) {
		this.sp = context.getSharedPreferences(name, mode);
		this.editor = sp.edit();
	}

	/**
	 * 添加信息到SharedPreferences
	 * 
	 * @param name
	 * @param map
	 * @throws Exception
	 */
	public void add(Map<String, String> map) {
		Set<String> set = map.keySet();
		for (String key : set) {
			editor.putString(key, map.get(key));
		}
		editor.commit();
	}

	public void setValueStr(String key, String val) {
		editor.putString(key, val).commit();
	}

	public void setValueInt(String key, Integer val) {
		editor.putInt(key, val).commit();
	}

	public void setValueLong(String key, Long val) {
		editor.putLong(key, val).commit();
	}

	public void setValueBoolean(String key, Boolean val) {
		editor.putBoolean(key, val).commit();
	}

	/**
	 * 删除信息
	 * 
	 * @throws Exception
	 */
	public void deleteAll() throws Exception {
		editor.clear();
		editor.commit();
	}

	/**
	 * 删除一条信息
	 */
	public void delete(String key) {
		editor.remove(key);
		editor.commit();
	}

	/**
	 * String 获取信息
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String get(String key, String delVal) {
		if (sp != null) {
			return sp.getString(key, delVal);
		}
		return delVal;
	}

	/**
	 * Integer 获取信息
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public Integer get(String key, Integer delVal) {
		if (sp != null) {
			return sp.getInt(key, delVal);
		}
		return delVal;
	}

	/**
	 * Long 获取信息
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public Long get(String key, Long delVal) {
		if (sp != null) {
			return sp.getLong(key, delVal);
		}
		return delVal;
	}

	public Boolean get(String key, Boolean delVal) {
		if (sp != null) {
			return sp.getBoolean(key, delVal);
		}
		return delVal;
	}

	/**
	 * 获取此SharedPreferences的Editor实例
	 * 
	 * @return
	 */
	public Editor getEditor() {
		return editor;
	}
}