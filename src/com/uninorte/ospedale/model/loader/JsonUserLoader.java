/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.model.loader;

import com.uninorte.ospedale.model.entity.Administrator;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.enums.Specialty;
import com.uninorte.ospedale.model.repository.IUserRepository;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author Samuel Ramirez
 */
public class JsonUserLoader {

    public void load(IUserRepository repo, String jsonPath) {
        try (FileReader reader = new FileReader(jsonPath)) {
            JSONObject root = new JSONObject(new JSONTokener(reader));
            JSONArray users = root.getJSONArray("users");
            for (int i = 0; i < users.length(); i++) {
                JSONObject u = users.getJSONObject(i);
                String type = u.getString("type").toLowerCase();
                long id = u.getLong("id");
                String username = u.getString("username");
                String firstname = u.getString("firstname");
                String lastname = u.getString("lastname");
                String password = u.getString("password");
                switch (type) {
                    case "admin":
                        repo.save(new Administrator(id, username, firstname, lastname, password));
                        break;
                    case "patient":
                        LocalDate birthdate = LocalDate.parse(u.getString("birthdate"));
                        repo.save(new Patient(
                                id, username, firstname, lastname, password,
                                u.getString("email"),
                                birthdate,
                                u.getBoolean("gender"),
                                u.getLong("phone"),
                                u.getString("address")
                        ));
                        break;
                    case "doctor":
                        Specialty specialty = SpecialtyNormalizer.normalize(u.getString("specialty"));
                        repo.save(new Doctor(
                                id, username, firstname, lastname, password,
                                specialty,
                                u.getString("licenceNumber"),
                                u.getString("assignedOffice")
                        ));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown user type: " + type);
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load users from: " + jsonPath, ex);
        }
    }
}
