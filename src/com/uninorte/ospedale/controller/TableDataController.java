/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import com.uninorte.ospedale.model.repository.IPatientRepository;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import packagee.Appointment;
import packagee.AppointmentStatus;
import packagee.Doctor;
import packagee.Patient;


/**
 *
 * @author Matt
 */
public class TableDataController {
    
   private final IAppointmentRepository appointmentRepository;
    private final IPatientRepository patientRepository;
    private final IDoctorRepository doctorRepository;

    public TableDataController(IAppointmentRepository appointmentRepository,
            IPatientRepository patientRepository,
            IDoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public Response<Object> getPatientAppointments(long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        appointments.sort(Comparator.comparing(Appointment::getDatetime).reversed());
        List<String[]> rows = new ArrayList<>();
        for (Appointment a : appointments) {
            rows.add(new String[]{
                a.getId(),
                a.getDatetime().toString(),
                a.getDoctor().getFirstname() + " " + a.getDoctor().getLastname(),
                a.getSpecialty().name(),
                a.isType() ? "In-person" : "Remote",
                a.getStatus().name()
            });
        }
        return ResponseFactory.ok("Patient appointments", rows);
    }

    public Response<Object> getDoctorAppointments(long doctorId, boolean onlyPending) {
        List<Appointment> appointments;
        if (onlyPending) {
            appointments = appointmentRepository.findByDoctorIdAndStatus(doctorId, AppointmentStatus.PENDING);
        } else {
            appointments = appointmentRepository.findByDoctorId(doctorId);
        }
        appointments.sort(Comparator.comparing(Appointment::getDatetime).reversed());
        List<String[]> rows = new ArrayList<>();
        for (Appointment a : appointments) {
            rows.add(new String[]{
                a.getId(),
                a.getDatetime().toString(),
                a.getPatient().getFirstname() + " " + a.getPatient().getLastname(),
                a.getSpecialty().name(),
                a.isType() ? "In-person" : "Remote",
                a.getStatus().name()
            });
        }
        return ResponseFactory.ok("Doctor appointments", rows);
    }

    public Response<Object> getAllPatients() {
        List<Patient> patients = patientRepository.findAllPatients();
        List<String[]> rows = new ArrayList<>();
        for (Patient p : patients) {
            rows.add(new String[]{
                String.valueOf(p.getId()),
                p.getFirstname(),
                p.getLastname(),
                p.getUsername()
            });
        }
        return ResponseFactory.ok("All patients", rows);
    }

    public Response<Object> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAllDoctors();
        List<String[]> rows = new ArrayList<>();
        for (Doctor d : doctors) {
            rows.add(new String[]{
                String.valueOf(d.getId()),
                d.getFirstname(),
                d.getLastname(),
                d.getSpecialty().name()
            });
        }
        return ResponseFactory.ok("All doctors", rows);
    }
}
