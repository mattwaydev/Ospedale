/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.uninorte.ospedale.model.dto;

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
) {}
