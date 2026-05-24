/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.controller.validator.AppointmentValidator;
import com.uninorte.ospedale.controller.validator.HospitalizationValidator;
import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import com.uninorte.ospedale.model.repository.IHospitalizationRepository;
import com.uninorte.ospedale.model.repository.IPatientRepository;
import java.time.LocalDate;
import java.util.Optional;
import com.uninorte.ospedale.model.entity.Appointment;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.Hospitalization;
import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.enums.AppointmentStatus;
import com.uninorte.ospedale.model.enums.HospitalizationStatus;
import com.uninorte.ospedale.model.enums.RoomType;
import com.uninorte.ospedale.model.dto.HospitalizationFromAppointmentDTO;
import com.uninorte.ospedale.model.dto.HospitalizationRequestDTO;
import com.uninorte.ospedale.model.entity.User;

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

    public Response<Object> request(HospitalizationRequestDTO dto) {
        return request(dto.patiendId(), dto.doctorId(), dto.date(), dto.reason(), dto.roomType(), dto.observations());
    }

    public Response<Object> request(long patientId, long doctorId, String date,
            String reason, String roomType, String observations) {

        Optional<User> patientFound = patientRepository.findById(patientId);
        if (patientFound.isEmpty())
            return ResponseFactory.notFound("Paciente no encontrado");

        Optional<User> doctorFound = doctorRepository.findById(doctorId);
        if (doctorFound.isEmpty())
            return ResponseFactory.notFound("Médico no encontrado");

        Optional<String> dateError = AppointmentValidator.validateDate(date);
        if (dateError.isPresent()) return ResponseFactory.badRequest(dateError.get());

        Optional<String> roomError = HospitalizationValidator.validateRoomType(roomType);
        if (roomError.isPresent()) return ResponseFactory.badRequest(roomError.get());

        LocalDate admissionDate = LocalDate.parse(date);
        RoomType room = RoomType.valueOf(roomType.toUpperCase());

        Patient patient = (Patient) patientFound.get();
        Doctor doctor = (Doctor) doctorFound.get();
        String id = hospitalizationRepository.nextIdForPatient(patientId);
        Hospitalization hosp = new Hospitalization(id, patient, doctor, admissionDate, reason, room, observations);
        hospitalizationRepository.save(hosp);
        patient.addHospitalization(hosp);
        doctor.addHospitalization(hosp);
        return ResponseFactory.ok("Hospitalización solicitada exitosamente", id);
    }

    public Response<Object> approve(String hospitalizationId, long doctorId) {
        Optional<Hospitalization> found = hospitalizationRepository.findById(hospitalizationId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Hospitalización no encontrada");
        Hospitalization hosp = found.get();
        if (hosp.getStatus() != HospitalizationStatus.REQUESTED)
            return ResponseFactory.badRequest("La hospitalización no está en estado REQUESTED");
        hosp.setStatus(HospitalizationStatus.ONGOING);
        return ResponseFactory.ok("Hospitalización aprobada", null);
    }

    public Response<Object> deny(String hospitalizationId, long doctorId) {
        Optional<Hospitalization> found = hospitalizationRepository.findById(hospitalizationId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Hospitalización no encontrada");
        Hospitalization hosp = found.get();
        if (hosp.getStatus() != HospitalizationStatus.REQUESTED)
            return ResponseFactory.badRequest("Solo se puede denegar una hospitalización en estado REQUESTED");
        hosp.setStatus(HospitalizationStatus.CANCELED);
        return ResponseFactory.ok("Hospitalización denegada", null);
    }

    public Response<Object> cancel(String hospId, long actorId) {
        return doCancel(hospId);
    }

    private Response<Object> doCancel(String hospitalizationId) {
        Optional<Hospitalization> found = hospitalizationRepository.findById(hospitalizationId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Hospitalización no encontrada");
        Hospitalization hosp = found.get();
        if (hosp.getStatus() == HospitalizationStatus.CANCELED)
            return ResponseFactory.badRequest("La hospitalización ya está cancelada");
        if (hosp.getStatus() == HospitalizationStatus.ONGOING || hosp.getStatus() == HospitalizationStatus.REQUESTED) {
            hosp.setStatus(HospitalizationStatus.CANCELED);
            return ResponseFactory.ok("Hospitalización cancelada", null);
        }
        return ResponseFactory.badRequest("La hospitalización no se puede cancelar en su estado actual");
    }

    public Response<Object> fromAppointment(String appointmentId, long doctorId,
            HospitalizationFromAppointmentDTO dto) {
        return fromAppointment(appointmentId, doctorId, dto.date(), dto.reason(),
                dto.roomType(), dto.observations());
    }

    public Response<Object> fromAppointment(String appointmentId, long doctorId,
            String date, String reason, String roomType, String observations) {

        Optional<Appointment> apptFound = appointmentRepository.findById(appointmentId);
        if (apptFound.isEmpty())
            return ResponseFactory.notFound("Cita no encontrada");

        Appointment appointment = apptFound.get();
        if (appointment.getDoctor().getId() != doctorId)
            return ResponseFactory.unauthorized("El médico no está asignado a esta cita");

        // Solo se puede derivar a hospitalización desde una cita activa
        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.CANCELED)
            return ResponseFactory.badRequest("La cita ya está finalizada y no se puede derivar");

        Optional<String> dateError = AppointmentValidator.validateDate(date);
        if (dateError.isPresent()) return ResponseFactory.badRequest(dateError.get());

        Optional<String> roomError = HospitalizationValidator.validateRoomType(roomType);
        if (roomError.isPresent()) return ResponseFactory.badRequest(roomError.get());

        LocalDate admissionDate = LocalDate.parse(date);
        RoomType room = RoomType.valueOf(roomType.toUpperCase());

        appointment.setStatus(AppointmentStatus.COMPLETED);
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        String id = hospitalizationRepository.nextIdForPatient(patient.getId());
        Hospitalization hosp = new Hospitalization(id, patient, doctor, admissionDate, reason, room, observations, HospitalizationStatus.ONGOING);
        hospitalizationRepository.save(hosp);
        patient.addHospitalization(hosp);
        doctor.addHospitalization(hosp);
        return ResponseFactory.ok("Hospitalización creada desde cita", id);
    }
}
