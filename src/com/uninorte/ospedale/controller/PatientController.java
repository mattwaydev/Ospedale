/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.controller.validator.UserValidator;
import com.uninorte.ospedale.model.dto.PatientFormDTO;
import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.entity.User;
import com.uninorte.ospedale.model.repository.IPatientRepository;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
/**
 *
 * @author Matt
 */
public class PatientController {

    private final IPatientRepository patientRepository;

    public PatientController(IPatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Response<PatientFormDTO> getProfile(long id) {
        Optional<User> found = patientRepository.findById(id);
        if (found.isEmpty())
            return ResponseFactory.notFound("Paciente no encontrado");
        Patient patient = (Patient) found.get();
        PatientFormDTO dto = new PatientFormDTO(
                String.valueOf(patient.getId()),
                patient.getUsername(),
                patient.getFirstname(),
                patient.getLastname(),
                null,
                null,
                patient.getEmail(),
                patient.getBirthdate().toString(),
                patient.isGender() ? "Male" : "Female",
                String.valueOf(patient.getPhone()),
                patient.getAddress()
        );
        return ResponseFactory.ok("Perfil cargado", dto);
    }

    // Overload que recibe DTO — usado por las vistas nuevas
    public Response<?> register(PatientFormDTO dto) {
        Optional<String> idError = UserValidator.validateId(dto.id());
        if (idError.isPresent())
            return ResponseFactory.badRequest(idError.get());

        boolean gender;
        if (dto.gender().equals("Male")) {
            gender = true;
        } else if (dto.gender().equals("Female")) {
            gender = false;
        } else {
            return ResponseFactory.badRequest("Seleccione un género");
        }

        long id = Long.parseLong(dto.id());
        return doRegister(id, dto.username(), dto.firstname(), dto.lastname(),
                dto.password(), dto.passwordConfirm(), dto.email(),
                dto.birthdate(), gender, dto.phone(), dto.address());
    }

    // Overload que recibe DTO — usado por las vistas nuevas
    public Response<?> update(long id, PatientFormDTO dto) {
        boolean gender;
        if (dto.gender().equals("Male")) {
            gender = true;
        } else if (dto.gender().equals("Female")) {
            gender = false;
        } else {
            return ResponseFactory.badRequest("Seleccione un género");
        }

        return doUpdate(id, dto.username(), dto.firstname(), dto.lastname(),
                dto.password(), dto.passwordConfirm(), dto.email(),
                dto.birthdate(), gender, dto.phone(), dto.address());
    }

    // Método privado con la lógica real de registro
    private Response<?> doRegister(long id, String username, String firstname, String lastname,
            String password, String confirmPassword, String email,
            String birthdate, boolean gender, String phone, String address) {

        Optional<String> usernameError = UserValidator.validateUsername(username);
        if (usernameError.isPresent()) return ResponseFactory.badRequest(usernameError.get());

        Optional<String> passwordError = UserValidator.validatePasswordMatch(password, confirmPassword);
        if (passwordError.isPresent()) return ResponseFactory.badRequest(passwordError.get());

        Optional<String> phoneError = UserValidator.validatePhone(phone);
        if (phoneError.isPresent()) return ResponseFactory.badRequest(phoneError.get());

        Optional<String> emailError = UserValidator.validateEmail(email);
        if (emailError.isPresent()) return ResponseFactory.badRequest(emailError.get());

        if (patientRepository.existsById(id))
            return ResponseFactory.conflict("El ID ya existe");
        if (patientRepository.existsByUsername(username))
            return ResponseFactory.conflict("El nombre de usuario ya existe");

        LocalDate birth;
        try {
            birth = LocalDate.parse(birthdate);
        } catch (DateTimeParseException e) {
            return ResponseFactory.badRequest("Fecha inválida, use AAAA-MM-DD");
        }

        Patient patient = new Patient(id, username, firstname, lastname, password,
                email, birth, gender, Long.parseLong(phone), address);
        patientRepository.save(patient);
        return ResponseFactory.ok("Paciente registrado exitosamente", null);
    }

    // Método privado con la lógica real de actualización
    private Response<?> doUpdate(long id, String username, String firstname, String lastname,
            String password, String confirmPassword, String email,
            String birthdate, boolean gender, String phone, String address) {

        Optional<User> found = patientRepository.findById(id);
        if (found.isEmpty())
            return ResponseFactory.notFound("Paciente no encontrado");

        Optional<String> usernameError = UserValidator.validateUsername(username);
        if (usernameError.isPresent()) return ResponseFactory.badRequest(usernameError.get());

        Optional<String> passwordError = UserValidator.validatePasswordMatch(password, confirmPassword);
        if (passwordError.isPresent()) return ResponseFactory.badRequest(passwordError.get());

        Optional<String> phoneError = UserValidator.validatePhone(phone);
        if (phoneError.isPresent()) return ResponseFactory.badRequest(phoneError.get());

        Optional<String> emailError = UserValidator.validateEmail(email);
        if (emailError.isPresent()) return ResponseFactory.badRequest(emailError.get());

        Patient patient = (Patient) found.get();
        // unicidad de username: solo chequea si el username cambió
        if (!username.equals(patient.getUsername())
                && patientRepository.existsByUsername(username))
            return ResponseFactory.conflict("El nombre de usuario ya existe");

        LocalDate birth;
        try {
            birth = LocalDate.parse(birthdate);
        } catch (DateTimeParseException e) {
            return ResponseFactory.badRequest("Fecha inválida, use AAAA-MM-DD");
        }

        patient.setUsername(username);
        patient.setFirstname(firstname);
        patient.setLastname(lastname);
        patient.setPassword(password);
        patient.setEmail(email);
        patient.setBirthdate(birth);
        patient.setGender(gender);
        patient.setPhone(Long.parseLong(phone));
        patient.setAddress(address);
        patientRepository.save(patient);
        return ResponseFactory.ok("Paciente actualizado exitosamente", null);
    }
}



