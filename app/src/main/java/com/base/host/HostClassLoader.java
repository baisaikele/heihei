package com.base.host;

import dalvik.system.DexClassLoader;

public class HostClassLoader extends DexClassLoader {

    public String apkPath;

    public HostClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
        this.apkPath = dexPath;
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(className);
        if (clazz != null)
            return clazz;

        try {
            clazz = getParent().loadClass(className);
        } catch (ClassNotFoundException e) {

        }

        if (clazz != null)
            return clazz;

        clazz = findClass(className);
        return clazz;
    }

    // @Override
    // protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
    // try {
    // Class<?> clazz = findClass(className);
    // if (clazz != null) {
    // return clazz;
    // }
    // } catch (ClassNotFoundException e) {
    // Log.e("PluginClassLoader", "UCK QIKU:error", e);
    // }
    // return super.loadClass(className, resolve);
    // }

}
