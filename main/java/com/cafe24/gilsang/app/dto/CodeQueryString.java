package com.cafe24.app.sample.dto;

import com.cafe24.app.sample.dto.core.BaseModel;

public class CodeQueryString extends BaseModel {
    private String code;
    private String state;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
