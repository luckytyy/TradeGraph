package com.dsc.model.Graph;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class Node {
    private  boolean agent;
    private boolean isRoot;
    private String id;
    private String name;
    private BigDecimal balance;
    private Long transferCnt;
    private Long realQueryCount;
    private String queryTimeStr;
    private String balanceDescription;
    private String lastTimeStr;

    public String getLastTimeStr() {
        return lastTimeStr;
    }

    public void setLastTimeStr(String lastTimeStr) {
        this.lastTimeStr = lastTimeStr;
    }

    public boolean isAgent() {
        return agent;
    }

    public void setAgent(boolean agent) {
        this.agent = agent;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getTransferCnt() {
        return transferCnt;
    }

    public void setTransferCnt(Long transferCnt) {
        this.transferCnt = transferCnt;
    }

    public String getQueryTimeStr() {
        return queryTimeStr;
    }

    public void setQueryTimeStr(String queryTimeStr) {
        this.queryTimeStr = queryTimeStr;
    }

    public Long getRealQueryCount() {
        return realQueryCount;
    }

    public void setRealQueryCount(Long realQueryCount) {
        this.realQueryCount = realQueryCount;
    }

    public String getBalanceDescription() {
        return balanceDescription;
    }

    public void setBalanceDescription(String balanceDescription) {
        this.balanceDescription = balanceDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
