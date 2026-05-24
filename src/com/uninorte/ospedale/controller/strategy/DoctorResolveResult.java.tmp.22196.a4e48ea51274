package com.uninorte.ospedale.controller.strategy;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.enums.Specialty;

/**
 * Resultado de la resolución de médico por una AppointmentRequestStrategy.
 * Encapsula éxito (doctor + specialty) o fracaso (Response de error).
 */
public final class DoctorResolveResult {

    private final Doctor doctor;
    private final Specialty specialty;
    private final Response<Object> error;

    private DoctorResolveResult(Doctor doctor, Specialty specialty, Response<Object> error) {
        this.doctor = doctor;
        this.specialty = specialty;
        this.error = error;
    }

    public static DoctorResolveResult success(Doctor doctor, Specialty specialty) {
        return new DoctorResolveResult(doctor, specialty, null);
    }

    public static DoctorResolveResult failure(Response<Object> error) {
        return new DoctorResolveResult(null, null, error);
    }

    public boolean isError() { return error != null; }
    public Doctor getDoctor() { return doctor; }
    public Specialty getSpecialty() { return specialty; }
    public Response<Object> getError() { return error; }
}
