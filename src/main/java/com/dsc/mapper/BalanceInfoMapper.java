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


}
