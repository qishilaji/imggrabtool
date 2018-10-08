package com.xbb.service.img;

import com.xbb.AppBeanFactory;
import com.xbb.entity.ImgParam;
import com.xbb.service.BaseService;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: xbb
 * @Date: 2018-9-10 16:57
 * @Description:
 */
public class ImgThread extends BaseService implements Runnable {
    //初始url地址
    private ImgParam param ;

    @Override
    public void run() {
        Integer key =(Integer) super.getValue(param.getSirNo());
        if(key>0) {
            ImgService service = AppBeanFactory.getBean(ImgService.class);
            List<String> urls = getUrls(param.getUrl());
            Integer count = 0;
            if (urls != null && urls.size() > 0)
                for (String url : urls) {
                    //开始图片下载方法
                    Map<String, List<String>> mapUrls = service.getUrls(url);
                    //链接地址保存链接表
                    service.saveHerfUrl(mapUrls.get("herf"), url);
                    //下载图片,保存到图片表
                    service.grabAndSaveImg(mapUrls.get("img"), param);
                    //更新状态
                    updateUrl(url);
                    count = service.getImgCount(param.getUrl());
                    if (count > param.getMaxNum()) {
                        break;
                    }
                }

            if (count < param.getMaxNum()) {
                try {
                    Thread.sleep(1000);
                    System.out.println("休息一秒在下载地址");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.run();
            } else {
                //根据地址保存图片
                String resut = service.saveImgFile(param.getUrl(), param.getMaxNum());
                if ("finished".equals(resut)) {
                    String sql = "select count(1) COU from img_info c where c.file_path is not null  and (c.parent_url in (" +
                            "select r.content from img_url r  start with r.parent='" + param.getUrl() + "' connect by  nocycle prior r.content=r.parent ) " +
                            "or c.url='" + param.getUrl() + "') ";
                    int countnow = service.getCount(sql);
                    if (countnow < param.getMaxNum()) {
                        this.run();
                    }
                }
            }
        }
    }



    /**
     *
     * @Description  保存图片
     * @Author       xieb
     * @Date         9:31 2018-9-20
     *
     **/
    public void updateUrl(String url) {
        JdbcTemplate template = (JdbcTemplate) AppBeanFactory.getBean("jdbcTemplate");
        template.execute("update img_url c set c.state=0 where c.content='"+url+"'");
    }
    /**
     *
     * @Description  获取有效地址
     * @Author       xieb
     * @Date         18:44 2018-9-19
     *
     **/
    public List<String> getUrls(String purl) {
        JdbcTemplate template = (JdbcTemplate) AppBeanFactory.getBean("jdbcTemplate");
        List<Map<String, Object>> lmap=template.queryForList("select r.content from img_url r where   r.state=1  start with r.parent='"+purl+"' connect by nocycle prior r.content=r.parent");
        List<String> urls=new ArrayList<>();
        if(lmap!=null&&lmap.size()>0)
            lmap.forEach(it->{
                urls.add((String) it.get("CONTENT"));
            });
        return urls;
    }

    public ImgParam getParam() {
        return param;
    }

    public void setParam(ImgParam param) {
        this.param = param;
    }
}
