package com.uninorte.ospedale.controller.strategy;

import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import java.time.LocalDateTime;

/**
 * Estrategia para resolver qué médico atiende una cita solicitada.
 * Principio O — Open/Closed: agregar RequestByUrgencyStrategy en el futuro
 * no requiere modificar AppointmentController.
 */
public interface AppointmentRequestStrategy {

    /**
     * Resuelve el médico y la especialidad para una cita.
     *
     * @param param      doctorId (como String) si la estrategia es BY_DOCTOR,
     *                   o nombre de especialidad si es BY_SPECIALTY
     * @param slot       fecha y hora exacta de la cita (slot de 15 min)
     * @param apptRepo   repositorio de citas (para verificar disponibilidad)
     * @param doctorRepo repositorio de médicos (para buscar por id o especialidad)
     * @return resultado con Doctor + Specialty en éxito, o Response de error
     */
    DoctorResolveResult resolve(String param, LocalDateTime slot,
                                IAppointmentRepository apptRepo,
                                IDoctorRepository doctorRepo);
}
