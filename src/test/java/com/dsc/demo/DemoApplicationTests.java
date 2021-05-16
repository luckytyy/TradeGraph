package com.dsc.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.mapper.BalanceInfoMapper;
import com.dsc.mapper.TradeInfoMapper;
import com.dsc.model.BalanceInfo;
import com.dsc.model.TradeInfo;
import com.dsc.web.service.TradeInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {


	@Autowired
	TradeInfoService tradeInfoService;

	@Autowired
	TradeInfoMapper tradeInfoMapper;

	@Autowired
    BalanceInfoMapper balanceInfoMapper;


	@Test
	void contextLoads() {

//        List<TradeInfo> list =
//                tradeInfoMapper.findList();
//
//        System.out.println(list.get(0));



        BalanceInfo balanceInfo = new BalanceInfo();
        balanceInfo.setBalance(new BigDecimal("10637197936570000000000"));
        balanceInfo.setNetwork("abbb");
        balanceInfo.setTokenInfoStr("abasd");
        JSONObject jsonObject = JSONObject.parseObject("{'a':'bb'}");
        balanceInfo.setTokenInfo(jsonObject);
        balanceInfo.setTransferCnt(1000L);
        balanceInfoMapper.insert(balanceInfo);

    }

}
