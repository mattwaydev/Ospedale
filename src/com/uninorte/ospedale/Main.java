/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale;

import com.uninorte.ospedale.controller.PatientController;
import com.uninorte.ospedale.controller.DoctorController;
import com.uninorte.ospedale.controller.AppointmentController;
import com.uninorte.ospedale.controller.HospitalizationController;
import com.uninorte.ospedale.controller.TableDataController;
import com.uninorte.ospedale.controller.ComboDataController;
import com.uninorte.ospedale.model.repository.IAppointmentRepository;
import com.uninorte.ospedale.model.repository.IHospitalizationRepository;
import com.uninorte.ospedale.model.storage.InMemoryAppointmentRepository;
import com.uninorte.ospedale.model.storage.InMemoryHospitalizationRepository;
import com.formdev.flatlaf.FlatDarkLaf;
import com.uninorte.ospedale.controller.AuthController;
import com.uninorte.ospedale.model.loader.JsonAppointmentLoader;
import com.uninorte.ospedale.model.loader.JsonHospitalizationLoader;
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
        IAppointmentRepository apptRepo = new InMemoryAppointmentRepository();
IHospitalizationRepository hospRepo = new InMemoryHospitalizationRepository();

        try {
            new JsonUserLoader().load(userRepo, "json/users.json");
        } catch (Exception ex) {
            System.err.println("No se pudo cargar users.json: " + ex.getMessage());
        }
        new JsonAppointmentLoader().load(apptRepo, userRepo, "json/appointments.json");
        new JsonHospitalizationLoader().load(hospRepo, userRepo, "json/hospitalizations.json");

        AuthController authController = new AuthController(userRepo);
        PatientController patientController = new PatientController(patientRepo);
DoctorController doctorController = new DoctorController(doctorRepo);
AppointmentController appointmentController = new AppointmentController(apptRepo, patientRepo, doctorRepo);
HospitalizationController hospitalizationController = new HospitalizationController(hospRepo, patientRepo, doctorRepo, apptRepo);
TableDataController tableController = new TableDataController(apptRepo, patientRepo, doctorRepo, hospRepo);
ComboDataController comboController = new ComboDataController(doctorRepo, apptRepo, hospRepo, patientRepo);

ViewNavigator navigator = new ViewNavigator(
    authController,
    patientController,
    doctorController,
    appointmentController,
    hospitalizationController,
    tableController,
    comboController
);
        EventQueue.invokeLater(() -> navigator.showLogin());
    }
}
