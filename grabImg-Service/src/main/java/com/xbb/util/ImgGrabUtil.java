package com.xbb.util;

import com.xbb.constant.ImgRex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: xbb
 * @Date: 2018-9-10 16:44
 * @Description:
 */
public class ImgGrabUtil {

    /**
     *
     * @Description  抽取页面地址
     * @Author       xieb
     * @Date         16:48 2018-9-10
     *
     **/
    public static List<String> getUrlByHtmlText(String html,String rex) {
        return getMatchers(rex,html);
    }



    /**
     *
     * @Description  返回匹配结果
     * @Author       xieb
     * @Date         15:39 2018-9-13
     *
     **/
   public static List<String> getMatchers(String regex, String source){
       List<String> list = new ArrayList<>();
       if(source!=null) {
           Pattern pattern = Pattern.compile(regex);
           Matcher matcher = pattern.matcher(source);
           while (matcher.find()) {
               list.add(matcher.group());
           }
       }
       return list;
   }

}
