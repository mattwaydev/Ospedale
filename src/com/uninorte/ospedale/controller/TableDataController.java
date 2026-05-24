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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import com.uninorte.ospedale.model.entity.Appointment;
import com.uninorte.ospedale.model.entity.Hospitalization;
import com.uninorte.ospedale.model.entity.Prescription;
import com.uninorte.ospedale.model.enums.AppointmentStatus;
import com.uninorte.ospedale.model.enums.HospitalizationStatus;


/**
 *
 * @author Matt
 */
public class TableDataController {

    private final IAppointmentRepository appointmentRepository;
    private final IHospitalizationRepository hospitalizationRepository;

    public TableDataController(IAppointmentRepository appointmentRepository,
            IHospitalizationRepository hospitalizationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.hospitalizationRepository = hospitalizationRepository;
    }

    public Response<List<AppointmentRowDTO>> getPatientAppointments(long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        appointments.sort(Comparator.comparing(Appointment::getDatetime).reversed());
        List<AppointmentRowDTO> rows = new ArrayList<>();
        for (Appointment a : appointments) {
            rows.add(toRow(a));
        }
        return ResponseFactory.ok("Citas del paciente", rows);
    }

    public Response<List<AppointmentRowDTO>> getDoctorAppointments(long doctorId, boolean onlyPending) {
        List<Appointment> appointments;
        if (onlyPending) {
            appointments = appointmentRepository.findByDoctorIdAndStatus(doctorId, AppointmentStatus.PENDING);
        } else {
            appointments = appointmentRepository.findByDoctorId(doctorId);
        }
        appointments.sort(Comparator.comparing(Appointment::getDatetime).reversed());
        List<AppointmentRowDTO> rows = new ArrayList<>();
        for (Appointment a : appointments) {
            rows.add(toRow(a));
        }
        return ResponseFactory.ok("Citas del médico", rows);
    }

    public Response<List<HospitalizationRowDTO>> getHospitalizationsByPatient(long pid) {
        List<Hospitalization> list = hospitalizationRepository.findByPatientId(pid);
        List<HospitalizationRowDTO> rows = new ArrayList<>();
        for (Hospitalization h : list) {
            rows.add(toRow(h));
        }
        return ResponseFactory.ok("Hospitalizaciones del paciente", rows);
    }

    public Response<List<HospitalizationRowDTO>> getHospitalizationRequests() {
        List<Hospitalization> list = hospitalizationRepository.findByStatus(HospitalizationStatus.REQUESTED);
        List<HospitalizationRowDTO> rows = new ArrayList<>();
        for (Hospitalization h : list) {
            rows.add(toRow(h));
        }
        return ResponseFactory.ok("Solicitudes de hospitalización", rows);
    }

    public Response<List<PrescriptionRowDTO>> getPrescriptions(String apptId) {
        Optional<Appointment> found = appointmentRepository.findById(apptId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Cita no encontrada");
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
        return ResponseFactory.ok("Recetas de la cita", rows);
    }

    private AppointmentRowDTO toRow(Appointment a) {
        return new AppointmentRowDTO(
                a.getId(),
                a.getDatetime().toString(),
                a.getDoctor().getFirstname() + " " + a.getDoctor().getLastname(),
                a.getPatient().getFirstname() + " " + a.getPatient().getLastname(),
                a.getSpecialty().name(),
                a.isType() ? "In-person" : "Remote",
                a.getStatus().name()
        );
    }

    private HospitalizationRowDTO toRow(Hospitalization h) {
        return new HospitalizationRowDTO(
                h.getId(),
                h.getDate().toString(),
                h.getPatient().getFirstname() + " " + h.getPatient().getLastname(),
                h.getDoctor().getFirstname() + " " + h.getDoctor().getLastname(),
                h.getRoomType().name(),
                h.getStatus().name()
        );
    }
}
