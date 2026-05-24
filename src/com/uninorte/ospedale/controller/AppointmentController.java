/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;
import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.controller.validator.AppointmentValidator;
import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import com.uninorte.ospedale.model.repository.IPatientRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import com.uninorte.ospedale.model.entity.Appointment;
import com.uninorte.ospedale.model.enums.AppointmentStatus;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.entity.Prescription;
import com.uninorte.ospedale.model.enums.Specialty;
import com.uninorte.ospedale.model.dto.AppointmentRequestDTO;
import com.uninorte.ospedale.model.dto.CompleteAppointmentDTO;
import com.uninorte.ospedale.model.dto.PrescriptionDTO;
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
            return ResponseFactory.notFound("Paciente no encontrado");

        Optional<String> dateError = AppointmentValidator.validateDate(date);
        if (dateError.isPresent()) return ResponseFactory.badRequest(dateError.get());

        Optional<String> timeError = AppointmentValidator.validateTime(time);
        if (timeError.isPresent()) return ResponseFactory.badRequest(timeError.get());

        LocalDate appointmentDate = LocalDate.parse(date);
        LocalTime appointmentTime = LocalTime.parse(time);
        LocalDateTime slot = LocalDateTime.of(appointmentDate, appointmentTime);

        Patient patient = (Patient) patientFound.get();
        Doctor doctor;
        Specialty specialty;

        if (byDoctor) {
            long doctorId = Long.parseLong(specialtyOrDoctorId);
            Optional<User> doctorFound = doctorRepository.findById(doctorId);
            if (doctorFound.isEmpty())
                return ResponseFactory.notFound("Médico no encontrado");
            doctor = (Doctor) doctorFound.get();
            specialty = doctor.getSpecialty();
            if (!appointmentRepository.doctorIsAvailableAt(doctorId, slot))
                return ResponseFactory.conflict("El médico no está disponible a esa hora");
        } else {
            try {
                specialty = Specialty.valueOf(specialtyOrDoctorId.toUpperCase().replace(" & ", "_"));
            } catch (IllegalArgumentException e) {
                return ResponseFactory.badRequest("Especialidad inválida");
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
                return ResponseFactory.conflict("No hay médicos disponibles para esa especialidad a esa hora");
        }

        String id = appointmentRepository.nextIdForPatient(patientId);
        Appointment appointment = new Appointment(id, patient, doctor, specialty, slot, reason, type);
        appointmentRepository.save(appointment);
        patient.addAppointment(appointment);
        doctor.addAppointment(appointment);
        return ResponseFactory.ok("Cita solicitada exitosamente", id);
    }

    public Response<Object> accept(String appointmentId, long doctorId) {
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Cita no encontrada");
        Appointment appointment = found.get();
        if (appointment.getStatus() != AppointmentStatus.REQUESTED)
            return ResponseFactory.badRequest("La cita no está en estado REQUESTED");
        if (appointment.getDoctor().getId() != doctorId)
            return ResponseFactory.unauthorized("El médico no está asignado a esta cita");
        appointment.setStatus(AppointmentStatus.PENDING);
        return ResponseFactory.ok("Cita aceptada", null);
    }

    public Response<Object> complete(String appointmentId, long doctorId, CompleteAppointmentDTO dto) {
        return doComplete(appointmentId, doctorId, dto.diagnosis(), dto.observations(),
                dto.recommendedTreatment(), dto.followUp());
    }

    public Response<Object> complete(String appointmentId, long doctorId) {
        return doComplete(appointmentId, doctorId, null, null, null, null);
    }

    private Response<Object> doComplete(String appointmentId, long doctorId,
            String diagnosis, String observations, String treatment, String followUp) {
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Cita no encontrada");
        Appointment appointment = found.get();
        if (appointment.getStatus() != AppointmentStatus.PENDING)
            return ResponseFactory.badRequest("La cita no está en estado PENDING");
        if (appointment.getDoctor().getId() != doctorId)
            return ResponseFactory.unauthorized("El médico no está asignado a esta cita");
        appointment.setStatus(AppointmentStatus.COMPLETED);
        if (diagnosis != null) appointment.setDiagnosis(diagnosis);
        if (observations != null) appointment.setObservations(observations);
        if (treatment != null) appointment.setRecommendedTreatment(treatment);
        if (followUp != null) appointment.setFollowUp(followUp);
        return ResponseFactory.ok("Cita completada", null);
    }

    public Response<Object> cancel(String appointmentId, long patientId) {
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Cita no encontrada");
        Appointment appointment = found.get();
        if (appointment.getStatus() == AppointmentStatus.COMPLETED)
            return ResponseFactory.badRequest("No se puede cancelar una cita completada");
        if (appointment.getPatient().getId() != patientId)
            return ResponseFactory.unauthorized("El paciente no está asignado a esta cita");
        appointment.setStatus(AppointmentStatus.CANCELED);
        return ResponseFactory.ok("Cita cancelada", null);
    }

    public Response<Object> reschedule(String appointmentId, long doctorId, String newTime, String reason) {
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Cita no encontrada");
        Appointment appointment = found.get();
        if (appointment.getDoctor().getId() != doctorId)
            return ResponseFactory.unauthorized("El médico no está asignado a esta cita");
        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.CANCELED)
            return ResponseFactory.badRequest("La cita ya está finalizada y no se puede reagendar");

        Optional<String> timeError = AppointmentValidator.validateTime(newTime);
        if (timeError.isPresent()) return ResponseFactory.badRequest(timeError.get());

        LocalTime time = LocalTime.parse(newTime);
        LocalDateTime newSlot = LocalDateTime.of(appointment.getDatetime().toLocalDate(), time);
        if (!appointmentRepository.doctorIsAvailableAt(doctorId, newSlot))
            return ResponseFactory.conflict("El médico no está disponible a esa hora");

        appointment.setDatetime(newSlot);
        appointment.setReason(appointment.getReason() + " | Reagendada: " + reason);
        return ResponseFactory.ok("Cita reagendada", null);
    }

    public Response<Object> prescribe(String appointmentId, long doctorId, PrescriptionDTO dto) {
        double dose;
        int duration, frequency;
        try {
            dose = Double.parseDouble(dto.dose());
            duration = Integer.parseInt(dto.treatmentDuration());
            frequency = Integer.parseInt(dto.frequency());
        } catch (NumberFormatException e) {
            return ResponseFactory.badRequest("Dosis, duración y frecuencia deben ser numéricos");
        }
        return prescribe(appointmentId, doctorId, dto.medicationName(), dose,
                dto.administrationRoute(), duration, dto.additionalInstructions(), frequency);
    }

    public Response<Object> prescribe(String appointmentId, long doctorId,
            String medicationName, double dose, String administrationRoute, int treatmentDuration, String additionalInstructions, int frecuency ) {
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (found.isEmpty())
            return ResponseFactory.notFound("Cita no encontrada");
        Appointment appointment = found.get();
        if (appointment.getDoctor().getId() != doctorId)
            return ResponseFactory.unauthorized("El médico no está asignado a esta cita");
        if (appointment.getStatus() != AppointmentStatus.PENDING)
            return ResponseFactory.badRequest("Solo se puede recetar durante una cita aceptada");
        appointment.addPrescription(new Prescription(appointment, medicationName, dose, administrationRoute, treatmentDuration, additionalInstructions, frecuency ));
        return ResponseFactory.ok("Receta registrada", null);
    }
}





    