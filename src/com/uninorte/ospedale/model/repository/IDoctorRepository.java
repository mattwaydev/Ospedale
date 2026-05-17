/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.uninorte.ospedale.model.repository;

import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.enums.Specialty;

import java.util.List;
/**
 *
 * @author Samuel Ramirez
 */
public interface IDoctorRepository extends IUserRepository{
    List<Doctor> findAllDoctors();
    List<Doctor> findBySpecialty(Specialty s);
}
