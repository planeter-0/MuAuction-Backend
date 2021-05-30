package icu.planeter.muauction.common.response;
/**
 * @description: Response status codes with messages
 * @author Planeter
 * @date 2021/5/27 18:59
 * @status dev
 */
public enum ResponseCode {

    SUCCESS(0, "SUCCESS"),
    FAILED(-1, "ERROR"),
    NEED_LOGIN(100, "NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT");

    private final int code;
    private final String message;

    ResponseCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}