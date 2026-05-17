/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.model.storage;

import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.User;
import com.uninorte.ospedale.model.enums.Specialty;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import com.uninorte.ospedale.model.repository.IUserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Samuel Ramirez
 */
public class InMemoryDoctorRepository implements IDoctorRepository {

    private final IUserRepository base;

    public InMemoryDoctorRepository(IUserRepository base) {
        this.base = base;
    }

    @Override
    public List<Doctor> findAllDoctors() {
        List<Doctor> out = new ArrayList<>();
        for (User u : base.findAll()) {
            if (u instanceof Doctor) {
                out.add((Doctor) u);
            }
        }
        return out;
    }

    @Override
    public List<Doctor> findBySpecialty(Specialty s) {
        List<Doctor> out = new ArrayList<>();
        for (User u : base.findAll()) {
            if (u instanceof Doctor) {
                Doctor d = (Doctor) u;
                if (d.getSpecialty() == s) {
                    out.add(d);
                }
            }
        }
        return out;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return base.findByUsername(username);
    }

    @Override
    public Optional<User> findById(long id) {
        return base.findById(id);
    }

    @Override
    public boolean existsById(long id) {
        return base.existsById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return base.existsByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return base.findAll();
    }

    @Override
    public void save(User u) {
        base.save(u);
    }
}
