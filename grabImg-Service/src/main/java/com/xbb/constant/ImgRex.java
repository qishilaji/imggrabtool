package com.xbb.constant;

/**
 * @Auther: xbb
 * @Date: 2018-9-10 10:30
 * @Description:图片抓取类正则表达式
 */
public  class ImgRex {



    /*********************************文件流参数*************************************************/
    //图片路径
    public static  final String  IMG_PATH="d:/temp/";
    /*********************************正则表达书*************************************************/
    //图片元素
    public static  final String  IMG_INFO="<img[^>]*src[=\\\"\\'\\s]+[^\\.]*\\/([^\\.]+)\\.[^\\\"\\']+[\\\"\\']?[^>]*>";
    //图片下载地址正则表达式
    public static  final String  IMG_SRC="[a-zA-z]+://[^\\s]*.jpg";
    //链接地址正则
    public static  final String  IMG_URL="http://[^\\s]*\\.html";
    //图片名称正则
    public static  final String  IMG_NAME="title*[=\\\"]+[\\u4e00-\\u9fa5]+[\\\"]?";
    //zhongwen
    public static  final String  CHINESE="[\\u4e00-\\u9fa5]";


}
