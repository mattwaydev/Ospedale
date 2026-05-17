/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import java.util.Optional;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.User;
import com.uninorte.ospedale.model.enums.Specialty;

/**
 *
 * @author Matt
 */
public class DoctorController {
    
    private final IDoctorRepository doctorRepository;

    public DoctorController(IDoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Response<Object> register(long id, String username, String firstname, String lastname,
            String password, String confirmPassword, String licenceNumber,
            String assignedOffice, String specialty) {

        if (id <= 0 || String.valueOf(id).length() != 12)
            return ResponseFactory.badRequest("ID must be 12 digits and greater than 0");
        if (doctorRepository.existsById(id))
            return ResponseFactory.conflict("ID already exists");
        if (doctorRepository.existsByUsername(username))
            return ResponseFactory.conflict("Username already exists");
        if (!password.equals(confirmPassword))
            return ResponseFactory.badRequest("Passwords do not match");
        if (!licenceNumber.matches("^L-\\d{10} MTL$"))
            return ResponseFactory.badRequest("Invalid licence number format, use L-XXXXXXXXXX MTL");
        if (!assignedOffice.matches("^O-\\d{3}$"))
            return ResponseFactory.badRequest("Invalid office format, use O-XXX");

        Specialty spec;
        try {
            spec = Specialty.valueOf(specialty.toUpperCase().replace(" & ", "_"));
        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest("Invalid specialty");
        }

        Doctor doctor = new Doctor(id, username, firstname, lastname, password, spec, licenceNumber, assignedOffice);
        doctorRepository.save(doctor);
        return ResponseFactory.ok("Doctor registered successfully", null);
    }

    public Response<Object> update(long id, String username, String firstname, String lastname,
            String password, String confirmPassword, String licenceNumber,
            String assignedOffice, String specialty) {

        Optional<User> found = doctorRepository.findById(id);
        if (found.isEmpty())
            return ResponseFactory.notFound("Doctor not found");
        if (!password.equals(confirmPassword))
            return ResponseFactory.badRequest("Passwords do not match");
        if (!licenceNumber.matches("^L-\\d{10} MTL$"))
            return ResponseFactory.badRequest("Invalid licence number format, use L-XXXXXXXXXX MTL");
        if (!assignedOffice.matches("^O-\\d{3}$"))
            return ResponseFactory.badRequest("Invalid office format, use O-XXX");

        Specialty spec;
        try {
            spec = Specialty.valueOf(specialty.toUpperCase().replace(" & ", "_"));
        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest("Invalid specialty");
        }

        Doctor doctor = (Doctor) found.get();
        doctor.setUsername(username);
        doctor.setFirstname(firstname);
        doctor.setLastname(lastname);
        doctor.setPassword(password);
        doctor.setSpecialty(spec);
        doctor.setLicenceNumber(licenceNumber);
        doctor.setAssignedOffice(assignedOffice);
        return ResponseFactory.ok("Doctor updated successfully", null);
    }
}
    