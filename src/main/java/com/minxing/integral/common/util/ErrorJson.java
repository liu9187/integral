package com.minxing.integral.common.util;

import com.alibaba.fastjson.JSONObject;

public class ErrorJson {
    private Errors errors;

    public ErrorJson(String code, String message) {
        this.errors = new Errors();
        this.errors.setMessage(message);
        this.errors.setStatus_code(code);
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }

    public static class Errors {
        private String status_code;
        private String message;

        public String getStatus_code() {
            return status_code;
        }

        public void setStatus_code(String status_code) {
            this.status_code = status_code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = null == message ? "操作失败" : message;
        }
    }
}
