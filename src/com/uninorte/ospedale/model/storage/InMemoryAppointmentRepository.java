/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.model.storage;

import com.uninorte.ospedale.model.entity.Appointment;
import com.uninorte.ospedale.model.enums.AppointmentStatus;
import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Samuel Ramirez
 */
public class InMemoryAppointmentRepository implements IAppointmentRepository {

    private final Map<String, Appointment> byId = new HashMap<>();
    private final Map<Long, Integer> counterByPatient = new HashMap<>();

    @Override
    public Optional<Appointment> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<Appointment> findByPatientId(long pid) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : byId.values()) {
            if (a.getPatient().getId() == pid) {
                out.add(a);
            }
        }
        return out;
    }

    @Override
    public List<Appointment> findByDoctorId(long did) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : byId.values()) {
            if (a.getDoctor().getId() == did) {
                out.add(a);
            }
        }
        return out;
    }

    @Override
    public List<Appointment> findByDoctorIdAndStatus(long did, AppointmentStatus s) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : byId.values()) {
            if (a.getDoctor().getId() == did && a.getStatus() == s) {
                out.add(a);
            }
        }
        return out;
    }

    @Override
    public boolean doctorIsAvailableAt(long did, LocalDateTime slot) {
        for (Appointment a : byId.values()) {
            if (a.getDoctor().getId() == did
                    && a.getDatetime().equals(slot)
                    && a.getStatus() != AppointmentStatus.CANCELED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String nextIdForPatient(long pid) {
        int next = counterByPatient.getOrDefault(pid, 0);
        counterByPatient.put(pid, next + 1);
        return String.format("A-%d-%04d", pid, next);
    }

    @Override
    public void save(Appointment a) {
        byId.put(a.getId(), a);
    }
}
