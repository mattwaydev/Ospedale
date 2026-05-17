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
public final class UserValidator {
    
    private UserValidator() {}
    
    public static Optional<String> validateUsername(String username) {
        if (username == null || username.trim().isEmpty())
            return Optional.of("El nombre de usuario no puede estar vacío");
        return Optional.empty();
    }
    
    public static Optional<String> validatePassword(String password) {
        if (password == null || password.trim().isEmpty())
            return Optional.of("La contraseña no puede estar vacía");
        return Optional.empty();
    }
    
    public static Optional<String> validatePasswordMatch(String password, String confirm) {
        if (!password.equals(confirm))
            return Optional.of("Las contraseñas no coinciden");
        return Optional.empty();
    }
    
    public static Optional<String> validateId(String id) {
        if (id == null || !id.matches("\\d{12}"))
            return Optional.of("El ID debe tener exactamente 12 dígitos");
        return Optional.empty();
    }
    
    public static Optional<String> validatePhone(String phone) {
        if (phone == null || !phone.matches("\\d{10}"))
            return Optional.of("El teléfono debe tener exactamente 10 dígitos");
        return Optional.empty();
    }
    
    public static Optional<String> validateEmail(String email) {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.com$"))
            return Optional.of("El email debe seguir el formato XXXXX@XXXXX.com");
        return Optional.empty();
    }
    
    public static Optional<String> validateBirthdate(String birthdate) {
        if (birthdate == null || !birthdate.matches("\\d{4}-\\d{2}-\\d{2}"))
            return Optional.of("La fecha debe seguir el formato YYYY-MM-DD");
        return Optional.empty();
    }
    
    public static Optional<String> validateLicenceNumber(String licence) {
        if (licence == null || !licence.matches("L-\\d{10} MTL"))
            return Optional.of("La licencia debe seguir el formato L-XXXXXXXXXX MTL");
        return Optional.empty();
    }
    
    public static Optional<String> validateAssignedOffice(String office) {
        if (office == null || !office.matches("O-\\d{3}"))
            return Optional.of("La oficina debe seguir el formato O-XXX");
        return Optional.empty();
    }
}