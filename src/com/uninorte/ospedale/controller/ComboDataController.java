/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.model.dto.DoctorComboDTO;
import com.uninorte.ospedale.model.dto.PatientComboDTO;
import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import com.uninorte.ospedale.model.repository.IHospitalizationRepository;
import com.uninorte.ospedale.model.repository.IPatientRepository;
import java.util.ArrayList;
import java.util.List;
import com.uninorte.ospedale.model.entity.Appointment;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.Hospitalization;
import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.enums.AppointmentStatus;
import com.uninorte.ospedale.model.enums.HospitalizationStatus;
import com.uninorte.ospedale.model.enums.RoomType;
import com.uninorte.ospedale.model.enums.Specialty;
/**
 *
 * @author Matt
 */
public class ComboDataController {

    private final IDoctorRepository doctorRepository;
    private final IAppointmentRepository appointmentRepository;
    private final IHospitalizationRepository hospitalizationRepository;
    private final IPatientRepository patientRepository;

    public ComboDataController(IDoctorRepository doctorRepository,
            IAppointmentRepository appointmentRepository,
            IHospitalizationRepository hospitalizationRepository,
            IPatientRepository patientRepository) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.hospitalizationRepository = hospitalizationRepository;
        this.patientRepository = patientRepository;
    }

    public Response<List<String>> getSpecialties() {
        List<String> specialties = new ArrayList<>();
        for (Specialty s : Specialty.values()) {
            specialties.add(s.name().replace("_", " & "));
        }
        return ResponseFactory.ok("Especialidades", specialties);
    }

    public Response<List<String>> getRoomTypes() {
        List<String> roomTypes = new ArrayList<>();
        for (RoomType r : RoomType.values()) {
            roomTypes.add(r.name());
        }
        return ResponseFactory.ok("Tipos de habitación", roomTypes);
    }

    public Response<List<DoctorComboDTO>> getDoctorsCombo() {
        List<Doctor> doctors = doctorRepository.findAllDoctors();
        List<DoctorComboDTO> result = new ArrayList<>();
        for (Doctor d : doctors) {
            result.add(new DoctorComboDTO(d.getId(),
                    d.getFirstname() + " " + d.getLastname(),
                    d.getSpecialty().name()));
        }
        return ResponseFactory.ok("Médicos", result);
    }

    public Response<List<String>> getAppointmentIdsByDoctorAndStatus(long doctorId, String statusName) {
        AppointmentStatus status;
        try {
            status = AppointmentStatus.valueOf(statusName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest("Estado de cita inválido: " + statusName);
        }
        List<Appointment> appts = appointmentRepository.findByDoctorIdAndStatus(doctorId, status);
        List<String> ids = new ArrayList<>();
        for (Appointment a : appts) {
            ids.add(a.getId());
        }
        return ResponseFactory.ok("IDs de citas del médico", ids);
    }

    public Response<List<String>> getReschedulableForDoctor(long doctorId) {
        List<Appointment> all = appointmentRepository.findByDoctorId(doctorId);
        List<String> ids = new ArrayList<>();
        for (Appointment a : all) {
            if (a.getStatus() == AppointmentStatus.REQUESTED
                    || a.getStatus() == AppointmentStatus.PENDING) {
                ids.add(a.getId());
            }
        }
        return ResponseFactory.ok("IDs de citas reagendables", ids);
    }

    public Response<List<String>> getHospitalizationIdsByStatus(String statusName) {
        HospitalizationStatus status;
        try {
            status = HospitalizationStatus.valueOf(statusName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest("Estado de hospitalización inválido: " + statusName);
        }
        List<Hospitalization> hosps = hospitalizationRepository.findByStatus(status);
        List<String> ids = new ArrayList<>();
        for (Hospitalization h : hosps) {
            ids.add(h.getId());
        }
        return ResponseFactory.ok("IDs de hospitalizaciones", ids);
    }

    public Response<List<PatientComboDTO>> getPatientsCombo() {
        List<Patient> patients = patientRepository.findAllPatients();
        List<PatientComboDTO> items = new ArrayList<>();
        for (Patient p : patients) {
            items.add(new PatientComboDTO(p.getId(),
                    p.getFirstname() + " " + p.getLastname()));
        }
        return ResponseFactory.ok("Pacientes", items);
    }

    public Response<List<String>> getAppointmentIdsByPatientAndCancelable(long pid) {
        List<Appointment> appts = appointmentRepository.findByPatientId(pid);
        List<String> ids = new ArrayList<>();
        for (Appointment a : appts) {
            if (a.getStatus() != AppointmentStatus.COMPLETED
                    && a.getStatus() != AppointmentStatus.CANCELED) {
                ids.add(a.getId());
            }
        }
        return ResponseFactory.ok("IDs de citas cancelables", ids);
    }
}
