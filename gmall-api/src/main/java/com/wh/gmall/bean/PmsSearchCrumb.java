package com.wh.gmall.bean;

import java.io.Serializable;

public class PmsSearchCrumb implements Serializable {

    private String urlParam;
    private String valueName;
    private String valueId;
    private static final long serialVersionUID = 1L;
    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }
}
