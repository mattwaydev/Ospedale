/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.uninorte.ospedale.model.dto;

import com.uninorte.ospedale.model.entity.Administrator;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.entity.User;
import com.uninorte.ospedale.model.enums.Role;

/**
 *
 * @author Samuel Ramirez
 */
public record UserSessionDTO(
        long id,
        String username,
        String firstname,
        String lastname,
        Role role
        ) {

    public static Role roleOf(User u) {
        if (u instanceof Administrator) {
            return Role.ADMIN;
        }
        if (u instanceof Doctor) {
            return Role.DOCTOR;
        }
        if (u instanceof Patient) {
            return Role.PATIENT;
        }
        throw new IllegalArgumentException("Unknown user type: " + u.getClass().getName());
    }
}
