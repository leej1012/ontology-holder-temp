package io.ont.utils;


public enum ErrorInfo {

    /**
     * success
     */
    SUCCESS(0, "SUCCESS", "成功"),

    /**
     * param error
     */
    PARAM_ERROR(61001, "FAIL, param error.", "参数错误"),

    /**
     * not found in db
     */
    NOT_FOUND(61002, "FAIL, not found.", "未找到"),

    /**
     * error net when communicate
     */
    COMM_NET_FAIL(64001, "FAIL, net communication fail.", "网络通信异常"),
    ;

    private int errorCode;
    private String errorDescEN;
    private String errorDescCN;

    ErrorInfo(int errorCode, String errorDescEN, String errorDescCN) {
        this.errorCode = errorCode;
        this.errorDescEN = errorDescEN;
        this.errorDescCN = errorDescCN;
    }

    public int code() {
        return errorCode;
    }

    public String descEN() {
        return errorDescEN;
    }

    public String descCN() {
        return errorDescCN;
    }


}
