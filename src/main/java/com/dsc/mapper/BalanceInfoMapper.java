package com.dsc.mapper;

import com.dsc.model.BalanceInfo;
import com.dsc.model.TradeInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BalanceInfoMapper {


    BalanceInfo findById(Integer id);

    List<BalanceInfo> findList();

    int insert(BalanceInfo TradeInfo);

    int updateById(BalanceInfo balanceInfo);

    void deleteAll();

    /**
     * 清除3个表的自增seq
     */
    void deleteSeq();

}
