package io.ont.bean;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Result {
    public String qid;
    public String method;
    public int errorCode;
    public String errorInfo;
    public Object result;

    public Result() {
    }

    public Result(String qid,String method, int errorCode, String errorInfo, Object result) {
        this.qid = qid;
        this.method = method;
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
        this.result = result;
    }

}
