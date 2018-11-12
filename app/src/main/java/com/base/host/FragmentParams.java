package com.base.host;

import java.util.HashMap;

public class FragmentParams {

    private HashMap<String, ViewParam> map = null;

    private static FragmentParams params;

    private FragmentParams() {
        map = new HashMap<String, ViewParam>();
    }

    public static FragmentParams getInstance() {
        if (params == null) {
            params = new FragmentParams();
        }
        return params;
    }

    /**
     * 创建一个key
     * 
     * @param cls1
     * @param cls2
     * @return
     */
    public String createKey(Class<?> cls1, Class<?> cls2) {
        return cls1.getName() + "_" + cls2.getName() + "_" + System.currentTimeMillis();
    }

    public void put(String key, ViewParam params) {
        map.put(key, params);
    }

    /**
     * 获取值
     * 
     * @param key
     * @return
     */
    public ViewParam get(String key) {
        return map.get(key);
    }

    public void remove(String key) {
        map.remove(key);
    }

}
