package com.xbb;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

/**
 * @Auther: xbb
 * @Date: 2018-9-13 16:09
 * @Description:
 */
public class AppBeanFactory implements ApplicationContextAware {
   public static  ApplicationContext applicationContext;
   public static Object getBean(String name){
       return applicationContext.getBean(name);
   }
   public static <T> T getBean(String name, @Nullable Class<T> requiredType) throws BeansException{
       return applicationContext.getBean(name,requiredType);
   }
   public static <T> T getBean(Class<T> requiredType) throws BeansException{
       return applicationContext.getBean(requiredType);
   }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
