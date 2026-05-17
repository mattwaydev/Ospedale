/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.uninorte.ospedale.model.dto;

/**
 *
 * @author Samuel Ramirez
 */
public record AppointmentRequestDTO(
    long patientId,
    String mode,
    Long doctorId,
    String specialty,
    String date,
    String time,
    String type,
    String reason         
) {}
