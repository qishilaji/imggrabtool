package com.xbb.controller.img;

import com.alibaba.fastjson.JSONObject;
import com.xbb.mq.client.MqSend;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.Random;

/**
 * @Auther: xbb
 * @Date: 2018-9-10 14:45
 * @Description:
 */
@FXMLController
@Controller
public class ImgController {

    @Autowired
    private MqSend mqSend;

    @FXML
    private TextField firstUrl;

    @FXML
    private  TextField maxNum;

    @FXML
    private  TextField keyWord;

    @FXML
    private TextArea hinText;

    @FXML
    private TextField sirNo;


    @FXML
    public void start(){
        hinText.setText("启动中>>>>>>");
        JSONObject json=new JSONObject();
        json.put("url",firstUrl.getText());
        json.put("keyWord",keyWord.getText());
        json.put("maxNum",maxNum.getText());
        String sirNoNew=getNo();
        sirNo.setText(sirNoNew);
        json.put("sirNo",sirNoNew);
        json.put("switchType",1);
        String result=mqSend.send("img-grabImg-start",json);
        if("success".equals(result))
            hinText.setText("启动成功!");
        else
            hinText.setText("启动失败!");
    }


    @FXML
    public void stop (){
        JSONObject json=new JSONObject();
        json.put("sirNo",sirNo.getText());
        json.put("switchType",0);
        String result=mqSend.send("img-grabImg-stop",json);
        hinText.setText("已停止:");
    }


    /**
     *
     * @Description  逻辑以后再定
     * @Author       xieb
     * @Date         16:22 2018-9-19
     *
     **/
    public String getNo() {
        return new Date().getTime()+""+ (int)(Math.random() * 1000000);
    }
}
