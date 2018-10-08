package com.xbb.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: xbb
 * @Date: 2018-9-10 16:16
 * @Description:
 */
public class BaseService {


    //常量控制集合
    public static Map keyMap=new HashMap<>();


    public synchronized boolean setKey(String key, Object value){
        this.keyMap.put(key,value);
        return true;
    }
    public synchronized Object getValue(String key){

        return this.keyMap.get(key);
    }


}
