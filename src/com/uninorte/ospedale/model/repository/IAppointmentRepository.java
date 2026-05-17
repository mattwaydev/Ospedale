/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.uninorte.ospedale.model.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import packagee.Appointment;
import packagee.AppointmentStatus;
/**
 *
 * @author Samuel Ramirez
 */
public interface IAppointmentRepository {
    Optional<Appointment> findById(String id);
    List<Appointment> findByPatientId(long pid);
    List<Appointment> findByDoctorId(long did);
    List<Appointment> findByDoctorIdAndStatus(long did, AppointmentStatus s);
    
    boolean doctorIsAvailableAt(long did, LocalDateTime slot);
    
    String nextIdForPatient(long pid);
    
    void save(Appointment a);
}
