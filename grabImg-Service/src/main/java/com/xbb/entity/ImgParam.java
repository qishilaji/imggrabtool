package com.xbb.entity;

import org.apache.activemq.command.ActiveMQMapMessage; /**
 * @Auther: xbb
 * @Date: 2018-9-10 10:16
 * @Description:
 */
public class ImgParam {

    //初始地址
    private String url;
    //关键字
    private String keyWord;
    //最大搜索量
    private Integer maxNum;
    //唯一标识
    private String key;
    //唯一标识
    private Integer switchType;
    //客户端标识
    private String sirNo;

    public ImgParam() {
    }


    public ImgParam(ActiveMQMapMessage map) {
        try {
            this.url = map.getString("url");
            this.keyWord = map.getString("keyWord");
            this.maxNum = map.getInt("maxNum");
            this.key = map.getString("key");
            this.switchType = map.getInt("switchType");
            this.sirNo = map.getString("sirNo");
        }catch (Exception e){

        }
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getSwitchType() {
        return switchType;
    }

    public void setSwitchType(Integer switchType) {
        this.switchType = switchType;
    }

    public String getSirNo() {
        return sirNo;
    }

    public void setSirNo(String sirNo) {
        this.sirNo = sirNo;
    }
}
