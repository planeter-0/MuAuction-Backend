package icu.planeter.muauction.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @description: Uniform response with data
 * @author Planeter
 * @date 2021/4/29 20:47
 * @status ok
 */
public class Response<T> {
    private int statusCode;
    private String message;
    private T data;
    // SUCCESS
    public Response() {
        this.statusCode = ResponseCode.SUCCESS.getCode();
        this.message = ResponseCode.SUCCESS.getMessage();
    }
    // SUCCESS with data
    public Response(T data) {
        this.statusCode = ResponseCode.SUCCESS.getCode();
        this.message = ResponseCode.SUCCESS.getMessage();
        this.data = data;
    }
    // Just Code
    public Response(ResponseCode code){
        this.statusCode =code.getCode();
        this.message =code.getMessage();
    }
    // Code and data
    public Response(ResponseCode code, T data){
        this.statusCode =code.getCode();
        this.message =code.getMessage();
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess(){
        return this.getStatusCode() ==  ResponseCode.SUCCESS.getCode();
    }
    @Override
    public String toString() {
        return "ResponseData{" +
                "data=" + data +
                "} " + super.toString();
    }
}