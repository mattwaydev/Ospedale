/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller.validator;
import java.util.Optional;

/**
 *
 * @author Matt
 */
public final class AppointmentValidator {

    private AppointmentValidator() {}

    public static Optional<String> validateDate(String date) {
        if (date == null || date.isBlank())
            return Optional.of("La fecha no puede estar vacía");
        if (!date.matches("^\\d{4}-\\d{2}-\\d{2}$"))
            return Optional.of("Formato de fecha inválido, use AAAA-MM-DD");
        return Optional.empty();
    }

    public static Optional<String> validateTime(String time) {
        if (time == null || time.isBlank())
            return Optional.of("La hora no puede estar vacía");
        if (!time.matches("^([01]\\d|2[0-3]):(00|15|30|45)$"))
            return Optional.of("Hora inválida, use HH:mm con minutos 00, 15, 30 o 45");
        return Optional.empty();
    }
}