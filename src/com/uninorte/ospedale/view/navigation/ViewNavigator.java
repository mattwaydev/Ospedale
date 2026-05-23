/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.view.navigation;

import com.uninorte.ospedale.controller.AuthController;
import com.uninorte.ospedale.controller.PatientController;
import com.uninorte.ospedale.controller.DoctorController;
import com.uninorte.ospedale.controller.AppointmentController;
import com.uninorte.ospedale.controller.HospitalizationController;
import com.uninorte.ospedale.controller.TableDataController;
import com.uninorte.ospedale.controller.ComboDataController;
import com.uninorte.ospedale.model.dto.UserSessionDTO;
import com.uninorte.ospedale.view.AdminView;
import com.uninorte.ospedale.view.DoctorView;
import com.uninorte.ospedale.view.LoginView;
import com.uninorte.ospedale.view.PatientView;
import javax.swing.JFrame;

public class ViewNavigator {

    private final AuthController authController;
    private final PatientController patientController;
    private final DoctorController doctorController;
    private final AppointmentController appointmentController;
    private final HospitalizationController hospitalizationController;
    private final TableDataController tableController;
    private final ComboDataController comboController;

    private JFrame currentView;
    private UserSessionDTO currentSession;

    public ViewNavigator(
            AuthController authController,
            PatientController patientController,
            DoctorController doctorController,
            AppointmentController appointmentController,
            HospitalizationController hospitalizationController,
            TableDataController tableController,
            ComboDataController comboController) {
        this.authController = authController;
        this.patientController = patientController;
        this.doctorController = doctorController;
        this.appointmentController = appointmentController;
        this.hospitalizationController = hospitalizationController;
        this.tableController = tableController;
        this.comboController = comboController;
    }

    public void showLogin() {
        if (currentView != null) currentView.dispose();
    LoginView login = new LoginView(authController, patientController, this);
    currentView = login;
    login.setVisible(true);
    }

    public void routeAfterLogin(UserSessionDTO session) {
        this.currentSession = session;
        if (currentView != null) currentView.dispose();
        switch (session.role()) {
            case ADMIN   -> showAdmin();
            case PATIENT -> openPatient(session);
            case DOCTOR  -> openLegacyDoctor(session);
            default -> throw new IllegalStateException("Rol desconocido: " + session.role());
        }
    }

    public void showAdmin() {
        if (currentView != null) currentView.dispose();
    AdminView admin = new AdminView(doctorController, patientController,
            comboController, tableController, this, currentSession);
    currentView = admin;
    admin.setVisible(true);
    }

    public void openDoctorAsAdmin(UserSessionDTO session) {
        openDoctor(session, true);
    }

   public void openPatientAsAdmin(UserSessionDTO session) {
    if (currentView != null) currentView.dispose();
    PatientView patientView = new PatientView(session, this,
            patientController, appointmentController, hospitalizationController,
            tableController, comboController, true);
    currentView = patientView;
    patientView.setVisible(true);
}

    public void logout() {
        currentSession = null;
        showLogin();
    }

    public void back() {
        showAdmin();
    }

    public void exit() {
        System.exit(0);
    }

    private void openPatient(UserSessionDTO session) {
    PatientView patientView = new PatientView(session, this,
            patientController, appointmentController, hospitalizationController,
            tableController, comboController, false);
    currentView = patientView;
    patientView.setVisible(true);
}

    public void openDoctor(UserSessionDTO session, boolean asAdmin) {
        if (currentView != null) currentView.dispose();
        DoctorView doctorView = new DoctorView(session, this,
                doctorController, appointmentController, hospitalizationController,
                patientController, tableController, comboController, asAdmin);
        currentView = doctorView;
        doctorView.setVisible(true);
    }

    private void openLegacyDoctor(UserSessionDTO s) {
        openDoctor(s, false);
    }
}