package com.dsc.model.Graph;

import java.util.Objects;

public class Edge {

    private String source;
    private String target;
    private String id;
    private String description;
    private String tradeVal;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTradeVal() {
        return tradeVal;
    }

    public void setTradeVal(String tradeVal) {
        this.tradeVal = tradeVal;
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
