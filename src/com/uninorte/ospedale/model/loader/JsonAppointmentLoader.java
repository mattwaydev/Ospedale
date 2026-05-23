package com.uninorte.ospedale.model.loader;

import com.uninorte.ospedale.model.entity.Appointment;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.entity.User;
import com.uninorte.ospedale.model.enums.AppointmentStatus;
import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import com.uninorte.ospedale.model.repository.IUserRepository;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonAppointmentLoader {

    private final RelationshipWirer wirer = new RelationshipWirer();

    public void load(IAppointmentRepository repo, IUserRepository userRepo, String jsonPath) {
        try (FileReader reader = new FileReader(jsonPath)) {
            JSONObject root = new JSONObject(new JSONTokener(reader));
            if (!root.has("appointments")) return;
            JSONArray appointments = root.getJSONArray("appointments");
            Map<Long, Integer> maxConsec = new HashMap<>();
            for (int i = 0; i < appointments.length(); i++) {
                JSONObject entry = appointments.getJSONObject(i);
                String id = entry.getString("id");
                long patientId = entry.getLong("patientId");
                long doctorId = entry.getLong("doctorId");

                Optional<User> patientFound = userRepo.findById(patientId);
                Optional<User> doctorFound = userRepo.findById(doctorId);
                if (patientFound.isEmpty() || !(patientFound.get() instanceof Patient)) {
                    System.err.println("JsonAppointmentLoader: patient " + patientId + " not found, skipping");
                    continue;
                }
                if (doctorFound.isEmpty() || !(doctorFound.get() instanceof Doctor)) {
                    System.err.println("JsonAppointmentLoader: doctor " + doctorId + " not found, skipping");
                    continue;
                }

                Patient patient = (Patient) patientFound.get();
                Doctor doctor = (Doctor) doctorFound.get();
                LocalDateTime datetime = LocalDateTime.parse(entry.getString("datetime"));
                String reason = entry.optString("reason", "");
                boolean type = entry.optBoolean("type", false);

                Appointment appt = new Appointment(id, patient, doctor,
                        doctor.getSpecialty(), datetime, reason, type);

                if (entry.has("status")) {
                    try {
                        appt.setStatus(AppointmentStatus.valueOf(entry.getString("status").toUpperCase()));
                    } catch (IllegalArgumentException ignored) {}
                }

                repo.save(appt);
                wirer.wire(appt);

                String[] parts = id.split("-");
                if (parts.length >= 3) {
                    try {
                        long pid = Long.parseLong(parts[1]);
                        int consec = Integer.parseInt(parts[2]);
                        maxConsec.merge(pid, consec, Math::max);
                    } catch (NumberFormatException ignored) {}
                }
            }
            for (Map.Entry<Long, Integer> e : maxConsec.entrySet()) {
                repo.seedCounter(e.getKey(), e.getValue());
            }
        } catch (IOException ex) {
            System.err.println("JsonAppointmentLoader: failed to load " + jsonPath + ": " + ex.getMessage());
        }
    }
}
