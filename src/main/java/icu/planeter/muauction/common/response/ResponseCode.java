package icu.planeter.muauction.common.response;

import java.lang.invoke.WrongMethodTypeException;

/**
 * @description: Response status codes with messages
 * @author Planeter
 * @date 2021/5/27 18:59
 * @status dev
 */
public enum ResponseCode {
    // The first two digits indicate the classification and the last two digits indicate the identifier
    // except SUCCESS and FAILED
    SUCCESS(0, "SUCCESS"),
    FAILED(-1, "ERROR"),
    EmailUsed(1001,"Email Used"),
    EmailWrong(1002,"Email Wrong"),
    PasswordWrong(1003,"Password Wrong"),
    NoSuchRole(1004,"No Such Role"),
    NoSuchPermission(1005,"No Such Permission"),
    AccountForbidden(1006,"Account Forbidden"),
    VerifyCodeWrong(1007,"VerifyCode Wrong"),
    NoSuchEntity(2001,"No Such Entity"),
    Sold(2002,"Sold"),
    UnPassed(2003,"UnPassed"),
    UploadFailed(3004,"Upload Failed");


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