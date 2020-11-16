package com.tang.shiyan3.db;


import org.litepal.crud.LitePalSupport;

public class AppInfo extends LitePalSupport {

    private int id;
    //应用名
    private String name;
    //包名
    private String packageName;
    //应用图标
    private byte[] icon;
    //应用目录
    private String dir;
    //应用数据大小
    private float size;
    //判断是否是系统应用
    private boolean isSystemApp;
    //应用拥有的权限
    private String permission;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }
}
