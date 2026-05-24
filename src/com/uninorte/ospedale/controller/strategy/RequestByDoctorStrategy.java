package com.uninorte.ospedale.controller.strategy;

import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.User;
import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Estrategia: busca al médico por su ID y verifica disponibilidad en el slot.
 */
public class RequestByDoctorStrategy implements AppointmentRequestStrategy {

    @Override
    public DoctorResolveResult resolve(String doctorIdStr, LocalDateTime slot,
                                       IAppointmentRepository apptRepo,
                                       IDoctorRepository doctorRepo) {
        long doctorId;
        try {
            doctorId = Long.parseLong(doctorIdStr);
        } catch (NumberFormatException e) {
            return DoctorResolveResult.failure(ResponseFactory.badRequest("ID de médico inválido"));
        }

        Optional<User> found = doctorRepo.findById(doctorId);
        if (found.isEmpty()) {
            return DoctorResolveResult.failure(ResponseFactory.notFound("Médico no encontrado"));
        }
        Doctor doctor = (Doctor) found.get();

        if (!apptRepo.doctorIsAvailableAt(doctorId, slot)) {
            return DoctorResolveResult.failure(ResponseFactory.conflict("El médico no está disponible a esa hora"));
        }

        return DoctorResolveResult.success(doctor, doctor.getSpecialty());
    }
}
