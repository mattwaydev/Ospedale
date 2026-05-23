/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.controller.validator.UserValidator;
import com.uninorte.ospedale.model.dto.DoctorFormDTO;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.User;
import com.uninorte.ospedale.model.enums.Specialty;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import java.util.Optional;


/**
 *
 * @author Matt
 */
public class DoctorController {

    private final IDoctorRepository doctorRepository;

    public DoctorController(IDoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Response<DoctorFormDTO> getProfile(long id) {
        Optional<User> found = doctorRepository.findById(id);
        if (found.isEmpty())
            return ResponseFactory.notFound("Doctor not found");
        Doctor doctor = (Doctor) found.get();
        DoctorFormDTO dto = new DoctorFormDTO(
                String.valueOf(doctor.getId()),
                doctor.getUsername(),
                doctor.getFirstname(),
                doctor.getLastname(),
                null,
                null,
                doctor.getSpecialty().name(),
                doctor.getLicenceNumber(),
                doctor.getAssignedOffice()
        );
        return ResponseFactory.ok("Profile loaded", dto);
    }

    // Overload que recibe DTO — usado por las vistas nuevas
    public Response<?> register(DoctorFormDTO dto) {
        Optional<String> idError = UserValidator.validateId(dto.id());
        if (idError.isPresent())
            return ResponseFactory.badRequest(idError.get());

        long id = Long.parseLong(dto.id());
        return doRegister(id, dto.username(), dto.firstname(), dto.lastname(),
                dto.password(), dto.passwordConfirm(), dto.licenceNumber(),
                dto.assignedOffice(), dto.specialty());
    }

    // Overload que recibe DTO — usado por las vistas nuevas
    public Response<?> update(long id, DoctorFormDTO dto) {
        return doUpdate(id, dto.username(), dto.firstname(), dto.lastname(),
                dto.password(), dto.passwordConfirm(), dto.licenceNumber(),
                dto.assignedOffice(), dto.specialty());
    }

    // Método privado con la lógica real de registro
    private Response<?> doRegister(long id, String username, String firstname, String lastname,
            String password, String confirmPassword, String licenceNumber,
            String assignedOffice, String specialty) {

        Optional<String> usernameError = UserValidator.validateUsername(username);
        if (usernameError.isPresent()) return ResponseFactory.badRequest(usernameError.get());

        Optional<String> passwordError = UserValidator.validatePasswordMatch(password, confirmPassword);
        if (passwordError.isPresent()) return ResponseFactory.badRequest(passwordError.get());

        Optional<String> licenceError = UserValidator.validateLicenceNumber(licenceNumber);
        if (licenceError.isPresent()) return ResponseFactory.badRequest(licenceError.get());

        Optional<String> officeError = UserValidator.validateAssignedOffice(assignedOffice);
        if (officeError.isPresent()) return ResponseFactory.badRequest(officeError.get());

        if (doctorRepository.existsById(id))
            return ResponseFactory.conflict("El ID ya existe");
        if (doctorRepository.existsByUsername(username))
            return ResponseFactory.conflict("El nombre de usuario ya existe");

        Specialty spec;
        try {
            spec = Specialty.valueOf(specialty.toUpperCase().replace(" & ", "_"));
        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest("Especialidad inválida");
        }

        Doctor doctor = new Doctor(id, username, firstname, lastname, password, spec, licenceNumber, assignedOffice);
        doctorRepository.save(doctor);
        return ResponseFactory.ok("Doctor registrado exitosamente", null);
    }

    // Método privado con la lógica real de actualización
    private Response<?> doUpdate(long id, String username, String firstname, String lastname,
            String password, String confirmPassword, String licenceNumber,
            String assignedOffice, String specialty) {

        Optional<User> found = doctorRepository.findById(id);
        if (found.isEmpty())
            return ResponseFactory.notFound("Doctor no encontrado");

        Optional<String> passwordError = UserValidator.validatePasswordMatch(password, confirmPassword);
        if (passwordError.isPresent()) return ResponseFactory.badRequest(passwordError.get());

        Optional<String> licenceError = UserValidator.validateLicenceNumber(licenceNumber);
        if (licenceError.isPresent()) return ResponseFactory.badRequest(licenceError.get());

        Optional<String> officeError = UserValidator.validateAssignedOffice(assignedOffice);
        if (officeError.isPresent()) return ResponseFactory.badRequest(officeError.get());

        Specialty spec;
        try {
            spec = Specialty.valueOf(specialty.toUpperCase().replace(" & ", "_"));
        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest("Especialidad inválida");
        }

        Doctor doctor = (Doctor) found.get();
        doctor.setUsername(username);
        doctor.setFirstname(firstname);
        doctor.setLastname(lastname);
        doctor.setPassword(password);
        doctor.setSpecialty(spec);
        doctor.setLicenceNumber(licenceNumber);
        doctor.setAssignedOffice(assignedOffice);
        return ResponseFactory.ok("Doctor actualizado exitosamente", null);
    }
}
    