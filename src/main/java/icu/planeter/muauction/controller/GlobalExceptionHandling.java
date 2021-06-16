package icu.planeter.muauction.controller;

/**
 * @author Planeter
 * @description: GlobalExceptionHandling
 * @date 2021/5/5 17:35
 * @status dev
 */

import icu.planeter.muauction.common.response.Response;
import icu.planeter.muauction.common.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandling {
    /**
     * shiro
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ShiroException.class)
    public Response<Object> doHandleShiroException(
            ShiroException e) {
        Response<Object> r = new Response<>();
        if (e instanceof AuthorizationException) {
            r = new Response<>(ResponseCode.NoSuchPermission);
        } else {
            e.printStackTrace();
        }
        return r;
    }
    /**
     * JPA
     */
    @ExceptionHandler(PersistenceException.class)
    public Response<Object> doHandlePersistenceException(
            PersistenceException e) {
        Response<Object> r = new Response<>();
        if (e instanceof EntityNotFoundException) {
            r = new Response<>(ResponseCode.NoSuchEntity);
        } else {
            e.printStackTrace();
        }
        log.warn("No Such Entity");
        return r;
    }

}