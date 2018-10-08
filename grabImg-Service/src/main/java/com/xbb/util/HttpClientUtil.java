package com.xbb.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.imageio.stream.ImageOutputStream;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class HttpClientUtil {

    /**
     * 用post方式提交数据
     *
     * @param s_url     请求的url地址
     * @param hm_param  请求参数map
     * @param s_charset 请求的字符集
     * @return 返回请求url的网页脚本，null时为请求失败
     */
    public static String post(String s_url, HashMap<String, Object> hm_param, String s_charset) {
        HttpPost httpPost = null;
        CloseableHttpClient client = null;
        try {
            httpPost = new HttpPost(s_url);
            client = HttpClients.custom().build();

            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>(hm_param.size());
            for (Map.Entry<String, Object> entry : hm_param.entrySet()) {
                NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
                valuePairs.add(nameValuePair);
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(valuePairs, s_charset);
            httpPost.setEntity(formEntity);
            HttpResponse resp = client.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() != 200) {
                return null;
            }

            HttpEntity entity = resp.getEntity();
            String respContent = EntityUtils.toString(entity, s_charset).trim();

            return respContent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (httpPost != null) {
                httpPost.abort();
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 用post方式提交数据
     *
     * @param s_url    请求的url地址
     * @param hm_param 请求参数map
     * @return 返回请求url的网页脚本，null时为请求失败
     */
    public static String post(String s_url, HashMap<String, String> hm_param) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;

        BasicCookieStore cookieStore = new BasicCookieStore();
        // BasicClientCookie cookie1 = new BasicClientCookie("JSESSIONID",
        // cookie_jsessionid);
        // cookie1.setDomain(cookie_domain);
        // cookieStore.addCookie(cookie1);
        //
        // BasicClientCookie cookie2 = new
        // BasicClientCookie("ASP.NET_SessionId", cookie_ASP_NET_SessionId);
        // cookie2.setDomain(cookie_domain);
        // cookieStore.addCookie(cookie2);

        try {
            httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

            // http 出现"411 Length Required” 错误
            // ：请求方式错误的，本来只是获取数据，应该使用GET的方式；但是我使用的是POST的方式。
            RequestBuilder requestBuilder = RequestBuilder.post().setUri(new URI(s_url));
            // RequestBuilder requestBuilder = RequestBuilder.get().setUri(new
            // URI(requestUrl));

            // 传参值
            if (hm_param != null) {
                Iterator<Map.Entry<String, String>> iter = hm_param.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    requestBuilder.addParameter(entry.getKey(), entry.getValue());
                }
            }

            HttpUriRequest request = requestBuilder.build();

            response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }

            String responseString = EntityUtils.toString(entity);
            return responseString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    /**
     * httpPost
     *
     * @param url       路径
     * @param jsonParam 参数
     * @return
     */
    public static JSONObject httpPostJson(String url, JSONObject jsonParam) {
        return httpPostJson(url, jsonParam, false);
    }

    /**
     * post请求
     *
     * @param url            url地址
     * @param jsonParam      参数
     * @param noNeedResponse 不需要返回结果
     * @return
     */
    public static JSONObject httpPostJson(String url, JSONObject jsonParam, boolean noNeedResponse) {
        // post请求返回结果
        DefaultHttpClient httpClient = new DefaultHttpClient();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        try {
            if (null != jsonParam) {
                // 解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /** 请求发送成功，并得到响应 **/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /** 读取服务器返回过来的json字符串数据 **/
                    str = EntityUtils.toString(result.getEntity());
                    if (noNeedResponse) {
                        return null;
                    }
                    /** 把json字符串转换成json对象 **/
                    jsonResult = JSONObject.parseObject(str);
                } catch (Exception e) {
                }
            }
        } catch (IOException e) {
        }
        return jsonResult;
    }

    /**
     * 发送get请求
     *
     * @param url 路径
     * @return
     */
    public static JSONObject httpGetJson(String url) {
        // get请求返回结果
        JSONObject jsonResult = null;
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            // 发送get请求
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            /** 请求发送成功，并得到响应 **/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /** 读取服务器返回过来的json字符串数据 **/
                String strResult = EntityUtils.toString(response.getEntity());
                /** 把json字符串转换成json对象 **/
                jsonResult = JSONObject.parseObject(strResult);
                url = URLDecoder.decode(url, "UTF-8");
            } else {
            }
        } catch (IOException e) {
        }
        return jsonResult;
    }


    /**
     * 模拟浏览器下载图片保存数据
     *
     * @author: xieb
     * @Date: 2017-11-15 14:57
     */
    public static byte[] downImg(String url) {
        if (url.startsWith("http://")) {
            try {
                // 创建一个url对象
                URL uri = new URL(url);
                URLConnection connection = uri.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");// 伪装成一个浏览器
                connection.getContent();
                InputStream in = connection.getInputStream(); // 开始一个流
                ByteArrayOutputStream baos = null;
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = in.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                byte[] b = baos.toByteArray();
                in.close();
                return b;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * @Description 获取网页内容
     * @Author xieb
     * @Date 10:24 2018-9-20
     **/
    public static String getContentFromIn(String url) {
        InputStream in = null;
        BufferedReader br = null;
        try {
            URL uri = new URL(url);
            URLConnection urlConn = uri.openConnection();
            urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            StringBuilder content = new StringBuilder(200);
            if (null == urlConn) {
                return "";
            }
            if (urlConn.getContentEncoding() != null && urlConn.getContentEncoding().length() > 0) {
                String encode = urlConn.getContentEncoding().toLowerCase();
                if (encode != null && encode.indexOf("gzip") >= 0) {
                    in = new GZIPInputStream(urlConn.getInputStream());
                }
            }

            if (null == in) {
                in = urlConn.getInputStream();
            }
            if (null != in) {
                br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line = "";
                while ((line = br.readLine()) != null) {
                    content.append(line);
                }
            }
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
        }
        return null;
    }


}