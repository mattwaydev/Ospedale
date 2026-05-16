/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.uninorte.ospedale.model.dto;

/**
 *
 * @author Samuel Ramirez
 */
public record PrescriptionDTO(
        String appointmentId,
        String medicationName,
        String dose,
        String administrationRoute,
        String treatmentDuration,
        String additionalInstructions,
        String frequency
) {}
