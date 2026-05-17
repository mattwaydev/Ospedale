/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.uninorte.ospedale.model.repository;

import java.util.List;
import com.uninorte.ospedale.model.entity.Patient;
/**
 *
 * @author Samuel Ramirez
 */
public interface IPatientRepository extends IUserRepository{
    List<Patient> findAllPatients();
    
}
