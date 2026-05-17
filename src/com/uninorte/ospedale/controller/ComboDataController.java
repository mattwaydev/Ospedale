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
public class ComboDataController {
    
    public Response<Object> getSpecialties() {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<Object> getDoctorsBySpecialty(String specialty) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<Object> getRoomTypes() {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<Object> getAppointmentIdsForDoctor(long doctorId, Object filter) {
        return ResponseFactory.serverError("not implemented");
    }
    
}
