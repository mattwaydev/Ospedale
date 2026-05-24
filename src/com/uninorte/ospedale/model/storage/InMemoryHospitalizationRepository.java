/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.model.storage;

import com.uninorte.ospedale.model.entity.Hospitalization;
import com.uninorte.ospedale.model.enums.HospitalizationStatus;
import com.uninorte.ospedale.model.observer.EntityEvent;
import com.uninorte.ospedale.model.observer.EventType;
import com.uninorte.ospedale.model.observer.Observer;
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
    private final List<Observer<EntityEvent>> observers = new ArrayList<>();

    @Override
    public void subscribe(Observer<EntityEvent> observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    private void notifyObservers(EntityEvent event) {
        for (Observer<EntityEvent> obs : observers) {
            obs.onNotify(event);
        }
    }

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
        boolean isNew = !byId.containsKey(h.getId());
        byId.put(h.getId(), h);
        notifyObservers(new EntityEvent(isNew ? EventType.CREATED : EventType.UPDATED, h.getId()));
    }

    @Override
    public void save(Hospitalization h, HospitalizationStatus initialStatus) {
        h.setStatus(initialStatus);
        boolean isNew = !byId.containsKey(h.getId());
        byId.put(h.getId(), h);
        notifyObservers(new EntityEvent(isNew ? EventType.CREATED : EventType.UPDATED, h.getId()));
    }

    @Override
    public List<Hospitalization> findAll() {
        return new ArrayList<>(byId.values());
    }

    // wire usa la lista cruda — no envolverla en unmodifiableList
    @Override
    public void seedCounter(long patientId, int lastUsedConsecutive) {
        counterByPatient.merge(patientId, lastUsedConsecutive + 1, Math::max);
    }
}
