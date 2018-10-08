package com.xbb.service.img;

import com.xbb.AppBeanFactory;
import com.xbb.constant.ImgRex;
import com.xbb.entity.ImgDO;
import com.xbb.entity.ImgParam;
import com.xbb.service.BaseService;
import com.xbb.util.FileUtil;
import com.xbb.util.HttpClientUtil;
import com.xbb.util.ImgGrabUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: xbb
 * @Date: 2018-9-10 09:34
 * @Description:图片搜索服务
 */

@Service
public class ImgService extends BaseService {


    public String grabImageStart(ImgParam param) {
        super.setKey(param.getSirNo(), param.getSwitchType());
        start(param);
        return "success";
    }

    /**
     * @Description 开始方法
     * @Author xieb
     * @Date 16:31 2018-9-10
     **/
    private boolean start(ImgParam param) {
        //获取链接地址,获取图片地址
        Map<String, List<String>> mapUrls = getUrls(param.getUrl());
        //链接地址保存链接表
        saveHerfUrl(mapUrls.get("herf"), param.getUrl());
        //下载图片,保存到图片表
        grabAndSaveImg(mapUrls.get("img"), param);
        //开线程,去下载后面的图片
        startThead(param);
        return true;
    }

    /**
     *
     * @Description  说明
     * @Author       xieb
     * @Date         14:04 2018-9-20
     *
     **/
    public void startThead(ImgParam param) {
        ImgThread imgThead = new ImgThread();
        imgThead.setParam(param);
        Thread thread = new Thread(imgThead);
        thread.start();
    }

    /**
     * @Description 保存链接地址
     * @Author xieb
     * @Date 16:39 2018-9-10
     **/
    public void saveHerfUrl(List<String> herf, String parent) {
        JdbcTemplate template = (JdbcTemplate) AppBeanFactory.getBean("jdbcTemplate");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time = sdf.format(new Date());
        if (herf != null && herf.size() > 0) {
            Set<String> urls = new HashSet();
            urls.addAll(herf);
            for (String url : urls) {
                List<Map<String, Object>> lmap = template.queryForList("select count(1) cou from  IMG_URL c where c.CONTENT='" + url + "'");
                if (lmap != null && lmap.get(0) != null) {
                    int count = ((BigDecimal) lmap.get(0).get("COU")).intValue();
                    if (count < 1){
                        String sql = " insert into IMG_URL (ID, PARENT, CONTENT, REMARK, STATE, TYPE, CTIME, UPTIME)" +
                                "values (img_url_seq.nextval, '" + parent + "', '" + url + "', '', '1', '1', to_date('" + time + "', 'yyyy-MM-ddHH24:mi:ss'), to_date('" + time + "', 'yyyy-MM-dd HH24:mi:ss'))";
                    template.execute(sql);
//                    System.out.println(sql);
                }
            }
        }
    }
}

    /**
     * @Description 获取页面链接和图片的地址
     * @Author xieb
     * @Date 16:36 2018-9-10
     **/
    public Map<String, List<String>> getUrls(String url) {
        Map<String, List<String>> result = new HashMap<>();
//        String html = HttpClientUtil.post(url, null);
        String html=HttpClientUtil.getContentFromIn(url);
        List<String> urls = ImgGrabUtil.getUrlByHtmlText(html, ImgRex.IMG_URL);
        List<String> img = ImgGrabUtil.getUrlByHtmlText(html, ImgRex.IMG_INFO);
        result.put("herf", urls);
        result.put("img", img);
        return result;
    }

    /**
     * @Description 保存图片信息到数据库
     * @Author xieb
     * @Date 16:41 2018-9-10
     **/
    public void grabAndSaveImg(List<String> img, ImgParam param) {
        if (img != null && img.size() > 0) {
            Set<String> imgs = new HashSet<>();
            imgs.addAll(img);
            for (String m : imgs) {
                //获取图片
                List<String> imgsrc = ImgGrabUtil.getUrlByHtmlText(m, ImgRex.IMG_SRC);
                if (imgsrc != null && imgsrc.size() > 0) {
                   for(String s:imgsrc) {
                       String iname = getImgTitle(m, ImgRex.IMG_NAME);
                       //保存数据到数据库
                       saveImgInfo(s, param, iname);
                   }
                }
            }
        }
    }
    /**
     *
     * @Description  获取图片文件
     * @Author       xieb
     * @Date         9:51 2018-9-25
     *
     **/
    private String getImgTitle(String m, String imgName) {
        //先看title元素
        String  tit="";
        List<String> title = ImgGrabUtil.getUrlByHtmlText(m, ImgRex.IMG_NAME);
        if(title!=null&&title.size()>0){
            tit= title.get(0).replace("title","").replace("=","").replace("\"","");
        }else{//去所有中文
            List<String> checeter = ImgGrabUtil.getUrlByHtmlText(m, ImgRex.CHINESE);
            if(checeter!=null&&checeter.size()>0) {
               for(String c:checeter){
                   tit+=c;
               }
           }
        }
        return tit;
    }


    /**
     *
     * @Description  下载并保存图片本体
     * @Author       xieb
     * @Date         9:50 2018-9-25
     *
     **/
    private final static  String IMG_PATH="D:/grabImg";
    public String saveImgFile(String url,int max) {
        String sql="select count(1) COU from img_info c where c.file_path is null  and (c.parent_url in (" +
                "select r.content from img_url r  start with r.parent='"+url+"' connect by  nocycle prior r.content=r.parent ) " +
                "or c.url='"+url+"') ";
        Integer count=getCount(sql);
        List<ImgDO> list=new ArrayList<>();
        if(count<500){
            list=getImgs(url,0);
            for (ImgDO img:list){
                toDownLoadImg(img);
            }
            return "finished";
        }else{
            list=getImgs(url,500);//下载图片
            for (ImgDO img:list){
                toDownLoadImg(img);
            }
            try {
                Thread.sleep(3000);
                System.out.println("休息3秒在下载图片");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return saveImgFile(url,max);
        }
    }

    /**
     *
     * @Description  下载图片
     * @Author       xieb
     * @Date         9:48 2018-9-26
     *
     **/
    public  void toDownLoadImg(ImgDO img) {
        JdbcTemplate template = (JdbcTemplate) AppBeanFactory.getBean("jdbcTemplate");
        byte[] content = HttpClientUtil.downImg(img.getUrl());
        if(content!=null&&content.length>20000){
            img.setImgSize(content.length+"");
            String filePath=IMG_PATH;
            if(img.getKeyword()!=null&&img.getKeyword().length()>0){
                filePath+="/"+img.getKeyword();
            }
            FileUtil.createDir(new File(filePath));
            String  imgpath=filePath+"/"+img.getName()+"-"+content.length+".jpg";
            img.setFilePath(imgpath);
            File file=new File(imgpath);
            try {
                OutputStream os=new FileOutputStream(file);
                os.write(content);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            template.update(" update img_info c set c.img_size="+content.length+",c.file_path='"+imgpath+"' where c.id='"+img.getId()+"'");
        }else{
            template.execute(" delete from img_info where id='"+img.getId()+"'");
        }
    }

    /**
     *
     * @Description  转换列表
     * @Author       xieb
     * @Date         18:01 2018-9-25
     *
     **/
    public List<ImgDO> getImgs(String url,int num) {
        List<ImgDO>  list=new ArrayList<>();
        JdbcTemplate jdbcTemplate = (JdbcTemplate) AppBeanFactory.getBean("jdbcTemplate");
        String sql="select  * from img_info c where c.file_path is null  and (c.parent_url in (" +
                "select r.content from img_url r  start with r.parent='"+url+"' connect by  nocycle prior r.content=r.parent ) " +
                "or c.url='"+url+"') ";
        if(num>0){
            sql+=" and rownum<"+(num+1);
        }
        List<Map<String, Object>> mapList=jdbcTemplate.queryForList(sql);
        if(mapList!=null&&mapList.size()>0){
            mapList.forEach(map->{
                ImgDO img=new ImgDO();
                img.setId(map.get("ID")+"");
                img.setUrl(map.get("URL")+"");
                img.setExt(map.get("EXT")+"");
                img.setName(map.get("NAME")+"");
                img.setParentUrl(map.get("PARENT_URL")+"");
                img.setKeyword(map.get("KEYWORD")+"");
                img.setRemark(map.get("REMARK")+"");
                list.add(img);
            });
        }
        return list ;
    }


    public void saveImgInfo(String imgsrc,ImgParam param, String name) {
        boolean boo=true;
        String keyword=param.getKeyWord()!=null?param.getKeyWord():"";
        if(keyword.length()>0){
            boo=name.contains(param.getKeyWord());
        }
        String sqlc="select count(1) COU from img_info where url='"+imgsrc+"'";
        int cou=getCount(sqlc);
        if(cou<1&&name.length()>1&&boo) {
            JdbcTemplate jdbcTemplate = (JdbcTemplate) AppBeanFactory.getBean("jdbcTemplate");
            String sql = " insert into img_info (ID,NAME,EXT,URL,PARENT_URL,KEYWORD,REMARK)" +
                    "values (sys_guid(), '" + name + "', 'jpg', '" + imgsrc + "','"+param.getUrl()+"','"+keyword+"','')";
            jdbcTemplate.execute(sql);
        }
    }

    /**
     * @Description 图片下载地址方法
     * @Author xieb
     * @Date 16:53 2018-9-10
     **/
    public boolean grabImageStop(ImgParam imgParam) {
        super.setKey(imgParam.getSirNo(), imgParam.getSwitchType());
        return true;
    }



    /**
     *
     * @Description  图片下载地址数量
     * @Author       xieb
     * @Date         9:57 2018-9-20
     *
     **/
    public Integer getImgCount(String parentUrl) {
        JdbcTemplate template = (JdbcTemplate) AppBeanFactory.getBean("jdbcTemplate");
        List<Map<String, Object>> lmaps=template.queryForList("select count(1) COU from img_info c where c.parent_url in (" +
                "select r.content from img_url r  start with r.parent='"+parentUrl+"' connect by  nocycle prior r.content=r.parent ) " +
                "or c.url='"+parentUrl+"'");
        BigDecimal c= (BigDecimal) lmaps.get(0).get("COU");
        return c.intValue();
    }


    /**
     *
     * @Description  统计数量
     * @Author       xieb
     * @Date         9:57 2018-9-20
     *
     **/
    public Integer getCount(String sql) {
        JdbcTemplate template = (JdbcTemplate) AppBeanFactory.getBean("jdbcTemplate");
        List<Map<String, Object>> lmaps=template.queryForList(sql);
        BigDecimal c= (BigDecimal) lmaps.get(0).get("COU");
        return c.intValue();
    }


}
