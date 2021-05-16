package com.dsc.util;

import com.alibaba.fastjson.JSON;
import com.dsc.model.Graph.Edge;
import com.dsc.model.Graph.Node;
import com.dsc.model.TradeInfo;
import com.dsc.model.TradeTotal;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIDeclaration;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.dsc.util.CommonParametersGetter.COMMON_NAME_PREFIX;

public class CalculateUtil {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CalculateUtil.class);



    public static Map<String, Object> calculateData(List<TradeInfo> tradeInfoList) {
        Map<String, Object> resultMap = new HashMap();
        Set<Node> nodeHashSet = new HashSet<>();
        Set<Edge> edgeSet = new HashSet<>();
        Map<String, TradeTotal> tradeInfoMap = new HashMap();
        Map<String, List<TradeInfo>> totalTradeInfoMap = new HashMap();

        for (TradeInfo tmp : tradeInfoList) {
            if (!nodeHashSet.contains(tmp.getFrom())) {
                Node nodeFrom = new Node();
                nodeFrom.setId(tmp.getFrom());
                nodeFrom.setName(tmp.getFromAlias() == null ? getLastNumChars(tmp.getFrom(), COMMON_NAME_PREFIX) : tmp.getFromAlias());
                nodeHashSet.add(nodeFrom);
            }
            if (!nodeHashSet.contains(tmp.getTo())) {
                Node nodeTo = new Node();
                nodeTo.setId(tmp.getTo());
                nodeTo.setName(tmp.getToAlias() == null ? getLastNumChars(tmp.getTo(), COMMON_NAME_PREFIX) : tmp.getToAlias());
                nodeHashSet.add(nodeTo);
            }

            String key = tmp.getFrom() + "------" + tmp.getTo();

            if (tradeInfoMap.containsKey(key)) {
                TradeTotal lastTradeInfo = tradeInfoMap.get(key);
                Long totalTradeCount = lastTradeInfo.getTotalTradeCount();
                BigDecimal tradeValue = lastTradeInfo.getTradeValue();
                totalTradeCount = totalTradeCount + 1;
                tradeValue =  tradeValue.add(tmp.getValue());
                lastTradeInfo.setTotalTradeCount(totalTradeCount);
                lastTradeInfo.setTradeValue(tradeValue);
            } else {
                TradeTotal newTradeTotal = new TradeTotal();
                newTradeTotal.setFrom(tmp.getFrom());
                newTradeTotal.setTo(tmp.getTo());
                newTradeTotal.setTradeValue(tmp.getValue());
                newTradeTotal.setTotalTradeCount(1L);
                tradeInfoMap.put(key, newTradeTotal);
            }
            List<TradeInfo> tradeInfoEachList = totalTradeInfoMap.get(key);
            if (tradeInfoEachList == null) {
                tradeInfoEachList = new ArrayList();
                tradeInfoEachList.add(tmp);
                totalTradeInfoMap.put(key, tradeInfoEachList);
            } else {
                tradeInfoEachList.add(tmp);
            }

        }

        for (String key : tradeInfoMap.keySet()) {
            TradeTotal tradeTotal = tradeInfoMap.get(key);
            Edge edge = new Edge();
            edge.setId(String.valueOf(UUID.randomUUID()));
            edge.setSource(tradeTotal.getFrom());
            edge.setTarget(tradeTotal.getTo());
            BigDecimal displayTradeVal = tradeTotal.getTradeValue();
            BigDecimal result = displayTradeVal.divide(new BigDecimal(1000000),10,BigDecimal.ROUND_HALF_UP);
            edge.setTradeVal(result.toString());
            List<TradeInfo> tradeInfos = totalTradeInfoMap.get(key);
            int size = 0 ;
            if (tradeInfos != null) {
                 size = tradeInfos.size();
            }
            edge.setDescription("" + size);
            edgeSet.add(edge);
        }
        resultMap.put("nodes", nodeHashSet);
        resultMap.put("edges", edgeSet);
        logger.info(" convert json data :", JSON.toJSONString(resultMap));
        return resultMap;
    }

    private static String getLastNumChars(String targetStr, int num) {
        if (StringUtils.isEmpty(targetStr) || targetStr.length() <= num) {
            return targetStr;
        }
        return targetStr.substring(targetStr.length() - num - 1, targetStr.length());
    }
}
