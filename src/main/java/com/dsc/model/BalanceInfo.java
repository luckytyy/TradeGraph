package com.dsc.model;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class BalanceInfo {

    private String id;
    private String sourceToken;
    private String network;
    private String hash;
    private JSONObject tokenInfo;
    private String tokenInfoStr;
    private Long transferCnt;
    private BigDecimal balance;
    private Date queryTime;
    private Date lastTradeTime;
    private Long realQueryCount;
    private String queryRecursiveInfo;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceToken() {
        return sourceToken;
    }

    public void setSourceToken(String sourceToken) {
        this.sourceToken = sourceToken;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public JSONObject getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(JSONObject tokenInfo) {
        this.tokenInfo = tokenInfo;
    }


    /**** 这里重写了 tokenInfoStr 的get set方法，从 tokenInfo中转换而来 */
    public String getTokenInfoStr() {
        return tokenInfo == null ? Strings.EMPTY:tokenInfo.toJSONString();
    }

    public void setTokenInfoStr(String tokenInfoStr) {
        this.tokenInfoStr = tokenInfo == null ? Strings.EMPTY:tokenInfo.toJSONString();
    }


    public Long getTransferCnt() {
        return transferCnt;
    }

    public void setTransferCnt(Long transferCnt) {
        this.transferCnt = transferCnt;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getRealQueryCount() {
        return realQueryCount;
    }

    public void setRealQueryCount(Long realQueryCount) {
        this.realQueryCount = realQueryCount;
    }

    public String getQueryRecursiveInfo() {
        return queryRecursiveInfo;
    }

    public void setQueryRecursiveInfo(String queryRecursiveInfo) {
        this.queryRecursiveInfo = queryRecursiveInfo;
    }

    public Date getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(Date queryTime) {
        this.queryTime = queryTime;
    }

    public Date getLastTradeTime() {
        return lastTradeTime;
    }

    public void setLastTradeTime(Date lastTradeTime) {
        this.lastTradeTime = lastTradeTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceInfo that = (BalanceInfo) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(sourceToken, that.sourceToken) &&
                Objects.equals(network, that.network) &&
                Objects.equals(hash, that.hash) &&
                Objects.equals(tokenInfo, that.tokenInfo) &&
                Objects.equals(tokenInfoStr, that.tokenInfoStr) &&
                Objects.equals(transferCnt, that.transferCnt) &&
                Objects.equals(balance, that.balance) &&
                Objects.equals(queryTime, that.queryTime) &&
                Objects.equals(lastTradeTime, that.lastTradeTime) &&
                Objects.equals(realQueryCount, that.realQueryCount) &&
                Objects.equals(queryRecursiveInfo, that.queryRecursiveInfo);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, sourceToken, network, hash, tokenInfo, tokenInfoStr, transferCnt, balance, queryTime, lastTradeTime, realQueryCount, queryRecursiveInfo);
    }

    @Override
    public String toString() {
        return "BalanceInfo{" +
                "id='" + id + '\'' +
                ", sourceToken='" + sourceToken + '\'' +
                ", network='" + network + '\'' +
                ", hash='" + hash + '\'' +
                ", tokenInfo=" + tokenInfo +
                ", tokenInfoStr='" + tokenInfoStr + '\'' +
                ", transferCnt=" + transferCnt +
                ", balance=" + balance +
                ", queryTime=" + queryTime +
                ", lastTradeTime=" + lastTradeTime +
                ", realQueryCount=" + realQueryCount +
                ", queryRecursiveInfo='" + queryRecursiveInfo + '\'' +
                '}';
    }
}
