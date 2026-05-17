/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.model.loader;

import com.uninorte.ospedale.model.enums.Specialty;

/**
 *
 * @author Samuel Ramirez
 */
public class SpecialtyNormalizer {

    public static Specialty normalize(String raw) {
        String v = raw.trim().toUpperCase();
        if (v.equals("ORTHOPEDICS")) {
            return Specialty.TRAUMATOLOGY_ORTHOPEDICS;
        }
        if (v.equals("GYNECOLOGY")) {
            return Specialty.GYNECOLOGY_OBSTETRICS;
        }
        return Specialty.valueOf(v);
    }
}
