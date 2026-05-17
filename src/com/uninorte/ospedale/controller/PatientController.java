/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.model.repository.IPatientRepository;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.entity.User;

/**
 *
 * @author Matt
 */
public class PatientController {
    
    private final IPatientRepository patientRepository;

    public PatientController(IPatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Response<Object> register(long id, String username, String firstname, String lastname,
            String password, String confirmPassword, String email,
            String birthdate, boolean gender, String phone, String address) {

        if (id <= 0 || String.valueOf(id).length() != 12)
            return ResponseFactory.badRequest("ID must be 12 digits and greater than 0");
        if (patientRepository.existsById(id))
            return ResponseFactory.conflict("ID already exists");
        if (patientRepository.existsByUsername(username))
            return ResponseFactory.conflict("Username already exists");
        if (!password.equals(confirmPassword))
            return ResponseFactory.badRequest("Passwords do not match");
        if (String.valueOf(phone).length() != 10)
            return ResponseFactory.badRequest("Phone must have exactly 10 digits");
        if (!email.matches("^[\\w.]+@[\\w.]+\\.com$"))
            return ResponseFactory.badRequest("Invalid email format");

        LocalDate birth;
        try {
            birth = LocalDate.parse(birthdate);
        } catch (DateTimeParseException e) {
            return ResponseFactory.badRequest("Invalid birthdate format, use YYYY-MM-DD");
        }

        Patient patient = new Patient(id, username, firstname, lastname, password,
                email, birth, gender, Long.parseLong(phone), address);
        patientRepository.save(patient);
        return ResponseFactory.ok("Patient registered successfully", null);
    }

    public Response<Object> update(long id, String username, String firstname, String lastname,
            String password, String confirmPassword, String email,
            String birthdate, boolean gender, String phone, String address) {

        Optional<User> found = patientRepository.findById(id);
        if (found.isEmpty())
            return ResponseFactory.notFound("Patient not found");
        if (!password.equals(confirmPassword))
            return ResponseFactory.badRequest("Passwords do not match");
        if (String.valueOf(phone).length() != 10)
            return ResponseFactory.badRequest("Phone must have exactly 10 digits");
        if (!email.matches("^[\\w.]+@[\\w.]+\\.com$"))
            return ResponseFactory.badRequest("Invalid email format");

        LocalDate birth;
        try {
            birth = LocalDate.parse(birthdate);
        } catch (DateTimeParseException e) {
            return ResponseFactory.badRequest("Invalid birthdate format, use YYYY-MM-DD");
        }

        Patient patient = (Patient) found.get();
        patient.setUsername(username);
        patient.setFirstname(firstname);
        patient.setLastname(lastname);
        patient.setPassword(password);
        patient.setEmail(email);
        patient.setBirthdate(birth);
        patient.setGender(gender);
        patient.setPhone(Long.parseLong(phone));
        patient.setAddress(address);
        return ResponseFactory.ok("Patient updated successfully", null);
    }
}



