package com.dsc.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.constant.CommonParametersGetter;
import com.dsc.model.BalanceInfo;
import com.dsc.model.TradeInfo;
import javafx.util.Pair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import static com.dsc.constant.CommonParametersGetter.MAX_TRASFER_QUERY_COUNT;
import static com.dsc.constant.CommonParametersGetter.USE_FLAG;


public class DownloadUtil {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DownloadUtil.class);


    public static final String TOKEN_GET_BALANCE_URL = "https://eth.tokenview.com/api/eth/address/tokenbalance/";
    public static final String TOKEN_GET_DETAIL_URL = "https://eth.tokenview.com/api/eth/address/tokentrans/";


    private static JSONArray getJsonDataFromUrl(String url) {
        logger.info("connnet to {} ", url);
        JSONArray data = null;
        try {
          String body= null;
            if("2".equals(USE_FLAG)) {
                Connection.Response res = Jsoup.connect(url).userAgent(CommonParametersGetter.getRandomUserAgents())
                        .header("Accept", "*/*")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .timeout(CommonParametersGetter.CONNECTION_TIME_OUT).ignoreContentType(true).execute();
                body = res.body();
            }else{
                Map<String,String> headerMap = new HashMap();
                headerMap.put("Accept","*/*");
                headerMap.put("Accept-Encoding","gzip, deflate");
                headerMap.put("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
                headerMap.put("Content-Type","application/json;charset=UTF-8");
                body = HttpsRequest.post(url);
            }
            logger.info("document = " + body);
            JSONObject jsonObject = JSONObject.parseObject(body);
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");
            data = jsonObject.getJSONArray("data");
            if ("1".equals(code) && data != null && data.size() > 0) {
                return data;
            } else {
                logger.error("get json data from {} error,code = {}, msg = {} .", url, code, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Pair<List<TradeInfo>, BalanceInfo> initData(String firstTokenStr) {
        List<TradeInfo> tradeInfoList = new ArrayList();

        BalanceInfo balanceInfo = null;
        String headUrl = TOKEN_GET_BALANCE_URL + firstTokenStr;
        JSONArray data = getJsonDataFromUrl(headUrl);
        balanceInfo = data.getObject(0, BalanceInfo.class);
        Long transferCnt = balanceInfo.getTransferCnt();
        Long queryTransferCnt = transferCnt > MAX_TRASFER_QUERY_COUNT ? MAX_TRASFER_QUERY_COUNT : transferCnt;

        StringBuilder sb  = new StringBuilder();
        sb.append(headUrl).append("\r\n");

        // 判断 最大查询交易记录需要 分页多少次
        Long batchLoop = queryTransferCnt % 50 != 0 ? queryTransferCnt / 50 + 1 : queryTransferCnt / 50;
        Long realQueryCount = 0L;
        Date lastTradeTime = null;

        for (int i = 0; i < batchLoop; i++) {
            List<TradeInfo> tmpTradeInfoList = new ArrayList();
            Object totalUrl = null;
            if (totalUrl == null) { // 一条记录都没有
                try {
                    // 目前从余额账号出发，只取第一条  之前未查询过的 记录做更深一层递归；
                    logger.info("balanceInfo = " + balanceInfo);
                    String getDataUrl = TOKEN_GET_DETAIL_URL + firstTokenStr + "/" + balanceInfo.getHash() + "/" + (i + 1) + "/50";
                    JSONArray detailTradeInfo = getJsonDataFromUrl(getDataUrl);
                    sb.append(getDataUrl).append("\r\n");

                    if (detailTradeInfo != null && detailTradeInfo.size() > 0) {
                        for (int r = 0; r < detailTradeInfo.size() && r < CommonParametersGetter.MAX_TRADE_COUNT_EACH_ADDR; r++) {
                            TradeInfo object = detailTradeInfo.getObject(r, TradeInfo.class);
                            tmpTradeInfoList.add(object);
                            lastTradeTime = object.getTime();
                            logger.info("for {},  trade = {} ,lastTradeTime ={} ", i+1, object,lastTradeTime);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tradeInfoList.addAll(tmpTradeInfoList);
                realQueryCount += tmpTradeInfoList.size();
            }
        }
        balanceInfo.setSourceToken(firstTokenStr);
        balanceInfo.setRealQueryCount(realQueryCount);
        balanceInfo.setQueryTime(new Date());
        balanceInfo.setQueryRecursiveInfo(sb.toString());
        balanceInfo.setLastTradeTime(lastTradeTime);
        logger.info("tmp query completed for ", balanceInfo);
        Pair<List<TradeInfo>, BalanceInfo> dataFromNetwork = new Pair<>(tradeInfoList, balanceInfo);
        return dataFromNetwork;
    }





//    /**
//     *  http get请求 请求参数在url后面拼接
//     *  @throws Exception
//     */
//    private static String sendRequest(String requestUrl,Map<String,String> header) throws Exception{
//        HttpsURLConnection conn = null;
//        InputStream input = null;
//        BufferedReader br = null;
//        StringBuffer buffer = null;
//        try {
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//            URL url = new URL(requestUrl);
//            conn = (HttpsURLConnection) url.openConnection();
//            conn.setSSLSocketFactory(sc.getSocketFactory());
//            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
//
//            conn.setDoOutput(false);
//            conn.setDoInput(true);
//            conn.setUseCaches(false);
//            conn.setConnectTimeout(1000 * 100);
//            conn.setReadTimeout(1000 * 100);
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "*/*");
//            conn.setRequestProperty("Connection", "keep-alive");
//            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//            conn.setRequestProperty("Charset", "UTF-8");
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//            //添加header
//            if(header != null){
//                for(Map.Entry<String, String> entry : header.entrySet()){
//                    String mapKey = entry.getKey();
//                    String mapValue = entry.getValue();
//                    conn.setRequestProperty(mapKey, mapValue);
//                }
//            }
//
//
//
//            conn.connect();
//
//            // 读取服务器端返回的内容
//            System.out.println("======================响应体=========================");
//            System.out.println("ResponseCode:" + conn.getResponseCode() + ",ResponseMessage:" + conn.getResponseMessage());
//            if(conn.getResponseCode()==200){
//                input = conn.getInputStream();
//            }else{
//                input = conn.getErrorStream();
//            }
//
//            br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
//            buffer = new StringBuffer();
//            String line = null;
//            while ((line = br.readLine()) != null) {
//                buffer.append(line);
//            }
//            System.out.println("返回报文:" + buffer.toString());
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            throw new Exception(e);
//        } finally {
//            try {
//                if (conn != null) {
//                    conn.disconnect();
//                    conn = null;
//                }
//                if (br != null) {
//                    br.close();
//                    br = null;
//                }
//            } catch (IOException ex) {
//                logger.error(ex.getMessage(), ex);
//                throw new Exception(ex);
//            }
//        }
//        return buffer.toString();
//    }

}
