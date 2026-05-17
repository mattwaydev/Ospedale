/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.uninorte.ospedale.model.repository;

import com.uninorte.ospedale.model.entity.Hospitalization;
import com.uninorte.ospedale.model.enums.HospitalizationStatus;

import java.util.List;
import java.util.Optional;
/**
 *
 * @author Samuel Ramirez
 */
public interface IHospitalizationRepository {
    Optional<Hospitalization> findById(String id);
    List<Hospitalization> findByStatus(HospitalizationStatus s);
    List<Hospitalization> findByPatientId(long pid);
    
    String nextIdForPatient(long pid);
    
    void save(Hospitalization h);
    
    void save(Hospitalization h, HospitalizationStatus initialStatus);
}
