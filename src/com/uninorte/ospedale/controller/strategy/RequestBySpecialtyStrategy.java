package com.uninorte.ospedale.controller.strategy;

import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.enums.Specialty;
import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Estrategia: busca cualquier médico disponible con la especialidad pedida.
 */
public class RequestBySpecialtyStrategy implements AppointmentRequestStrategy {

    @Override
    public DoctorResolveResult resolve(String specialtyStr, LocalDateTime slot,
                                       IAppointmentRepository apptRepo,
                                       IDoctorRepository doctorRepo) {
        Specialty specialty;
        try {
            specialty = Specialty.valueOf(specialtyStr.toUpperCase().replace(" & ", "_"));
        } catch (IllegalArgumentException e) {
            return DoctorResolveResult.failure(ResponseFactory.badRequest("Especialidad inválida"));
        }

        List<Doctor> doctors = doctorRepo.findBySpecialty(specialty);
        for (Doctor d : doctors) {
            if (apptRepo.doctorIsAvailableAt(d.getId(), slot)) {
                return DoctorResolveResult.success(d, specialty);
            }
        }

        return DoctorResolveResult.failure(
            ResponseFactory.conflict("No hay médicos disponibles para esa especialidad a esa hora")
        );
    }
}
