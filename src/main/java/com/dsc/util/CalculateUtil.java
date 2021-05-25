package com.dsc.util;

import com.alibaba.fastjson.JSON;
import com.dsc.model.BalanceInfo;
import com.dsc.model.Graph.Edge;
import com.dsc.model.Graph.Node;
import com.dsc.model.TradeAgentSummary;
import com.dsc.model.TradeInfo;
import com.dsc.model.TradeTotal;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.dsc.constant.CommonParametersGetter.COMMON_NAME_PREFIX;
import static com.dsc.constant.CommonParametersGetter.ENTER_STR;

public class CalculateUtil {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CalculateUtil.class);


    public static Map<String, Object> calculateData(List<TradeInfo> tradeInfoList, List<BalanceInfo> balanceInfoList, List<TradeAgentSummary> agentList) {
        String rootTokenAddr = balanceInfoList.get(0).getSourceToken();

        Map<String, Object> resultMap = new HashMap();
        Set<Node> nodeHashSet = new HashSet<>();
        Set<Edge> edgeSet = new HashSet<>();
        Map<String, TradeTotal> tradeInfoMap = new HashMap();
        Map<String, List<TradeInfo>> totalTradeInfoMap = new HashMap();

        Map<String, List<TradeInfo>> govenanceInMap = new HashMap();
        Map<String, List<TradeInfo>> govenanceOutMap = new HashMap();
        Map<String, BalanceInfo> balanceInfoMap = new HashMap();
        if (!CollectionUtils.isEmpty(balanceInfoList)) {
            for (BalanceInfo tmp : balanceInfoList) {
                balanceInfoMap.put(tmp.getSourceToken(), tmp);
            }
        }

        Set<String> agentTokens = new HashSet();
        if(!CollectionUtils.isEmpty(agentList)){
            for(TradeAgentSummary tmp:agentList){
                agentTokens.add(tmp.getTokenAddr());
            }
        }


        for (TradeInfo tmp : tradeInfoList) {
            boolean isFromGovenance = !StringUtils.isEmpty(tmp.getFromAlias());
            boolean isToGovenance = !StringUtils.isEmpty(tmp.getToAlias());
            // 如果是官方系的，直接跳过，该条交易单独统计到 balance上面
            if (isFromGovenance) {
                List<TradeInfo> tradeInfos = govenanceInMap.get(tmp.getTo());
                if (tradeInfos == null) {
                    tradeInfos = new ArrayList();
                }
                tradeInfos.add(tmp);
                govenanceInMap.put(tmp.getTo(), tradeInfos);
            }

            if (isToGovenance) {
                List<TradeInfo> tradeInfos = govenanceOutMap.get(tmp.getFrom());
                if (tradeInfos == null) {
                    tradeInfos = new ArrayList();
                }
                tradeInfos.add(tmp);
                govenanceOutMap.put(tmp.getFrom(), tradeInfos);
            }

            if (isFromGovenance || isToGovenance) {
                continue;
            }

            // 如果 from 和 to 都在，才将交易记录算进去
            if (!balanceInfoMap.containsKey(tmp.getFrom()) || !balanceInfoMap.containsKey(tmp.getTo())) {
                continue;
            }





            if (!nodeHashSet.contains(tmp.getFrom())) {
                Node nodeFrom = new Node();
                nodeFrom.setId(tmp.getFrom());
                nodeFrom.setName(tmp.getFromAlias() == null ? getLastNumChars(tmp.getFrom(), COMMON_NAME_PREFIX) : tmp.getFromAlias());
                nodeHashSet.add(nodeFrom);

                BalanceInfo balanceInfo = balanceInfoMap.get(tmp.getFrom());
                if (balanceInfo != null) {
                    nodeFrom.setBalance(balanceInfo.getBalance());
                    nodeFrom.setTransferCnt(balanceInfo.getTransferCnt());
                    nodeFrom.setRealQueryCount(balanceInfo.getRealQueryCount());
                    nodeFrom.setQueryTimeStr(formatDate(balanceInfo.getQueryTime()));
                    nodeFrom.setLastTimeStr(formatDate(balanceInfo.getLastTradeTime()));
                }

            }
            if (!nodeHashSet.contains(tmp.getTo())) {
                Node nodeTo = new Node();
                nodeTo.setId(tmp.getTo());
                nodeTo.setName(tmp.getToAlias() == null ? getLastNumChars(tmp.getTo(), COMMON_NAME_PREFIX) : tmp.getToAlias());
                nodeHashSet.add(nodeTo);

                BalanceInfo balanceInfo = balanceInfoMap.get(tmp.getTo());
                if (balanceInfo != null) {
                    nodeTo.setBalance(balanceInfo.getBalance());
                    nodeTo.setTransferCnt(balanceInfo.getTransferCnt());
                    nodeTo.setRealQueryCount(balanceInfo.getRealQueryCount());
                    nodeTo.setQueryTimeStr(formatDate(balanceInfo.getQueryTime()));
                    nodeTo.setLastTimeStr(formatDate(balanceInfo.getLastTradeTime()));
                }
            }


            String key = tmp.getFrom() + "------" + tmp.getTo();

            if (tradeInfoMap.containsKey(key)) {
                TradeTotal lastTradeInfo = tradeInfoMap.get(key);
                Long totalTradeCount = lastTradeInfo.getTotalTradeCount();
                BigDecimal tradeValue = lastTradeInfo.getTradeValue();
                totalTradeCount = totalTradeCount + 1;
                tradeValue = tradeValue.add(tmp.getValue());
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

        for (Node tmp : nodeHashSet) {
            if (tmp.getId().equals(rootTokenAddr)) {
                tmp.setRoot(true);
            }
            if(agentTokens.contains(tmp.getId())){
                tmp.setAgent(true);
            }
        }

        for (String key : tradeInfoMap.keySet()) {
            TradeTotal tradeTotal = tradeInfoMap.get(key);
            Edge edge = new Edge();
            edge.setId(String.valueOf(UUID.randomUUID()));
            edge.setSource(tradeTotal.getFrom());
            edge.setTarget(tradeTotal.getTo());
            BigDecimal displayTradeVal = tradeTotal.getTradeValue();
            edge.setTradeVal(formatDisplayBigDeicmalValues(displayTradeVal));
            List<TradeInfo> tradeInfos = totalTradeInfoMap.get(key);
            int size = 0;
            if (tradeInfos != null) {
                size = tradeInfos.size();
            }
            edge.setTradeCount(size);
            edge.setTradeDescription(getTradeTimePeriod(tradeInfos));
            edgeSet.add(edge);
        }

        fullfillDataNode(nodeHashSet, govenanceInMap, govenanceOutMap, "Huobi");
        fullfillDataNode(nodeHashSet, govenanceInMap, govenanceOutMap, "Binance");
        fullfillDataNode(nodeHashSet, govenanceInMap, govenanceOutMap, "Gate.io");
        fullfillDataNode(nodeHashSet, govenanceInMap, govenanceOutMap, "MXC");
        resultMap.put("nodes", nodeHashSet);
        resultMap.put("edges", edgeSet);
        logger.info(" convert json data :", JSON.toJSONString(resultMap));
        return resultMap;
    }

    private static String getTradeTimePeriod(List<TradeInfo> tradeInfos){
        if(!CollectionUtils.isEmpty(tradeInfos)){
            Collections.sort(tradeInfos, new Comparator<TradeInfo>() {
                @Override
                public int compare(TradeInfo o1, TradeInfo o2) {
                    return o1.getTime().getTime() > o2.getTime().getTime()? 1:-1 ;
                }
            });
        }
        StringBuilder sb = new StringBuilder();
        Date d1 = tradeInfos.get(0).getTime();
        Date d2 = tradeInfos.get(tradeInfos.size()-1).getTime();
        sb.append("交易时间从"+formatDate(d1)+" 到 "+ formatDate(d2));
        return sb.toString();
    }

    /**
     * 原始数字需要除以 100w，才是正常的数字，但是要保留小数位数
     *
     * @param displayTradeVal
     * @return
     */
    private static String formatDisplayBigDeicmalValues(BigDecimal displayTradeVal) {
        return displayTradeVal.divide(new BigDecimal(1000000), 10, BigDecimal.ROUND_HALF_UP).toString();
    }

    private static String getLastNumChars(String targetStr, int num) {
        if (StringUtils.isEmpty(targetStr) || targetStr.length() <= num) {
            return targetStr;
        }
        return targetStr.substring(targetStr.length() - num - 1, targetStr.length());
    }


    /**
     * 对于每个 数据节点，将 跟官方平台的交易数据，做统计汇总
     * Huobi
     * Binance
     * Gate.io
     * MXC
     *
     * @param nodeSet
     * @param govenanceInMap
     * @param govenanceOutMap
     */
    private static void fullfillDataNode(Set<Node> nodeSet, Map<String, List<TradeInfo>> govenanceInMap,
                                         Map<String, List<TradeInfo>> govenanceOutMap, String specialKey) {
        for (Node tmp : nodeSet) {
            String key = tmp.getId();
            StringBuilder sb = new StringBuilder();
            List<TradeInfo> tradeInfos = govenanceInMap.get(key);
            int count = 0;
            BigDecimal values = new BigDecimal(0);
            if (!CollectionUtils.isEmpty(tradeInfos)) {

                for (TradeInfo tradeInfo : tradeInfos) {
                    if (tradeInfo.getFromAlias().indexOf(specialKey) > -1) {
                        count += 1;
                        values = values.add(tradeInfo.getValue());
                    }
                }

                if (count > 0) {
                    sb.append(specialKey + " 向该账号转入" + count + "次，转入金额" + formatDisplayBigDeicmalValues(values)).append(ENTER_STR);

                    if (StringUtils.isEmpty(tmp.getBalanceDescription())) {
                        tmp.setBalanceDescription(sb.toString());
                    } else {
                        String newDesc = tmp.getBalanceDescription() + ENTER_STR + sb.toString();
                        tmp.setBalanceDescription(newDesc);
                    }
                }
            }

            List<TradeInfo> tradeInfosOut = govenanceOutMap.get(key);
            int count1 = 0;
            BigDecimal values1 = new BigDecimal(0);
            if (!CollectionUtils.isEmpty(tradeInfosOut)) {
                for (TradeInfo tradeInfo : tradeInfosOut) {
                    if (tradeInfo.getToAlias().indexOf(specialKey) > -1) {
                        count1 += 1;
                        values1 = values1.add(tradeInfo.getValue());
                    }
                }
                if (count1 > 0) {
                    sb.append(specialKey + " 向该账号转入" + count1 + "次，转入金额" + formatDisplayBigDeicmalValues(values1)).append(ENTER_STR);

                    if (StringUtils.isEmpty(tmp.getBalanceDescription())) {
                        tmp.setBalanceDescription(sb.toString());
                    } else {
                        String newDesc = tmp.getBalanceDescription() + ENTER_STR + sb.toString();
                        tmp.setBalanceDescription(newDesc);
                    }
                }
            }

        }
    }


    private static ThreadLocal<SimpleDateFormat> simpleDateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };


    private static String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = simpleDateFormatThreadLocal.get();
        String result = simpleDateFormat.format(date);
//        simpleDateFormatThreadLocal.remove();
        return result;
    }


}
