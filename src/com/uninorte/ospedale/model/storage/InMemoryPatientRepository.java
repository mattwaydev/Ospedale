/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.model.storage;

import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.entity.User;
import com.uninorte.ospedale.model.repository.IPatientRepository;
import com.uninorte.ospedale.model.repository.IUserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Samuel Ramirez
 */
public class InMemoryPatientRepository implements IPatientRepository {

    private final IUserRepository base;

    public InMemoryPatientRepository(IUserRepository base) {
        this.base = base;
    }

    @Override
    public List<Patient> findAllPatients() {
        List<Patient> out = new ArrayList<>();
        for (User u : base.findAll()) {
            if (u instanceof Patient) {
                out.add((Patient) u);
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
