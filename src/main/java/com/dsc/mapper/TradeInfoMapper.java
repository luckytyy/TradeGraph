package com.dsc.mapper;

import com.dsc.model.TradeInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TradeInfoMapper {


    TradeInfo findById(Integer id);

    List<TradeInfo> findList();

//    List<TradeInfo> findListByCondition(TradeInfo TradeInfo);

    int insert(TradeInfo TradeInfo);


}
