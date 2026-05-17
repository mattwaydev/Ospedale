/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale;

import com.formdev.flatlaf.FlatDarkLaf;
import com.uninorte.ospedale.controller.AuthController;
import com.uninorte.ospedale.model.loader.JsonUserLoader;
import com.uninorte.ospedale.model.repository.IDoctorRepository;
import com.uninorte.ospedale.model.repository.IPatientRepository;
import com.uninorte.ospedale.model.repository.IUserRepository;
import com.uninorte.ospedale.model.storage.InMemoryDoctorRepository;
import com.uninorte.ospedale.model.storage.InMemoryPatientRepository;
import com.uninorte.ospedale.model.storage.InMemoryUserRepository;
import com.uninorte.ospedale.view.navigation.ViewNavigator;
import java.awt.EventQueue;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("FlatLaf falló: " + ex.getMessage());
        }

        IUserRepository userRepo = new InMemoryUserRepository();
        IPatientRepository patientRepo = new InMemoryPatientRepository(userRepo);
        IDoctorRepository doctorRepo = new InMemoryDoctorRepository(userRepo);

        try {
            new JsonUserLoader().load(userRepo, "json/users.json");
        } catch (Exception ex) {
            System.err.println("No se pudo cargar users.json: " + ex.getMessage());

        }

        AuthController authController = new AuthController(userRepo);

        ViewNavigator navigator = new ViewNavigator(authController);

        EventQueue.invokeLater(() -> navigator.showLogin());
    }
}
