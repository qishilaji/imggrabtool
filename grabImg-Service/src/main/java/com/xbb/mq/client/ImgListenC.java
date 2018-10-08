package com.xbb.mq.client;

import com.xbb.entity.ImgParam;
import com.xbb.service.img.ImgService;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * @Auther: xbb
 * @Date: 2018-9-10 09:43
 * @Description:图片信息客户端
 */
@Component
public class ImgListenC {

    @Autowired
    private ImgService imgService;

    /**
     * @Description 图片抓取入口方法
     * @Author xieb
     * @Date 10:15 2018-9-10
     **/
    @JmsListener(destination = "img-grabImg-start")
    public void grabImg(Object object) {
        ActiveMQMapMessage map = (ActiveMQMapMessage) object;
        ImgParam imgParam = new ImgParam(map);
        String result = imgService.grabImageStart(imgParam);
    }

    /**
     * @Description 停止方法
     * @Author xieb
     * @Date 10:15 2018-9-10
     **/
    @JmsListener(destination = "img-grabImg-stop")
    public void grabImageStop(Object object) {
        ActiveMQMapMessage map = (ActiveMQMapMessage) object;
        ImgParam imgParam = new ImgParam(map);
        boolean result = imgService.grabImageStop(imgParam);
    }

}
