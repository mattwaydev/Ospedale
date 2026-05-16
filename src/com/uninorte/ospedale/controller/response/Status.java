/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller.response;

/**
 *
 * @author Matt
 */
public enum Status {
    OK(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    CONFLICT(409),
    SERVER_ERROR(500);
    
    private final int code;
    
    Status(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
