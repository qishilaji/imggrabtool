package com.xbb;


import com.xbb.view.Sample;
import com.xbb.view.StartSplash;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @Description  工具类启动页面
 * @Author       xieb
 * @Date         11:24 2018-9-10
 *
 **/
@SpringBootApplication
@Configuration
@ComponentScan(value = "com.xbb")
public class ToolStart extends AbstractJavaFxApplicationSupport
{
    public static void main( String[] args )
    {
        launch(ToolStart.class, Sample.class, new StartSplash(),args);

    }

}
