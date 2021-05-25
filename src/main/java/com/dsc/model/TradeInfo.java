package com.dsc.model;


import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class TradeInfo {

    private Long id;
    private Long index;
    private Long block_no;
    private String token ;
    private String tokenAddr;
    private String tokenSymbol;
    private Long tokenDecimals;
    private Date time;
    private String txid;
    private JSONObject tokenInfo;
    private String tokenInfoStr;

    private String from;
    private String fromAlias;
    private String to;
    private String toAlias;
    private BigDecimal value;
    private String conformations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Long getBlock_no() {
        return block_no;
    }

    public void setBlock_no(Long block_no) {
        this.block_no = block_no;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenAddr() {
        return tokenAddr;
    }

    public void setTokenAddr(String tokenAddr) {
        this.tokenAddr = tokenAddr;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public Long getTokenDecimals() {
        return tokenDecimals;
    }

    public void setTokenDecimals(Long tokenDecimals) {
        this.tokenDecimals = tokenDecimals;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
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

    public String getToAlias() {
        return toAlias;
    }

    public void setToAlias(String toAlias) {
        this.toAlias = toAlias;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getConformations() {
        return conformations;
    }

    public void setConformations(String conformations) {
        this.conformations = conformations;
    }

    public String getFromAlias() {
        return fromAlias;
    }

    public void setFromAlias(String fromAlias) {
        this.fromAlias = fromAlias;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradeInfo)) return false;
        TradeInfo tradeInfo = (TradeInfo) o;
        return Objects.equals(getTxid(), tradeInfo.getTxid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTxid());
    }

    @Override
    public String toString() {
        return "TradeInfo{" +
                "id=" + id +
                ", index=" + index +
                ", block_no=" + block_no +
                ", token='" + token + '\'' +
                ", tokenAddr='" + tokenAddr + '\'' +
                ", tokenSymbol='" + tokenSymbol + '\'' +
                ", tokenDecimals=" + tokenDecimals +
                ", time=" + time +
                ", txid='" + txid + '\'' +
                ", tokenInfo=" + tokenInfo +
                ", tokenInfoStr='" + tokenInfoStr + '\'' +
                ", from='" + from + '\'' +
                ", fromAlias='" + fromAlias + '\'' +
                ", to='" + to + '\'' +
                ", toAlias='" + toAlias + '\'' +
                ", value=" + value +
                ", conformations='" + conformations + '\'' +
                '}';
    }
}
