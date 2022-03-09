package io.ont.exception;


public class OntologyHolderException extends RuntimeException {

    private String errDesCN;

    private String errDesEN;

    private int errCode;

    private String action;

    public OntologyHolderException(String msg) {
        super(msg);
    }

    public OntologyHolderException() {
        super();
    }

    public String getErrDesCN() {
        return errDesCN;
    }

    public String getErrDesEN() {
        return errDesEN;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getAction() {
        return action;
    }
}
