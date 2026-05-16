/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller.response;

/**
 *
 * @author Matt
 */
public class ResponseFactory {
    private ResponseFactory() {}
    
    public static <T> Response<T> ok(String message, T data) {
        return new Response<>(Status.OK.getCode(), message, data);
    }
    
    public static <T> Response<T> badRequest(String message) {
        return new Response<>(Status.BAD_REQUEST.getCode(), message, null);
    }
    
    public static <T> Response<T> unauthorized(String message) {
        return new Response<>(Status.UNAUTHORIZED.getCode(), message, null);
    }
    
    public static <T> Response<T> notFound(String message) {
        return new Response<>(Status.NOT_FOUND.getCode(), message, null);
    }
    
    public static <T> Response<T> conflict(String message) {
        return new Response<>(Status.CONFLICT.getCode(), message, null);
    }
    
    public static <T> Response<T> serverError(String message) {
        return new Response<>(Status.SERVER_ERROR.getCode(), message, null);
    }
}
