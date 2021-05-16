package com.dsc.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.model.BalanceInfo;
import com.dsc.model.TradeInfo;
import javafx.util.Pair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.LoggerFactory;

import java.util.*;


public class DownloadUtil {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DownloadUtil.class);


    public static final String TOKEN_GET_BALANCE_URL = "https://eth.tokenview.com/api/eth/address/tokenbalance/";
    public static final String TOKEN_GET_DETAIL_URL = "https://eth.tokenview.com/api/eth/address/tokentrans/";


    private static JSONArray getJsonDataFromUrl(String url) {
        logger.info("connnet to {} ", url);
        JSONArray data = null;
        try {
            Connection.Response res = Jsoup.connect(url).userAgent(CommonParametersGetter.getRandomUserAgents())
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .timeout(CommonParametersGetter.CONNECTION_TIME_OUT).ignoreContentType(true).execute();
            String body = res.body();
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


    public static Pair<List<TradeInfo>, BalanceInfo> initData(String firstTokenStr, Set<String> alreadyQueryData) {
        List<TradeInfo> tradeInfoList = new ArrayList();
        BalanceInfo balanceInfo = null;
        for (int i = 0; i < 1; i++) {
            Object totalUrl = null;
            if (totalUrl == null) { // 一条记录都没有
                String headUrl = TOKEN_GET_BALANCE_URL + firstTokenStr;
                try {
                    JSONArray data = getJsonDataFromUrl(headUrl);
                    balanceInfo = data.getObject(0, BalanceInfo.class);
                    boolean findData = false;
                    for (int s = 0 ;s<data.size();s++) {
                        balanceInfo = data.getObject(s,BalanceInfo.class);
                        if(alreadyQueryData.contains(balanceInfo.getHash())){
                            continue;
                        }else{
                            findData = true;
                            break;
                        }
                    }

                    // 目前从余额账号出发，只取第一条  之前未查询过的 记录做更深一层递归；
                    if (balanceInfo == null || !findData) {
                        logger.info("network error or repeated ,{} can not find balance detail ", firstTokenStr);
                        break;
                    } else {
                        System.out.println("balanceInfo = " + balanceInfo);
                        String getDataUrl = TOKEN_GET_DETAIL_URL + firstTokenStr + "/" + balanceInfo.getHash() + "/1/50";
                        JSONArray detailTradeInfo = getJsonDataFromUrl(getDataUrl);
                        if (detailTradeInfo != null && detailTradeInfo.size() > 0) {
                            logger.info(firstTokenStr + " has cnt count =  {} .", detailTradeInfo.size());
                            for (int r = 0; r < detailTradeInfo.size() && r < CommonParametersGetter.MAX_TRADE_COUNT_EACH_ADDR; r++) {
                                TradeInfo object = detailTradeInfo.getObject(r, TradeInfo.class);
                                tradeInfoList.add(object);
                                logger.info("for {},  trade = {} ", i, object);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        balanceInfo.setSourceToken(firstTokenStr);
        Pair<List<TradeInfo>, BalanceInfo> dataFromNetwork = new Pair<>(tradeInfoList, balanceInfo);
        return dataFromNetwork;
    }

    public static Map<String, Object> getDownloadData() {
        Pair<List<TradeInfo>, BalanceInfo> listBalanceInfoPair = initData("0x219a5733d918c955f6237512e0878ea07a372ea8",new HashSet());
        return CalculateUtil.calculateData(listBalanceInfoPair.getKey());
    }

    public static void main(String[] args) {
        getDownloadData();
    }

}
