package com.xbb.mq.server;

import com.alibaba.fastjson.JSONObject;
import com.xbb.entity.ImgParam;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * @Auther: xbb
 * @Date: 2018-9-10 09:43
 * @Description:图片信息服务端
 */
@Service
public class ImgListenS {
    @Autowired
    private JmsMessagingTemplate jmt;


    /**
     *
     * @Description  图片执行返回方法
     * @Author       xieb
     * @Date         10:15 2018-9-10
     *
     **/
    public void imgCallBack(JSONObject json){
        jmt.convertAndSend("img-imgCallBack",json);
    }

}
