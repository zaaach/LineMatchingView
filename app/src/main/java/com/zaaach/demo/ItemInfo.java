package com.zaaach.demo;

public class ItemInfo {
    public static final int TEXT  = 0;
    public static final int IMAGE = 1;

    public int type;
    public String content;
    public String desc;//答案描述

    public ItemInfo(int type, String content, String desc) {
        this.type = type;
        this.content = content;
        this.desc = desc;
    }

    public ItemInfo(int type, String content) {
        this(type, content, content);
    }
}
