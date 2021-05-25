package com.dsc.model.Graph;

import java.util.Objects;

public class Edge {

    private String source;
    private String target;
    private String id;
    private Integer tradeCount;
    private String tradeVal;
    private String tradeDescription;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Integer getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(Integer tradeCount) {
        this.tradeCount = tradeCount;
    }

    public String getTradeVal() {
        return tradeVal;
    }

    public void setTradeVal(String tradeVal) {
        this.tradeVal = tradeVal;
    }

    public String getTradeDescription() {
        return tradeDescription;
    }

    public void setTradeDescription(String tradeDescription) {
        this.tradeDescription = tradeDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(source, edge.source) &&
                Objects.equals(target, edge.target) &&
                Objects.equals(id, edge.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(source, target, id);
    }
}
