package com.uninorte.ospedale.model.loader;

import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.Hospitalization;
import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.entity.User;
import com.uninorte.ospedale.model.enums.HospitalizationStatus;
import com.uninorte.ospedale.model.enums.RoomType;
import com.uninorte.ospedale.model.repository.IHospitalizationRepository;
import com.uninorte.ospedale.model.repository.IUserRepository;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonHospitalizationLoader {

    private final RelationshipWirer wirer = new RelationshipWirer();

    public void load(IHospitalizationRepository repo, IUserRepository userRepo, String jsonPath) {
        try (FileReader reader = new FileReader(jsonPath)) {
            JSONObject root = new JSONObject(new JSONTokener(reader));
            if (!root.has("hospitalizations")) return;
            JSONArray hospitalizations = root.getJSONArray("hospitalizations");
            Map<Long, Integer> maxConsec = new HashMap<>();
            for (int i = 0; i < hospitalizations.length(); i++) {
                JSONObject entry = hospitalizations.getJSONObject(i);
                String id = entry.getString("id");
                long patientId = entry.getLong("patientId");
                long doctorId = entry.getLong("doctorId");

                Optional<User> patientFound = userRepo.findById(patientId);
                Optional<User> doctorFound = userRepo.findById(doctorId);
                if (patientFound.isEmpty() || !(patientFound.get() instanceof Patient)) {
                    System.err.println("JsonHospitalizationLoader: patient " + patientId + " not found, skipping");
                    continue;
                }
                if (doctorFound.isEmpty() || !(doctorFound.get() instanceof Doctor)) {
                    System.err.println("JsonHospitalizationLoader: doctor " + doctorId + " not found, skipping");
                    continue;
                }

                Patient patient = (Patient) patientFound.get();
                Doctor doctor = (Doctor) doctorFound.get();
                LocalDate date = LocalDate.parse(entry.getString("date"));
                String reason = entry.optString("reason", "");
                String observations = entry.optString("observations", "");

                RoomType roomType;
                try {
                    roomType = RoomType.valueOf(entry.optString("roomType", "STANDARD").toUpperCase());
                } catch (IllegalArgumentException e) {
                    roomType = RoomType.STANDARD;
                }

                HospitalizationStatus status = HospitalizationStatus.REQUESTED;
                if (entry.has("status")) {
                    try {
                        status = HospitalizationStatus.valueOf(entry.getString("status").toUpperCase());
                    } catch (IllegalArgumentException ignored) {}
                }

                Hospitalization hosp = new Hospitalization(id, patient, doctor, date, reason, roomType, observations, status);
                repo.save(hosp);
                wirer.wire(hosp);

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
            System.err.println("JsonHospitalizationLoader: failed to load " + jsonPath + ": " + ex.getMessage());
        }
    }
}
