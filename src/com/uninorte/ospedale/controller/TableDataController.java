/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.model.dto.AppointmentRowDTO;
import com.uninorte.ospedale.model.dto.HospitalizationRowDTO;
import com.uninorte.ospedale.model.dto.PrescriptionRowDTO;
import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import com.uninorte.ospedale.model.repository.IHospitalizationRepository;
import com.uninorte.ospedale.model.repository.IPatientRepository;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.uninorte.ospedale.model.entity.Appointment;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.Hospitalization;
import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.entity.Prescription;
import com.uninorte.ospedale.model.enums.AppointmentStatus;
import com.uninorte.ospedale.model.enums.HospitalizationStatus;


/**
 *
 * @author Matt
 */
public class TableDataController {

   private final IAppointmentRepository appointmentRepository;
    private final IPatientRepository patientRepository;
    private final IDoctorRepository doctorRepository;
    private final IHospitalizationRepository hospitalizationRepository;

    public TableDataController(IAppointmentRepository appointmentRepository,
            IPatientRepository patientRepository,
            IDoctorRepository doctorRepository,
            IHospitalizationRepository hospitalizationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.hospitalizationRepository = hospitalizationRepository;
    }

    public List<AppointmentRowDTO> getPatientAppointments(long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        appointments.sort(Comparator.comparing(Appointment::getDatetime).reversed());
        List<AppointmentRowDTO> rows = new ArrayList<>();
        for (Appointment a : appointments) {
            rows.add(new AppointmentRowDTO(
                    a.getId(),
                    a.getDatetime().toString(),
                    a.getDoctor().getFirstname() + " " + a.getDoctor().getLastname(),
                    a.getPatient().getFirstname() + " " + a.getPatient().getLastname(),
                    a.getSpecialty().name(),
                    a.isType() ? "In-person" : "Remote",
                    a.getStatus().name()
            ));
        }
        return rows;
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

    public List<HospitalizationRowDTO> getHospitalizationsByPatient(long pid) {
        List<Hospitalization> list = hospitalizationRepository.findByPatientId(pid);
        List<HospitalizationRowDTO> rows = new ArrayList<>();
        for (Hospitalization h : list) {
            rows.add(new HospitalizationRowDTO(
                    h.getId(),
                    h.getDate().toString(),
                    h.getPatient().getFirstname() + " " + h.getPatient().getLastname(),
                    h.getDoctor().getFirstname() + " " + h.getDoctor().getLastname(),
                    h.getRoomType().name(),
                    h.getStatus().name()
            ));
        }
        return rows;
    }

    public List<HospitalizationRowDTO> getHospitalizationRequests() {
        List<Hospitalization> list = hospitalizationRepository.findByStatus(HospitalizationStatus.REQUESTED);
        List<HospitalizationRowDTO> rows = new ArrayList<>();
        for (Hospitalization h : list) {
            rows.add(new HospitalizationRowDTO(
                    h.getId(),
                    h.getDate().toString(),
                    h.getPatient().getFirstname() + " " + h.getPatient().getLastname(),
                    h.getDoctor().getFirstname() + " " + h.getDoctor().getLastname(),
                    h.getRoomType().name(),
                    h.getStatus().name()
            ));
        }
        return rows;
    }

    public List<PrescriptionRowDTO> getPrescriptions(String apptId) {
        java.util.Optional<Appointment> found = appointmentRepository.findById(apptId);
        if (found.isEmpty()) return new ArrayList<>();
        List<PrescriptionRowDTO> rows = new ArrayList<>();
        for (Prescription p : found.get().getPrescriptions()) {
            rows.add(new PrescriptionRowDTO(
                    p.getMedicationName(),
                    String.valueOf(p.getDose()),
                    p.getAdministrationRoute(),
                    String.valueOf(p.getTreatmentDuration()),
                    p.getAdditionalInstructions(),
                    String.valueOf(p.getFrecuency())
            ));
        }
        return rows;
    }
}
