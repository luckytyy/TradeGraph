package com.dsc.model;

import java.math.BigDecimal;
import java.util.Objects;

public class TradeTotal {


    private String from;
    private String to;
    private Long totalTradeCount;
    private BigDecimal tradeValue;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Long getTotalTradeCount() {
        return totalTradeCount;
    }

    public void setTotalTradeCount(Long totalTradeCount) {
        this.totalTradeCount = totalTradeCount;
    }


    public BigDecimal getTradeValue() {
        return tradeValue;
    }

    public void setTradeValue(BigDecimal tradeValue) {
        this.tradeValue = tradeValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradeTotal)) return false;
        TradeTotal that = (TradeTotal) o;
        return Objects.equals(getFrom(), that.getFrom()) &&
                Objects.equals(getTo(), that.getTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo());
    }

    @Override
    public String toString() {
        return "TradeTotal{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", totalTradeCount=" + totalTradeCount +
                ", tradeValue=" + tradeValue +
                '}';
    }
}
