package com.dsc.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpsRequest {
  
    /** 
     *  
     * @param url 
     * @return 
     */  
    public static String post(String url) {  
        //增加下面两行代码  
        Protocol myhttps = new Protocol("https", new MySSLSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);  
          
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        method.getParams().setContentCharset("UTF8");
        try {
            client.executeMethod(method);
//            byte[] responseBody = post.getResponseBody();
//            String result = new String(responseBody,"GBK");
            InputStream inputStream = method.getResponseBodyAsStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String str = "";
            while ((str = br.readLine()) != null) {
                stringBuffer.append(str);
            }
            return stringBuffer.toString();
        } catch (HttpException e) {
            e.printStackTrace();  
        } catch (IOException e) {
            e.printStackTrace();  
        } finally {  
            method.releaseConnection();
        }  
        return null;  
    }  
}  