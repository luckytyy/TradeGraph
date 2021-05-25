package com.dsc.web.service;


import com.alibaba.fastjson.JSON;
import com.dsc.constant.TradeTypeEnum;
import com.dsc.mapper.BalanceInfoMapper;
import com.dsc.mapper.QueryInfoMapper;
import com.dsc.mapper.TradeInfoMapper;
import com.dsc.model.BalanceInfo;
import com.dsc.model.QueryInfo;
import com.dsc.model.TradeAgentSummary;
import com.dsc.model.TradeInfo;
import com.dsc.util.CalculateUtil;
import com.dsc.util.DownloadUtil;
import javafx.util.Pair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.StringUtils;

import java.util.*;

import static com.dsc.constant.CommonParametersGetter.AGENT_TRADE_MAX_COUNT;

@Service
public class TradeInfoService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TradeInfoService.class);

    @Autowired
    private TradeInfoMapper tradeInfoMapper;

    @Autowired
    private BalanceInfoMapper balanceInfoMapper;

    @Autowired
    private QueryInfoMapper queryInfoMapper;

    public Map<String, Object> showAllDataFromDB() {
        List<TradeInfo> tradeInfoList = tradeInfoMapper.findList();
        List<BalanceInfo> balanceInfoList = balanceInfoMapper.findList();
        List<TradeAgentSummary> agentList = tradeInfoMapper.getAgentList(AGENT_TRADE_MAX_COUNT);
        return CalculateUtil.calculateData(tradeInfoList, balanceInfoList, agentList);
    }


    public void initData(String firstToken) {
        cleanDB();

        // 第二个请求中的 from  to地址，也就是第一个请求的入参地址
        Set<String> alreadQueryHashAddrsSet = new HashSet();
        Pair<List<TradeInfo>, BalanceInfo> listBalanceInfoPair = DownloadUtil.initData(firstToken);
        List<TradeInfo> tradeInfoList = listBalanceInfoPair.getKey();
        BalanceInfo balanceInfo = listBalanceInfoPair.getValue();
        Set<String> txIdDataSet = new HashSet();

        initFirtDataSet(txIdDataSet);
        saveToDb(tradeInfoList, txIdDataSet, balanceInfo);

        alreadQueryHashAddrsSet.add(firstToken);
        recursiveSaveToDB(balanceInfo, tradeInfoList, firstToken, 1, alreadQueryHashAddrsSet, txIdDataSet);
    }


    /*****  私有处理方法开始  */
    /**
     * 将数据库里的所有 交易信息 全部查询出来，避免重复插入报错
     *
     * @param txIdDataSet
     */
    private void initFirtDataSet(Set<String> txIdDataSet) {
        List<TradeInfo> list = tradeInfoMapper.findList();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(tmp -> {
                txIdDataSet.add(tmp.getTxid());
            });
        }
    }


    private void recursiveSaveToDB(BalanceInfo balanceInfo, List<TradeInfo> tradeInfoList, String lastTokenSource,
                                   int currentDegree, Set<String> alreadQueryHashAddrsSet, Set<String> txIdDataSet) {
        // 对 balanceInfo 的 多条交易记录 tradeInfoList 来分析，取出对于它的 IN 和 OUT 最多的记录来继续查，并且 再如此重复递归再来一次即可；
        String currentTokenHashAddr = balanceInfo.getSourceToken();
        Map<String, Long> inTokenMap = new HashMap();
        Map<String, Long> outTokenMap = new HashMap();

        if (currentDegree > 2) {
            logger.info("查询次数达到限制，退出...");
            return;
        }

        for (int i = 0; i < tradeInfoList.size(); i++) {
            TradeInfo tmp = tradeInfoList.get(i);
            String fromAlias = tmp.getFromAlias();
            String toAlias = tmp.getToAlias();
            // Huobi Binance 开头的，认为是官方账号，不用这个账号继续地柜查询
//            boolean isGovenance = (!StringUtils.isEmpty(fromAlias) && fromAlias.contains("Huobi")) || (
//                    !StringUtils.isEmpty(toAlias) && toAlias.contains("Huobi"))
//                    || (!StringUtils.isEmpty(fromAlias) && fromAlias.contains("Balance")) || (
//                    !StringUtils.isEmpty(toAlias) && toAlias.contains("Balance"));
            boolean isGovenance = !StringUtils.isEmpty(fromAlias) || !StringUtils.isEmpty(toAlias);
            if (isGovenance) {
                continue;
            }

            if (currentTokenHashAddr.equals(tmp.getFrom())) {
                Long aLong = outTokenMap.get(tmp.getTo());
                if (aLong == null) {
                    aLong = 0L;
                }
                aLong += 1;
                outTokenMap.put(tmp.getTo(), aLong);
            } else if (currentTokenHashAddr.equals(tmp.getTo())) {
                Long aLong = inTokenMap.get(tmp.getFrom());
                if (aLong == null) {
                    aLong = 0L;
                }
                aLong += 1;
                inTokenMap.put(tmp.getFrom(), aLong);
            }
        }

        List<String> maxInToken = getMaxToken(inTokenMap);
        List<String> maxOutToken = getMaxToken(outTokenMap);
        Set<String> tokenAllList = new HashSet();
        tokenAllList.addAll(maxInToken);
        tokenAllList.addAll(maxOutToken);

        QueryInfo tmp = new QueryInfo();
        tmp.setSourceToken(currentTokenHashAddr);
        tmp.setType(TradeTypeEnum.IN.getCode());
        tmp.setTokens(JSON.toJSONString(maxInToken));
        tmp.setDescription(JSON.toJSONString(maxInToken) + " --> " + currentTokenHashAddr);
        queryInfoMapper.insert(tmp);

        QueryInfo tmpOut = new QueryInfo();
        tmpOut.setSourceToken(currentTokenHashAddr);
        tmpOut.setType(TradeTypeEnum.OUT.getCode());
        tmpOut.setTokens(JSON.toJSONString(maxOutToken));
        tmpOut.setDescription(currentTokenHashAddr + " --> " + JSON.toJSONString(maxOutToken));
        queryInfoMapper.insert(tmpOut);


        for (String nextTokenStr : tokenAllList) {
            // 如果该条信息曾经被查询过，则直接跳过；
            if (alreadQueryHashAddrsSet.contains(nextTokenStr)) {
                continue;
            }

            Pair<List<TradeInfo>, BalanceInfo> listBalanceInfoPair = DownloadUtil.initData(nextTokenStr);
            BalanceInfo innerBalance = listBalanceInfoPair.getValue();
            List<TradeInfo> tradeInfoSecondList = listBalanceInfoPair.getKey();
            saveToDb(tradeInfoSecondList, txIdDataSet, listBalanceInfoPair.getValue());
            alreadQueryHashAddrsSet.add(nextTokenStr);

            recursiveSaveToDB(innerBalance, tradeInfoSecondList, nextTokenStr, currentDegree + 1, alreadQueryHashAddrsSet, txIdDataSet);
        }

//            String nextTokenStr = null;
//            // 如果是from节点有数据
//            if (!alreadQueryHashAddrsSet.contains(tmp.getFrom())) {
//                nextTokenStr = tmp.getFrom();
//            } else if (!alreadQueryHashAddrsSet.contains(tmp.getTo())) {
//                nextTokenStr = tmp.getTo();
//            }
//            logger.info("firstTokenStr = {} ,nextTokenStr = {} ", lastTokenSource, nextTokenStr);


    }

    /**
     * 求出 交易次数最多的2个账号
     *
     * @param tokenMap
     * @return
     */
    private List<String> getMaxToken(Map<String, Long> tokenMap) {
        List<String> resultList = new ArrayList();
        if (CollectionUtils.isEmpty(tokenMap)) {
            return resultList;
        }
        Map<Long, List<String>> tmpMap = new HashMap();
        Set<Long> timesSet = new HashSet();
        for (String token : tokenMap.keySet()) {
            Long val1 = tokenMap.get(token);
            timesSet.add(val1);
            if (tmpMap.containsKey(val1)) {
                List<String> tokenList = tmpMap.get(val1);
                tokenList.add(token);
            } else {
                List<String> tokenList = new ArrayList();
                tokenList.add(token);
                tmpMap.put(val1, tokenList);
            }
        }

        List<Long> tmpList = new ArrayList();
        tmpList.addAll(timesSet);
        Collections.sort(tmpList);

        for (Long val : tmpList) {
            List<String> tokenList = tmpMap.get(val);
            logger.info("caution !! 交易次数" + val + "次的，token 有 【" + JSON.toJSONString(tokenList) + "】");
        }

        if (!CollectionUtils.isEmpty(tokenMap)) {
            // 先取出交易次数最多的
            Long aLong = tmpList.get(tmpList.size() - 1);
            List<String> tokenList = tmpMap.get(aLong);
            resultList.addAll(tokenList);
            // 如果交易次数最多的2个token都有了，就不继续找第二多的了
            if (tokenList.size() == 1) {
                // 取出交易次数第二多的,必须要 tmpList 至少有2条记录
                if (tmpList.size() > 1) {
                    Long bLong = tmpList.get(tmpList.size() - 2);
                    List<String> tokenListB = tmpMap.get(bLong);
                    resultList.addAll(tokenListB);
                }
            }
        }
        return resultList;
    }

    private void saveToDb(List<TradeInfo> tradeInfoList, Set<String> txIdDataSet, BalanceInfo balanceInfo) {
        if (!CollectionUtils.isEmpty(tradeInfoList)) {
            for (TradeInfo tmp : tradeInfoList) {
                if (!txIdDataSet.contains(tmp.getTxid())) {
                    tradeInfoMapper.insert(tmp);
                    txIdDataSet.add(tmp.getTxid());
                }
            }
        }
        if (balanceInfo != null) {
            balanceInfoMapper.insert(balanceInfo);
        }
    }

    private void cleanDB() {
        balanceInfoMapper.deleteAll();
        balanceInfoMapper.deleteSeq();
        tradeInfoMapper.deleteAll();
        queryInfoMapper.deleteAll();
        logger.info("delete completed...");
    }

    public static void main(String[] args) {
        Long a1 = 1L;
        Long a2 = 3L;
        Long a3 = 2L;
        List<Long> along = new ArrayList();
        along.add(a1);
        along.add(a2);
        along.add(a3);
        Collections.sort(along);
        System.out.println(along);


        int a = 501 / 10;

        System.out.println(a);
    }
}
