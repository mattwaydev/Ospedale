package com.uninorte.ospedale.model.dto;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

/**
 *
 * @author Samuel Ramirez
 */
public record PatientFormDTO(
    String id,
    String username,
    String firstname,
    String lastname,
    String password,
    String passwordConfirm,
    String email,
    String birthdate,
    String gender,
    String phone,
    String address
) {}
