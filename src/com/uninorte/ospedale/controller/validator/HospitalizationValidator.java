/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller.validator;
import com.uninorte.ospedale.model.enums.RoomType;
import java.util.Optional;

/**
 *
 * @author Matt
 */
public final class HospitalizationValidator {

    private HospitalizationValidator() {}

    public static Optional<String> validateRoomType(String roomType) {
        if (roomType == null || roomType.isBlank())
            return Optional.of("El tipo de habitación no puede estar vacío");
        try {
            RoomType.valueOf(roomType.toUpperCase());
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.of("Tipo de habitación inválido");
        }
    }
}