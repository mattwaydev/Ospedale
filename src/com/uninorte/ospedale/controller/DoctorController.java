/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;
import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;


/**
 *
 * @author Matt
 */
public class DoctorController {
    
    public Response<String> register(Object dto) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<String> update(long id, Object dto) {
        return ResponseFactory.serverError("not implemented");
    }
    
}
