package com.dsc.mapper;

import com.dsc.model.TradeAgentSummary;
import com.dsc.model.TradeInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TradeInfoMapper {


    TradeInfo findById(Integer id);

    List<TradeInfo> findList();

//    List<TradeInfo> findListByCondition(TradeInfo TradeInfo);

    int insert(TradeInfo TradeInfo);

    void deleteAll();

    /**
     * 查询 一天内交易量达到 最大值 threshholdVale 的 token列表
     *
     * @param threshholdVale
     * @return
     */
    List<TradeAgentSummary> getAgentList(int threshholdVale);
}
