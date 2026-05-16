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
public class HospitalizationController {
    
    public Response<String> request(Object dto) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<String> approve(String hospId) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<String> deny(String hospId) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<String> fromAppointment(String appointmentId, Object dto) {
        return ResponseFactory.serverError("not implemented");
    }
    
}
