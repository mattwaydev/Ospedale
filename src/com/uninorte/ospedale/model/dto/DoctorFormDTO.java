/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.uninorte.ospedale.model.dto;

/**
 *
 * @author Samuel Ramirez
 */
public record DoctorFormDTO(
        String id,
        String username,
        String firstname,
        String lastname,
        String password,
        String passwordConfirm,
        String specialty,
        String licenceNumber,
        String asignedOffice
) {}
