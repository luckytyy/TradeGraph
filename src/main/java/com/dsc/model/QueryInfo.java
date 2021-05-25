package com.dsc.model;


public class QueryInfo {

    private Long id;
    private String sourceToken;
    private String type ;
    private String tokens;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceToken() {
        return sourceToken;
    }

    public void setSourceToken(String sourceToken) {
        this.sourceToken = sourceToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "QueryInfo{" +
                "id=" + id +
                ", sourceToken='" + sourceToken + '\'' +
                ", type='" + type + '\'' +
                ", tokens='" + tokens + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
