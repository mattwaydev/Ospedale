/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.model.loader;

import com.uninorte.ospedale.model.entity.Appointment;
import com.uninorte.ospedale.model.entity.Hospitalization;

/**
 *
 * @author Samuel Ramirez
 */
public class RelationshipWirer {

    public void wire(Appointment a) {
        a.getPatient().addAppointment(a);
        a.getDoctor().addAppointment(a);
    }

    public void wire(Hospitalization h) {
        h.getPatient().setHospitalization(h);
        h.getDoctor().addHospitalization(h);
    }
}
