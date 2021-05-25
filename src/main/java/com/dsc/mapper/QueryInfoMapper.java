package com.dsc.mapper;

import com.dsc.model.QueryInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QueryInfoMapper {


    QueryInfo findById(Integer id);

    List<QueryInfo> findList();

    int insert(QueryInfo TradeInfo);

    void deleteAll();

}
