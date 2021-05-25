package com.dsc.constant;

public enum TradeTypeEnum {
    IN("IN","转入"),
    OUT("OUT","转出")

    ;

    private String code;
    private String desc;

    TradeTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
