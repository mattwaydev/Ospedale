/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import com.uninorte.ospedale.model.repository.IHospitalizationRepository;
import com.uninorte.ospedale.model.repository.IPatientRepository;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import packagee.Appointment;
import packagee.AppointmentStatus;
import packagee.Doctor;
import packagee.Hospitalization;
import packagee.HospitalizationStatus;
import packagee.Patient;
import packagee.RoomType;

/**
 *
 * @author Matt
 */
public class HospitalizationController {
    
    private final IHospitalizationRepository hospitalizationRepository;
    private final IPatientRepository patientRepository;
    private final IDoctorRepository doctorRepository;
    private final IAppointmentRepository appointmentRepository;

    public HospitalizationController(IHospitalizationRepository hospitalizationRepository,
            IPatientRepository patientRepository,
            IDoctorRepository doctorRepository,
            IAppointmentRepository appointmentRepository) {
        this.hospitalizationRepository = hospitalizationRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Response<Object> request(long patientId, long doctorId, String date,
            String reason, String roomType, String observations) {

        Optional<packagee.User> patientFound = patientRepository.findById(patientId);
        if (patientFound.isEmpty())
            return ResponseFactory.notFound("Patient not found");

        Optional<packagee.User> doctorFound = doctorRepository.findById(doctorId);
        if (doctorFound.isEmpty())
            return ResponseFactory.notFound("Doctor not found");

        LocalDate admissionDate;
        try {
            admissionDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return ResponseFactory.badRequest("Invalid date format, use YYYY-MM-DD");
        }

        RoomType room;
        try {
            room = RoomType.valueOf(roomType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest("Invalid room type");
        }

        Patient patient = (Patient) patientFound.get();
        Doctor doctor = (Doctor) doctorFound.get();
        String id = hospitalizationRepository.nextIdForPatient(patientId);
        Hospitalization hosp = new Hospitalization(id, patient, doctor, admissionDate, reason, room, observations);
        hospitalizationRepository.save(hosp);
        return ResponseFactory.ok("Hospitalization requested successfully", id);
    }

    public Response<Object> approve(String hospitalizationId, long doctorId) {
        Optional<Hospitalization> found = hospitalizationRepository.findById(hospitalizationId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Hospitalization not found");
        Hospitalization hosp = found.get();
        if (hosp.getStatus() != HospitalizationStatus.REQUESTED)
            return ResponseFactory.badRequest("Hospitalization is not in REQUESTED status");
        hosp.setStatus(HospitalizationStatus.ONGOING);
        return ResponseFactory.ok("Hospitalization approved", null);
    }

    public Response<Object> deny(String hospitalizationId, long doctorId) {
        Optional<Hospitalization> found = hospitalizationRepository.findById(hospitalizationId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Hospitalization not found");
        Hospitalization hosp = found.get();
        if (hosp.getStatus() != HospitalizationStatus.REQUESTED)
            return ResponseFactory.badRequest("Hospitalization is not in REQUESTED status");
        hosp.setStatus(HospitalizationStatus.CANCELED);
        return ResponseFactory.ok("Hospitalization denied", null);
    }

    public Response<Object> fromAppointment(String appointmentId, long doctorId,
            String date, String reason, String roomType, String observations) {

        Optional<Appointment> apptFound = appointmentRepository.findById(appointmentId);
        if (apptFound.isEmpty())
            return ResponseFactory.notFound("Appointment not found");

        Appointment appointment = apptFound.get();
        if (appointment.getDoctor().getId() != doctorId)
            return ResponseFactory.unauthorized("Doctor is not assigned to this appointment");

        LocalDate admissionDate;
        try {
            admissionDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return ResponseFactory.badRequest("Invalid date format, use YYYY-MM-DD");
        }

        RoomType room;
        try {
            room = RoomType.valueOf(roomType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest("Invalid room type");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        String id = hospitalizationRepository.nextIdForPatient(patient.getId());
        Hospitalization hosp = new Hospitalization(id, patient, doctor, admissionDate, reason, room, observations, HospitalizationStatus.ONGOING);
        hospitalizationRepository.save(hosp);
        return ResponseFactory.ok("Hospitalization created from appointment", id);
    }
}
