/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;
import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import com.uninorte.ospedale.model.repository.IPatientRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import com.uninorte.ospedale.model.entity.Appointment;
import com.uninorte.ospedale.model.enums.AppointmentStatus;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.entity.Prescription;
import com.uninorte.ospedale.model.enums.Specialty;
import com.uninorte.ospedale.model.dto.AppointmentRequestDTO;
import com.uninorte.ospedale.model.entity.User;
/**
 *
 * @author Matt
 */
public class AppointmentController {
    
    private final IAppointmentRepository appointmentRepository;
    private final IPatientRepository patientRepository;
    private final IDoctorRepository doctorRepository;

    public AppointmentController(IAppointmentRepository appointmentRepository,
            IPatientRepository patientRepository,
            IDoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public Response<Object> request(AppointmentRequestDTO dto) {
        boolean type = "IN_PERSON".equalsIgnoreCase(dto.type());
        boolean byDoctor = "DOCTOR".equalsIgnoreCase(dto.mode());
        String specialtyOrDoctorId = byDoctor
                ? String.valueOf(dto.doctorId())
                : (dto.specialty() != null ? dto.specialty() : "");
        return doRequest(dto.patientId(), dto.date(), dto.time(), dto.reason(), type, specialtyOrDoctorId, byDoctor);
    }

    public Response<Object> request(long patientId, String date, String time,
            String reason, boolean type, String specialtyOrDoctorId, boolean byDoctor) {
        return doRequest(patientId, date, time, reason, type, specialtyOrDoctorId, byDoctor);
    }

    private Response<Object> doRequest(long patientId, String date, String time,
            String reason, boolean type, String specialtyOrDoctorId, boolean byDoctor) {

        Optional<User> patientFound = patientRepository.findById(patientId);
        if (patientFound.isEmpty())
            return ResponseFactory.notFound("Patient not found");

        LocalDate appointmentDate;
        try {
            appointmentDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return ResponseFactory.badRequest("Invalid date format, use YYYY-MM-DD");
        }

        if (!time.matches("^([01]\\d|2[0-3]):(00|15|30|45)$"))
            return ResponseFactory.badRequest("Invalid time, use HH:mm with minutes 00, 15, 30 or 45");

        LocalTime appointmentTime = LocalTime.parse(time);
        LocalDateTime slot = LocalDateTime.of(appointmentDate, appointmentTime);

        Patient patient = (Patient) patientFound.get();
        Doctor doctor;
        Specialty specialty;

        if (byDoctor) {
            long doctorId = Long.parseLong(specialtyOrDoctorId);
            Optional<User> doctorFound = doctorRepository.findById(doctorId);
            if (doctorFound.isEmpty())
                return ResponseFactory.notFound("Doctor not found");
            doctor = (Doctor) doctorFound.get();
            specialty = doctor.getSpecialty();
            if (!appointmentRepository.doctorIsAvailableAt(doctorId, slot))
                return ResponseFactory.conflict("Doctor is not available at that time");
        } else {
            try {
                specialty = Specialty.valueOf(specialtyOrDoctorId.toUpperCase().replace(" & ", "_"));
            } catch (IllegalArgumentException e) {
                return ResponseFactory.badRequest("Invalid specialty");
            }
            java.util.List<Doctor> doctors = doctorRepository.findBySpecialty(specialty);
            doctor = null;
            for (Doctor d : doctors) {
                if (appointmentRepository.doctorIsAvailableAt(d.getId(), slot)) {
                    doctor = d;
                    break;
                }
            }
            if (doctor == null)
                return ResponseFactory.conflict("No doctor available for that specialty at that time");
        }

        String id = appointmentRepository.nextIdForPatient(patientId);
        Appointment appointment = new Appointment(id, patient, doctor, specialty, slot, reason, type);
        appointmentRepository.save(appointment);
        return ResponseFactory.ok("Appointment requested successfully", id);
    }

    public Response<Object> accept(String appointmentId, long doctorId) {
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Appointment not found");
        Appointment appointment = found.get();
        if (appointment.getStatus() != AppointmentStatus.REQUESTED)
            return ResponseFactory.badRequest("Appointment is not in REQUESTED status");
        if (appointment.getDoctor().getId() != doctorId)
            return ResponseFactory.unauthorized("Doctor is not assigned to this appointment");
        appointment.setStatus(AppointmentStatus.PENDING);
        return ResponseFactory.ok("Appointment accepted", null);
    }

    public Response<Object> complete(String appointmentId, long doctorId) {
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Appointment not found");
        Appointment appointment = found.get();
        if (appointment.getStatus() != AppointmentStatus.PENDING)
            return ResponseFactory.badRequest("Appointment is not in PENDING status");
        if (appointment.getDoctor().getId() != doctorId)
            return ResponseFactory.unauthorized("Doctor is not assigned to this appointment");
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return ResponseFactory.ok("Appointment completed", null);
    }

    public Response<Object> cancel(String appointmentId, long patientId) {
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Appointment not found");
        Appointment appointment = found.get();
        if (appointment.getStatus() == AppointmentStatus.COMPLETED)
            return ResponseFactory.badRequest("Cannot cancel a completed appointment");
        if (appointment.getPatient().getId() != patientId)
            return ResponseFactory.unauthorized("Patient is not assigned to this appointment");
        appointment.setStatus(AppointmentStatus.CANCELED);
        return ResponseFactory.ok("Appointment cancelled", null);
    }

    public Response<Object> reschedule(String appointmentId, long doctorId, String newTime, String reason) {
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Appointment not found");
        Appointment appointment = found.get();
        if (appointment.getDoctor().getId() != doctorId)
            return ResponseFactory.unauthorized("Doctor is not assigned to this appointment");
        if (!newTime.matches("^([01]\\d|2[0-3]):(00|15|30|45)$"))
            return ResponseFactory.badRequest("Invalid time, use HH:mm with minutes 00, 15, 30 or 45");
        LocalTime time = LocalTime.parse(newTime);
        LocalDateTime newSlot = LocalDateTime.of(appointment.getDatetime().toLocalDate(), time);
        appointment.setDatetime(newSlot);
        appointment.setReason(appointment.getReason() + " | Rescheduled: " + reason);
        return ResponseFactory.ok("Appointment rescheduled", null);
    }

    public Response<Object> prescribe(String appointmentId, long doctorId,
            String medicationName, double dose, String administrationRoute, int treatmentDuration, String additionalInstructions, int frecuency ) {
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Appointment not found");
        Appointment appointment = found.get();
        if (appointment.getDoctor().getId() != doctorId)
            return ResponseFactory.unauthorized("Doctor is not assigned to this appointment");
        if (appointment.getStatus() != AppointmentStatus.PENDING)
            return ResponseFactory.badRequest("Can only prescribe during an accepted appointment");
        appointment.addPrescription(new Prescription(appointment, medicationName, dose, administrationRoute, treatmentDuration, additionalInstructions, frecuency ));
        return ResponseFactory.ok("Prescription added", null);
    }
}





    