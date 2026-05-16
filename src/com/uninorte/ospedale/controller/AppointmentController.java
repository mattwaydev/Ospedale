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
public class AppointmentController {
    
    public Response<String> request(Object dto) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<String> accept(String appointmentId, long doctorId) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<String> complete(String appointmentId, Object dto) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<String> cancel(String appointmentId) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<String> reschedule(String appointmentId, String newTime, String reason) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<String> prescribe(String appointmentId, Object dto) {
        return ResponseFactory.serverError("not implemented");
    }
    
}
