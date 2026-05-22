/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.model.storage;

import com.uninorte.ospedale.model.entity.Hospitalization;
import com.uninorte.ospedale.model.enums.HospitalizationStatus;
import com.uninorte.ospedale.model.repository.IHospitalizationRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Samuel Ramirez
 */
public class InMemoryHospitalizationRepository implements IHospitalizationRepository {

    private final Map<String, Hospitalization> byId = new HashMap<>();
    private final Map<Long, Integer> counterByPatient = new HashMap<>();

    @Override
    public Optional<Hospitalization> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<Hospitalization> findByStatus(HospitalizationStatus s) {
        List<Hospitalization> out = new ArrayList<>();
        for (Hospitalization h : byId.values()) {
            if (h.getStatus() == s) {
                out.add(h);
            }
        }
        return out;
    }

    @Override
    public List<Hospitalization> findByPatientId(long pid) {
        List<Hospitalization> out = new ArrayList<>();
        for (Hospitalization h : byId.values()) {
            if (h.getPatient().getId() == pid) {
                out.add(h);
            }
        }
        return out;
    }

    @Override
    public String nextIdForPatient(long pid) {
        int next = counterByPatient.getOrDefault(pid, 0);
        counterByPatient.put(pid, next + 1);
        return String.format("H-%d-%04d", pid, next);
    }

    @Override
    public void save(Hospitalization h) {
        byId.put(h.getId(), h);
    }

    @Override
    public void save(Hospitalization h, HospitalizationStatus initialStatus) {
        h.setStatus(initialStatus);
        byId.put(h.getId(), h);
    }
}
