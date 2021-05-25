package com.dsc.model;

public class TradeAgentSummary {

    private String tokenAddr;
    private String tradeDateStr;
    private Long tradeCount;

    public String getTokenAddr() {
        return tokenAddr;
    }

    public void setTokenAddr(String tokenAddr) {
        this.tokenAddr = tokenAddr;
    }

    public String getTradeDateStr() {
        return tradeDateStr;
    }

    public void setTradeDateStr(String tradeDateStr) {
        this.tradeDateStr = tradeDateStr;
    }

    public Long getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(Long tradeCount) {
        this.tradeCount = tradeCount;
    }

    @Override
    public String toString() {
        return "TradeAgentSummary{" +
                "tokenAddr='" + tokenAddr + '\'' +
                ", tradeDateStr='" + tradeDateStr + '\'' +
                ", tradeCount=" + tradeCount +
                '}';
    }
}
