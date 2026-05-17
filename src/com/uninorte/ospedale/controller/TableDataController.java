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
public class TableDataController {
    
    public Response<Object> getPatientAppointments(long patientId) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<Object> getDoctorAppointments(long doctorId, Object filter) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<Object> getHospitalizationRequests() {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<Object> getHospitalizationsByPatient(long patientId) {
        return ResponseFactory.serverError("not implemented");
    }
    
    public Response<Object> getPrescriptions(String appointmentId) {
        return ResponseFactory.serverError("not implemented");
    }
    
}
