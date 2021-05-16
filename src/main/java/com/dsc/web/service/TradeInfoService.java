package com.dsc.web.service;


import com.dsc.mapper.BalanceInfoMapper;
import com.dsc.mapper.TradeInfoMapper;
import com.dsc.model.BalanceInfo;
import com.dsc.model.TradeInfo;
import com.dsc.util.CalculateUtil;
import com.dsc.util.DownloadUtil;
import javafx.util.Pair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dsc.util.CommonParametersGetter.MAX_LOOKUP_DEGREE;
import static com.dsc.util.CommonParametersGetter.MAX_LOOKUP_SIZE;

@Service
public class TradeInfoService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TradeInfoService.class);

    @Autowired
    private TradeInfoMapper tradeInfoMapper;

    @Autowired
    private BalanceInfoMapper balanceInfoMapper;

    public Map<String, Object> showAllDataFromDB() {

        List<TradeInfo> tradeInfoList = tradeInfoMapper.findList();

        return CalculateUtil.calculateData(tradeInfoList);
    }


    public void initData(String firstToken) {

        // 第二个请求中的 from  to地址，也就是第一个请求的入参地址
        Set<String> alreadQueryHashAddrsSet = new HashSet();
        // 第二个请求中的 token 地址
        Set<String> alreadyQueryTokenAddrSet = new HashSet();
        Pair<List<TradeInfo>, BalanceInfo> listBalanceInfoPair = DownloadUtil.initData(firstToken, alreadQueryHashAddrsSet);
        List<TradeInfo> tradeInfoList = listBalanceInfoPair.getKey();
        BalanceInfo balanceInfo = listBalanceInfoPair.getValue();
        Set<String> txIdDataSet = new HashSet();

        initFirtDataSet(txIdDataSet);
        saveToDb(tradeInfoList, txIdDataSet, balanceInfo);


        if (tradeInfoList != null && tradeInfoList.size() > 0) {
            alreadQueryHashAddrsSet.add(firstToken);
            alreadyQueryTokenAddrSet.add(tradeInfoList.get(0).getToken());
        }
        recursiveSaveToDB(tradeInfoList, firstToken, 1, alreadQueryHashAddrsSet, txIdDataSet, alreadyQueryTokenAddrSet);
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


    private void recursiveSaveToDB(List<TradeInfo> tradeInfoList, String lastTokenSource,
                                   int currentDegree, Set<String> alreadQueryHashAddrsSet, Set<String> txIdDataSet, Set<String> alreadyQueryTokenAddrSet) {
        if (currentDegree > MAX_LOOKUP_DEGREE - 1) {
            return;
        } else {
            // 插入第二层
            for (int i = 0; i < tradeInfoList.size() && i < MAX_LOOKUP_SIZE; i++) {
                TradeInfo tmp = tradeInfoList.get(i);
                String fromAlias = tmp.getFromAlias();
                String toAlias = tmp.getToAlias();
                // Huobi Binance 开头的，认为是官方账号，不用这个账号继续地柜查询
//                boolean isGovenance = (!StringUtils.isEmpty(fromAlias) && fromAlias.contains("Huobi")) || (
//                        !StringUtils.isEmpty(toAlias) && toAlias.contains("Huobi"))
//                        || (!StringUtils.isEmpty(fromAlias) && fromAlias.contains("Binance")) || (
//                        !StringUtils.isEmpty(toAlias) && toAlias.contains("Binance"));
                boolean isGovenance = false;
                if (isGovenance) {
                    continue;
                }

                String nextTokenStr = null;
                // 如果是from节点有数据
                if (!alreadQueryHashAddrsSet.contains(tmp.getFrom())) {
                    nextTokenStr = tmp.getFrom();
                } else if (!alreadQueryHashAddrsSet.contains(tmp.getTo())) {
                    nextTokenStr = tmp.getTo();
                }
                logger.info("firstTokenStr = {} ,nextTokenStr = {} ", lastTokenSource, nextTokenStr);

                Pair<List<TradeInfo>, BalanceInfo> listBalanceInfoPair = DownloadUtil.initData(nextTokenStr, alreadyQueryTokenAddrSet);

                List<TradeInfo> tradeInfoSecondList = listBalanceInfoPair.getKey();
                saveToDb(tradeInfoSecondList, txIdDataSet, listBalanceInfoPair.getValue());


                if (tradeInfoSecondList != null && tradeInfoSecondList.size() > 0) {
                    alreadQueryHashAddrsSet.add(lastTokenSource);
                    alreadyQueryTokenAddrSet.add(tradeInfoSecondList.get(0).getToken());

                }
                recursiveSaveToDB(tradeInfoSecondList, nextTokenStr, currentDegree + 1, alreadQueryHashAddrsSet, txIdDataSet, alreadyQueryTokenAddrSet);
            }


        }
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
}
