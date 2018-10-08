package com.xbb.mq.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @Auther: xbb
 * @Date: 2018-9-10 14:29
 * @Description:消息发送端
 */
@Service
public class MqSend {

    @Autowired
    private JmsMessagingTemplate jmt;

    public void MqSend(){
        System.out.println("");
    }

    /**
     *
     * @Description  发送消息
     * @Author       xieb
     * @Date         14:40 2018-9-10
     *
     **/
    public String send(String name,Object mq){
        jmt.convertAndSend(name,mq);
        return "success";
    }



}
