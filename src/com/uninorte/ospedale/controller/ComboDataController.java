/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.model.dto.DoctorComboDTO;
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

    public Response<Object> getSpecialties() {
        List<String> specialties = new ArrayList<>();
        for (Specialty s : Specialty.values()) {
            specialties.add(s.name().replace("_", " & "));
        }
        return ResponseFactory.ok("Specialties", specialties);
    }

    public Response<Object> getRoomTypes() {
        List<String> roomTypes = new ArrayList<>();
        for (RoomType r : RoomType.values()) {
            roomTypes.add(r.name());
        }
        return ResponseFactory.ok("Room types", roomTypes);
    }

    public Response<Object> getDoctors() {
        List<Doctor> doctors = doctorRepository.findAllDoctors();
        List<String[]> result = new ArrayList<>();
        for (Doctor d : doctors) {
            result.add(new String[]{
                String.valueOf(d.getId()),
                d.getFirstname() + " " + d.getLastname(),
                d.getSpecialty().name()
            });
        }
        return ResponseFactory.ok("Doctors", result);
    }

    public List<DoctorComboDTO> getDoctorsCombo() {
        List<Doctor> doctors = doctorRepository.findAllDoctors();
        List<DoctorComboDTO> result = new ArrayList<>();
        for (Doctor d : doctors) {
            result.add(new DoctorComboDTO(d.getId(),
                    d.getFirstname() + " " + d.getLastname(),
                    d.getSpecialty().name()));
        }
        return result;
    }

    public Response<Object> getDoctorsBySpecialty(String specialty) {
        Specialty spec;
        try {
            spec = Specialty.valueOf(specialty.toUpperCase().replace(" & ", "_"));
        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest("Invalid specialty");
        }
        List<Doctor> doctors = doctorRepository.findBySpecialty(spec);
        List<String[]> result = new ArrayList<>();
        for (Doctor d : doctors) {
            result.add(new String[]{
                String.valueOf(d.getId()),
                d.getFirstname() + " " + d.getLastname()
            });
        }
        return ResponseFactory.ok("Doctors by specialty", result);
    }

    public Response<Object> getAppointmentIdsByDoctorAndStatus(long doctorId, String statusName) {
        AppointmentStatus status;
        try {
            status = AppointmentStatus.valueOf(statusName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest("Invalid appointment status: " + statusName);
        }
        List<Appointment> appts = appointmentRepository.findByDoctorIdAndStatus(doctorId, status);
        List<String> ids = new ArrayList<>();
        for (Appointment a : appts) {
            ids.add(a.getId());
        }
        return ResponseFactory.ok("Appointment IDs", ids);
    }

    public Response<Object> getReschedulableForDoctor(long doctorId) {
        List<Appointment> all = appointmentRepository.findByDoctorId(doctorId);
        List<String> ids = new ArrayList<>();
        for (Appointment a : all) {
            if (a.getStatus() == AppointmentStatus.REQUESTED
                    || a.getStatus() == AppointmentStatus.PENDING) {
                ids.add(a.getId());
            }
        }
        return ResponseFactory.ok("Reschedulable appointment IDs", ids);
    }

    public Response<Object> getHospitalizationIdsByStatus(String statusName) {
        HospitalizationStatus status;
        try {
            status = HospitalizationStatus.valueOf(statusName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest("Invalid hospitalization status: " + statusName);
        }
        List<Hospitalization> hosps = hospitalizationRepository.findByStatus(status);
        List<String> ids = new ArrayList<>();
        for (Hospitalization h : hosps) {
            ids.add(h.getId());
        }
        return ResponseFactory.ok("Hospitalization IDs", ids);
    }

    public Response<Object> getPatientsCombo() {
        List<Patient> patients = patientRepository.findAllPatients();
        List<String> items = new ArrayList<>();
        for (Patient p : patients) {
            items.add(p.getId() + " - " + p.getFirstname() + " " + p.getLastname());
        }
        return ResponseFactory.ok("Patients combo", items);
    }

    public List<String> getAppointmentIdsByPatientAndCancelable(long pid) {
        List<Appointment> appts = appointmentRepository.findByPatientId(pid);
        List<String> ids = new ArrayList<>();
        for (Appointment a : appts) {
            if (a.getStatus() != AppointmentStatus.COMPLETED
                    && a.getStatus() != AppointmentStatus.CANCELED) {
                ids.add(a.getId());
            }
        }
        return ids;
    }
}
