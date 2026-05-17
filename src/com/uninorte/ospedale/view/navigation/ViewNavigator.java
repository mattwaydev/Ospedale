/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.view.navigation;

import com.uninorte.ospedale.controller.AuthController;
import com.uninorte.ospedale.model.dto.UserSessionDTO;


import packagee.NewJFrame1;
import packagee.NewJFrame11;
import packagee.NewJFrame111;

import java.util.ArrayList;

public class ViewNavigator {

    private final AuthController authController;

    public ViewNavigator(AuthController authController) {
        this.authController = authController;
    }

    public void showLogin() {
        new com.uninorte.ospedale.view.LoginView(authController, this).setVisible(true);
    }

    public void routeAfterLogin(UserSessionDTO session) {
        switch (session.role()) {
            case ADMIN -> openLegacyAdmin(session);
            case PATIENT -> openLegacyPatient(session);
            case DOCTOR -> openLegacyDoctor(session);
            default -> throw new IllegalStateException("Rol desconocido: " + session.role());
        }
    }

    private void openLegacyAdmin(UserSessionDTO s) {
        new NewJFrame11(null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), authController, this).setVisible(true);
    }

    private void openLegacyPatient(UserSessionDTO s) {
        new NewJFrame1(null, null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), authController, this).setVisible(true);

    }

    private void openLegacyDoctor(UserSessionDTO s) {
        new NewJFrame111(null, null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), authController, this).setVisible(true);
    }

    public void exit() {
        System.exit(0);
    }
}
