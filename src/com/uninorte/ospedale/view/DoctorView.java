/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.uninorte.ospedale.view;

import com.uninorte.ospedale.controller.AppointmentController;
import com.uninorte.ospedale.controller.ComboDataController;
import com.uninorte.ospedale.controller.DoctorController;
import com.uninorte.ospedale.controller.HospitalizationController;
import com.uninorte.ospedale.controller.PatientController;
import com.uninorte.ospedale.controller.TableDataController;
import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.model.dto.CompleteAppointmentDTO;
import com.uninorte.ospedale.model.dto.DoctorFormDTO;
import com.uninorte.ospedale.model.dto.HospitalizationFromAppointmentDTO;
import com.uninorte.ospedale.model.dto.HospitalizationRowDTO;
import com.uninorte.ospedale.model.dto.PatientFormDTO;
import com.uninorte.ospedale.model.dto.PrescriptionDTO;
import com.uninorte.ospedale.model.dto.PrescriptionRowDTO;
import com.uninorte.ospedale.model.dto.UserSessionDTO;
import com.uninorte.ospedale.view.navigation.ViewNavigator;
import java.awt.Color;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jjlora
 * @author edangulo
 * @author Samuel Ramirez
 * @author Matt
 * @author Sebastián
 */
public class DoctorView extends javax.swing.JFrame {

    private int x, y;
    private final UserSessionDTO session;
    private final ViewNavigator navigator;
    private final DoctorController dc;
    private final AppointmentController ac;
    private final HospitalizationController hc;
    private final PatientController pc;
    private final TableDataController tc;
    private final ComboDataController cc;
    private final boolean openedByAdmin;

    public DoctorView(UserSessionDTO session, ViewNavigator navigator,
            DoctorController dc, AppointmentController ac, HospitalizationController hc,
            PatientController pc, TableDataController tc, ComboDataController cc,
            boolean openedByAdmin) {
        initComponents();
        this.session = session;
        this.navigator = navigator;
        this.dc = dc;
        this.ac = ac;
        this.hc = hc;
        this.pc = pc;
        this.tc = tc;
        this.cc = cc;
        this.openedByAdmin = openedByAdmin;

        btnBack.setVisible(openedByAdmin);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(null);

        Response<DoctorFormDTO> profileResp = dc.getProfile(session.id());
        if (profileResp.getCode() == 200) {
            DoctorFormDTO dto = profileResp.getData();
            txtDoctorFirstname.setText(dto.firstname());
            txtDoctorLastname.setText(dto.lastname());
            txtDoctorUsername.setText(dto.username());
            txtDoctorLicenceNumber.setText(dto.licenceNumber());
            txtDoctorOffice.setText(dto.assignedOffice());
            cmbDoctorSpecialty.setSelectedItem(dto.specialty().replace("_", " & "));
        }
        lblDoctorId.setText("DOCTOR VIEW | ID: " + session.id());

        populateCombosFromResponse(cmbAcceptAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "REQUESTED"));
        populateCombosFromResponse(cmbCompleteAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "PENDING"));
        populateCombosFromResponse(cmbPrescribeAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "PENDING"));
        populateCombosFromResponse(cmbHospAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "PENDING"));
        populateCombosFromResponse(cmbRescheduleAppointmentId, cc.getReschedulableForDoctor(session.id()));
        populateCombosFromResponse(cmbCancelHospitalizationId, cc.getHospitalizationIdsByStatus("REQUESTED"));
        populateCombosFromResponse(cmbSearchPatient, cc.getPatientsCombo());

        rdoTotalAppointments.addItemListener(e -> refreshAppointmentsTable());
        rdoPendingAppointments.addItemListener(e -> refreshAppointmentsTable());
        rdoHospRequests.addItemListener(e -> refreshHospitalizationsTable());
        rdoHospByPatientId.addItemListener(e -> refreshHospitalizationsTable());

        cmbPrescribeAppointmentId.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                Object sel = cmbPrescribeAppointmentId.getSelectedItem();
                if (sel != null && !"Select one".equals(sel.toString())) {
                    refreshPrescriptionsTable(sel.toString());
                }
            }
        });

        refreshAppointmentsTable();
        refreshHospitalizationsTable();
    }

    @SuppressWarnings("unchecked")
    private void populateCombosFromResponse(javax.swing.JComboBox<String> combo, Response<Object> resp) {
        combo.removeAllItems();
        combo.addItem("Select one");
        if (resp.getCode() == 200 && resp.getData() instanceof List) {
            for (Object item : (List<?>) resp.getData()) {
                combo.addItem(item.toString());
            }
        }
    }

    private void refreshAppointmentsTable() {
        boolean onlyPending = rdoPendingAppointments.isSelected();
        Response<Object> resp = tc.getDoctorAppointments(session.id(), onlyPending);
        DefaultTableModel model = (DefaultTableModel) tblDoctorAppointments.getModel();
        model.setRowCount(0);
        if (resp.getCode() == 200 && resp.getData() instanceof List) {
            for (Object row : (List<?>) resp.getData()) {
                model.addRow((Object[]) row);
            }
        }
    }

    private void refreshHospitalizationsTable() {
        DefaultTableModel model = (DefaultTableModel) tblHospitalizations.getModel();
        model.setRowCount(0);
        if (rdoHospRequests.isSelected()) {
            for (HospitalizationRowDTO row : tc.getHospitalizationRequests()) {
                model.addRow(new Object[]{row.id(), row.date(), row.patientFullname(), row.doctorFullname(), row.roomType(), row.status()});
            }
        } else if (rdoHospByPatientId.isSelected()) {
            Object sel = cmbSearchPatient.getSelectedItem();
            if (sel != null && !"Select one".equals(sel.toString())) {
                try {
                    long pid = Long.decode(sel.toString().split(" - ")[0].trim());
                    for (HospitalizationRowDTO row : tc.getHospitalizationsByPatient(pid)) {
                        model.addRow(new Object[]{row.id(), row.date(), row.patientFullname(), row.doctorFullname(), row.roomType(), row.status()});
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    private void refreshPrescriptionsTable(String appointmentId) {
        DefaultTableModel model = (DefaultTableModel) tblPrescriptions.getModel();
        model.setRowCount(0);
        for (PrescriptionRowDTO row : tc.getPrescriptions(appointmentId)) {
            model.addRow(new Object[]{row.medicationName(), row.dose(), row.administrationRoute(),
                row.treatmentDuration(), row.additionalInstructions(), row.frequency()});
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRound1 = new packagee.PanelRound();
        panelRound2 = new packagee.PanelRound();
        btnClose = new javax.swing.JButton();
        lblDoctorId = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        rdoTotalAppointments = new javax.swing.JRadioButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblDoctorAppointments = new javax.swing.JTable();
        rdoPendingAppointments = new javax.swing.JRadioButton();
        btnLogout = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        cmbSearchPatient = new javax.swing.JComboBox<>();
        jLabel38 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblHospitalizations = new javax.swing.JTable();
        btnSearchPatient = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtDoctorFirstname = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDoctorLastname = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtDoctorLicenceNumber = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtDoctorOffice = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtDoctorUsername = new javax.swing.JTextField();
        txtDoctorPassword = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtDoctorPasswordConfirm = new javax.swing.JTextField();
        cmbDoctorSpecialty = new javax.swing.JComboBox<>();
        btnSaveProfile = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cmbAcceptAppointmentId = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        btnAcceptAppointment = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        cmbRescheduleAppointmentId = new javax.swing.JComboBox<>();
        btnRescheduleAppointment = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        txtRescheduleNewTime = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtRescheduleReason = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        cmbCompleteAppointmentId = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        btnCompleteAppointment = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtHospDate = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txtHospRoomType = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtHospObservations = new javax.swing.JTextArea();
        btnSendToHospitalization = new javax.swing.JButton();
        cmbHospAppointmentId = new javax.swing.JComboBox<>();
        rdoHospRequests = new javax.swing.JRadioButton();
        rdoHospByPatientId = new javax.swing.JRadioButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtCompleteDiagnosis = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        txtCompleteObservations = new javax.swing.JTextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        txtCompleteTreatment = new javax.swing.JTextArea();
        jScrollPane9 = new javax.swing.JScrollPane();
        txtCompleteFollowUp = new javax.swing.JTextArea();
        jSeparator4 = new javax.swing.JSeparator();
        btnCancelHospitalization = new javax.swing.JButton();
        cmbCancelHospitalizationId = new javax.swing.JComboBox<>();
        jScrollPane10 = new javax.swing.JScrollPane();
        txtHospReason = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txtPrescribeMedication = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txtPrescribeDose = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        txtPrescribeRoute = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        txtPrescribeFrequency = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        txtPrescribeDuration = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        txtPrescribeInstructions = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPrescriptions = new javax.swing.JTable();
        btnAddMedicationRow = new javax.swing.JButton();
        btnPrescribe = new javax.swing.JButton();
        cmbPrescribeAppointmentId = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        panelRound1.setRadius(50);

        panelRound2.setRadius(50);
        panelRound2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelRound2MouseDragged(evt);
            }
        });
        panelRound2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelRound2MousePressed(evt);
            }
        });

        btnClose.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnClose.setText("X");
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnClose.setFocusable(false);
        btnClose.setRequestFocusEnabled(false);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        lblDoctorId.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N
        lblDoctorId.setText("DOCTOR VIEW");

        btnBack.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDoctorId)
                .addGap(32, 32, 32)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClose)
                .addGap(19, 19, 19))
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDoctorId, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnBack))
        );

        rdoTotalAppointments.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rdoTotalAppointments.setText("Total appointments");
        rdoTotalAppointments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoTotalAppointmentsActionPerformed(evt);
            }
        });

        tblDoctorAppointments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Date", "Patient", "Specialty", "Type", "Status"
            }
        ));
        jScrollPane3.setViewportView(tblDoctorAppointments);

        rdoPendingAppointments.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rdoPendingAppointments.setText("Pending appointments");
        rdoPendingAppointments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoPendingAppointmentsActionPerformed(evt);
            }
        });

        btnLogout.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLogout)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addComponent(rdoTotalAppointments)
                            .addGap(18, 18, 18)
                            .addComponent(rdoPendingAppointments))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(108, 108, 108)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1035, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(152, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoTotalAppointments)
                    .addComponent(rdoPendingAppointments))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 504, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addGap(23, 23, 23))
        );

        jTabbedPane1.addTab("Appointments visualization", jPanel4);

        cmbSearchPatient.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbSearchPatient.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        jLabel38.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel38.setText("Patient");

        tblHospitalizations.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Date", "Doctor", "Specialty", "Type", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblHospitalizations);

        btnSearchPatient.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnSearchPatient.setText("Search");
        btnSearchPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchPatientActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel38)
                        .addGap(18, 18, 18)
                        .addComponent(cmbSearchPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(99, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnSearchPatient)
                .addGap(601, 601, 601))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(cmbSearchPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(btnSearchPatient)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("History Appointments of a patient", jPanel5);

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel2.setText("Firstname");

        txtDoctorFirstname.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel3.setText("Lastname");

        txtDoctorLastname.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel5.setText("Specialty");

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel7.setText("License Number");

        txtDoctorLicenceNumber.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel8.setText("Assigned office");

        txtDoctorOffice.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("User");

        txtDoctorUsername.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        txtDoctorPassword.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Password");

        jLabel11.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel11.setText("Password confirmation");

        txtDoctorPasswordConfirm.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        cmbDoctorSpecialty.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbDoctorSpecialty.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one", "General Medicine", "Cardiology", "Pediatrics", "Neurology", "Traumatology & Orthopedics", "Gynecology & Obstetrics", "Dermatology", "Psychiatry", "Oncology", "Ophthalmology", "Internal Medicine" }));

        btnSaveProfile.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnSaveProfile.setText("Save");
        btnSaveProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveProfileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(211, 211, 211)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDoctorFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtDoctorLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(cmbDoctorSpecialty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(351, 351, 351)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtDoctorLicenceNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDoctorOffice, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(558, 558, 558)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDoctorPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtDoctorUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(521, 521, 521)
                        .addComponent(jLabel11))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(576, 576, 576)
                        .addComponent(btnSaveProfile))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(561, 561, 561)
                        .addComponent(txtDoctorPasswordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(269, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtDoctorFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtDoctorLastname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(cmbDoctorSpecialty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtDoctorLicenceNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDoctorOffice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(30, 30, 30)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(txtDoctorUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(27, 27, 27)
                .addComponent(txtDoctorPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(txtDoctorPasswordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnSaveProfile)
                .addContainerGap(161, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Modify info", jPanel3);

        jLabel14.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Appointment ID");

        jLabel13.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel13.setText("Accept medical appointment");

        cmbAcceptAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbAcceptAppointmentId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        btnAcceptAppointment.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnAcceptAppointment.setText("Accept");
        btnAcceptAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcceptAppointmentActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Reschedule medical appointment");

        jLabel16.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Appointment");

        cmbRescheduleAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbRescheduleAppointmentId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        btnRescheduleAppointment.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnRescheduleAppointment.setText("Accept");
        btnRescheduleAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRescheduleAppointmentActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("New time appointment");

        txtRescheduleNewTime.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Reason for appointment");

        txtRescheduleReason.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel19.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Complete medical appointment");

        jLabel20.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Appointment");

        cmbCompleteAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbCompleteAppointmentId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        jLabel21.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Diagnosis");

        jLabel22.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Observations");

        jLabel23.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Recommended treatment");

        jLabel24.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Follow-up indication");

        btnCompleteAppointment.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCompleteAppointment.setText("Complete");
        btnCompleteAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompleteAppointmentActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("Hospitalization");

        jLabel27.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Reason for hospitalization");

        jLabel28.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("Date of entry");

        txtHospDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel29.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Room type");

        txtHospRoomType.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel30.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("Observations");

        txtHospObservations.setColumns(20);
        txtHospObservations.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtHospObservations.setRows(5);
        jScrollPane1.setViewportView(txtHospObservations);

        btnSendToHospitalization.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnSendToHospitalization.setText("Generate");
        btnSendToHospitalization.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendToHospitalizationActionPerformed(evt);
            }
        });

        cmbHospAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbHospAppointmentId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        rdoHospRequests.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rdoHospRequests.setText("Requests");

        rdoHospByPatientId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rdoHospByPatientId.setText("Patient ID");

        txtCompleteDiagnosis.setColumns(20);
        txtCompleteDiagnosis.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtCompleteDiagnosis.setRows(5);
        jScrollPane6.setViewportView(txtCompleteDiagnosis);

        txtCompleteObservations.setColumns(20);
        txtCompleteObservations.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtCompleteObservations.setRows(5);
        jScrollPane7.setViewportView(txtCompleteObservations);

        txtCompleteTreatment.setColumns(20);
        txtCompleteTreatment.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtCompleteTreatment.setRows(5);
        jScrollPane8.setViewportView(txtCompleteTreatment);

        txtCompleteFollowUp.setColumns(20);
        txtCompleteFollowUp.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtCompleteFollowUp.setRows(5);
        jScrollPane9.setViewportView(txtCompleteFollowUp);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        btnCancelHospitalization.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCancelHospitalization.setText("Cancel");
        btnCancelHospitalization.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelHospitalizationActionPerformed(evt);
            }
        });

        cmbCancelHospitalizationId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbCancelHospitalizationId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        txtHospReason.setColumns(20);
        txtHospReason.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtHospReason.setRows(5);
        jScrollPane10.setViewportView(txtHospReason);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(btnAcceptAppointment)
                                        .addGap(87, 87, 87))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(cmbAcceptAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(67, 67, 67))))
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel13)
                        .addGap(22, 22, 22)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(90, 90, 90)
                                    .addComponent(cmbRescheduleAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(99, 99, 99)
                                    .addComponent(txtRescheduleNewTime, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(98, 98, 98)
                                    .addComponent(txtRescheduleReason, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(112, 112, 112)
                                    .addComponent(btnRescheduleAppointment)))
                            .addGap(91, 91, 91))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(btnCompleteAppointment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(99, 99, 99)
                                        .addComponent(cmbCompleteAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 25, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(42, 42, 42)
                                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(41, 41, 41)
                                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(42, 42, 42)
                                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(43, 43, 43)
                                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(121, 121, 121)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtHospDate, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtHospRoomType, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnCancelHospitalization)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSendToHospitalization))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(56, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(cmbHospAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(rdoHospRequests)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(rdoHospByPatientId, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(cmbCancelHospitalizationId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel19)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel20)
                        .addGap(18, 18, 18)
                        .addComponent(cmbCompleteAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel21)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel22)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel23)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCompleteAppointment)
                        .addGap(12, 12, 12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(cmbAcceptAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(btnAcceptAppointment))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16)
                                .addGap(18, 18, 18)
                                .addComponent(cmbRescheduleAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(txtRescheduleNewTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(txtRescheduleReason, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(btnRescheduleAppointment)))
                        .addGap(18, 18, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel25)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoHospRequests)
                    .addComponent(rdoHospByPatientId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbHospAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbCancelHospitalizationId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel27)
                .addGap(16, 16, 16)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addGap(18, 18, 18)
                .addComponent(txtHospDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel29)
                .addGap(18, 18, 18)
                .addComponent(txtHospRoomType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel30)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSendToHospitalization)
                    .addComponent(btnCancelHospitalization))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jTabbedPane1.addTab("Request/Appointments", jPanel1);

        jLabel31.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel31.setText("Appointment ID");

        jLabel32.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel32.setText("Medication name");

        txtPrescribeMedication.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel33.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel33.setText("Dose");

        txtPrescribeDose.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel34.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel34.setText("Administration route");

        txtPrescribeRoute.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel35.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel35.setText("Frecuency");

        txtPrescribeFrequency.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel36.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel36.setText("Treatment duration");

        txtPrescribeDuration.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel37.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel37.setText("Additional instructions");

        txtPrescribeInstructions.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        tblPrescriptions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Appointment ID", "Medication name", "Dose", "Administration route", "Treatment duration", "Additional instructions", "Frecuency"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblPrescriptions);

        btnAddMedicationRow.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnAddMedicationRow.setText("Add");
        btnAddMedicationRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMedicationRowActionPerformed(evt);
            }
        });

        btnPrescribe.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnPrescribe.setText("Prescribe");
        btnPrescribe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrescribeActionPerformed(evt);
            }
        });

        cmbPrescribeAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbPrescribeAppointmentId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel31)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cmbPrescribeAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(9, 9, 9)
                                        .addComponent(jLabel32))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel36)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtPrescribeDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel37)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtPrescribeInstructions, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel35)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtPrescribeFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtPrescribeMedication, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel33)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtPrescribeDose, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel34)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtPrescribeRoute, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAddMedicationRow))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(583, 583, 583)
                        .addComponent(btnPrescribe)))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(jLabel32)
                    .addComponent(txtPrescribeMedication, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33)
                    .addComponent(txtPrescribeDose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34)
                    .addComponent(txtPrescribeRoute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddMedicationRow)
                    .addComponent(cmbPrescribeAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(txtPrescribeDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(txtPrescribeInstructions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35)
                    .addComponent(txtPrescribeFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(btnPrescribe)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Prescribe medications", jPanel2);

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelRound2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRound1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRound1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void panelRound2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_panelRound2MousePressed

    private void panelRound2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseDragged
        this.setLocation(this.getLocation().x + evt.getX() - x, this.getLocation().y + evt.getY() - y);
    }//GEN-LAST:event_panelRound2MouseDragged

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        navigator.exit();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        navigator.back();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        navigator.logout();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void rdoTotalAppointmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoTotalAppointmentsActionPerformed
        rdoPendingAppointments.setSelected(false);
        refreshAppointmentsTable();
    }//GEN-LAST:event_rdoTotalAppointmentsActionPerformed

    private void rdoPendingAppointmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoPendingAppointmentsActionPerformed
        rdoTotalAppointments.setSelected(false);
        refreshAppointmentsTable();
    }//GEN-LAST:event_rdoPendingAppointmentsActionPerformed

    private void btnSearchPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchPatientActionPerformed
        Object sel = cmbSearchPatient.getSelectedItem();
        if (sel == null || "Select one".equals(sel.toString())) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente.");
            return;
        }
        try {
            long pid = Long.decode(sel.toString().split(" - ")[0].trim());
            Response<PatientFormDTO> resp = pc.getProfile(pid);
            if (resp.getCode() == 200) {
                PatientFormDTO dto = resp.getData();
                JOptionPane.showMessageDialog(this,
                        "Paciente: " + dto.firstname() + " " + dto.lastname()
                        + "\nEmail: " + dto.email()
                        + "\nFecha de nac.: " + dto.birthdate()
                        + "\nTeléfono: " + dto.phone()
                        + "\nDirección: " + dto.address());
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + resp.getMessage());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID de paciente inválido.");
        }
    }//GEN-LAST:event_btnSearchPatientActionPerformed

    private void btnSaveProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveProfileActionPerformed
        DoctorFormDTO dto = new DoctorFormDTO(
                String.valueOf(session.id()),
                txtDoctorUsername.getText(),
                txtDoctorFirstname.getText(),
                txtDoctorLastname.getText(),
                txtDoctorPassword.getText(),
                txtDoctorPasswordConfirm.getText(),
                cmbDoctorSpecialty.getSelectedItem() != null
                        ? cmbDoctorSpecialty.getSelectedItem().toString().replace(" & ", "_")
                        : "",
                txtDoctorLicenceNumber.getText(),
                txtDoctorOffice.getText()
        );
        Response<?> resp = dc.update(session.id(), dto);
        JOptionPane.showMessageDialog(this, resp.getMessage());
    }//GEN-LAST:event_btnSaveProfileActionPerformed

    private void btnAcceptAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcceptAppointmentActionPerformed
        Object sel = cmbAcceptAppointmentId.getSelectedItem();
        if (sel == null || "Select one".equals(sel.toString())) {
            JOptionPane.showMessageDialog(this, "Seleccione un appointment.");
            return;
        }
        Response<Object> resp = ac.accept(sel.toString(), session.id());
        JOptionPane.showMessageDialog(this, resp.getMessage());
        if (resp.getCode() == 200) {
            populateCombosFromResponse(cmbAcceptAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "REQUESTED"));
            populateCombosFromResponse(cmbCompleteAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "PENDING"));
            populateCombosFromResponse(cmbRescheduleAppointmentId, cc.getReschedulableForDoctor(session.id()));
            populateCombosFromResponse(cmbHospAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "PENDING"));
            populateCombosFromResponse(cmbPrescribeAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "PENDING"));
            refreshAppointmentsTable();
        }
    }//GEN-LAST:event_btnAcceptAppointmentActionPerformed

    private void btnRescheduleAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRescheduleAppointmentActionPerformed
        Object sel = cmbRescheduleAppointmentId.getSelectedItem();
        if (sel == null || "Select one".equals(sel.toString())) {
            JOptionPane.showMessageDialog(this, "Seleccione un appointment.");
            return;
        }
        String newTime = txtRescheduleNewTime.getText().trim();
        String reason = txtRescheduleReason.getText().trim();
        Response<Object> resp = ac.reschedule(sel.toString(), session.id(), newTime, reason);
        JOptionPane.showMessageDialog(this, resp.getMessage());
        if (resp.getCode() == 200) {
            txtRescheduleNewTime.setText("");
            txtRescheduleReason.setText("");
            refreshAppointmentsTable();
        }
    }//GEN-LAST:event_btnRescheduleAppointmentActionPerformed

    private void btnCompleteAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompleteAppointmentActionPerformed
        Object sel = cmbCompleteAppointmentId.getSelectedItem();
        if (sel == null || "Select one".equals(sel.toString())) {
            JOptionPane.showMessageDialog(this, "Seleccione un appointment.");
            return;
        }
        CompleteAppointmentDTO dto = new CompleteAppointmentDTO(
                txtCompleteDiagnosis.getText(),
                txtCompleteObservations.getText(),
                txtCompleteTreatment.getText(),
                txtCompleteFollowUp.getText()
        );
        Response<Object> resp = ac.complete(sel.toString(), session.id(), dto);
        JOptionPane.showMessageDialog(this, resp.getMessage());
        if (resp.getCode() == 200) {
            txtCompleteDiagnosis.setText("");
            txtCompleteObservations.setText("");
            txtCompleteTreatment.setText("");
            txtCompleteFollowUp.setText("");
            populateCombosFromResponse(cmbCompleteAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "PENDING"));
            populateCombosFromResponse(cmbHospAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "PENDING"));
            populateCombosFromResponse(cmbPrescribeAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "PENDING"));
            refreshAppointmentsTable();
        }
    }//GEN-LAST:event_btnCompleteAppointmentActionPerformed

    private void btnPrescribeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrescribeActionPerformed
        Object sel = cmbPrescribeAppointmentId.getSelectedItem();
        if (sel == null || "Select one".equals(sel.toString())) {
            JOptionPane.showMessageDialog(this, "Seleccione un appointment.");
            return;
        }
        PrescriptionDTO dto = new PrescriptionDTO(
                sel.toString(),
                txtPrescribeMedication.getText(),
                txtPrescribeDose.getText(),
                txtPrescribeRoute.getText(),
                txtPrescribeDuration.getText(),
                txtPrescribeInstructions.getText(),
                txtPrescribeFrequency.getText()
        );
        Response<Object> resp = ac.prescribe(sel.toString(), session.id(), dto);
        JOptionPane.showMessageDialog(this, resp.getMessage());
        if (resp.getCode() == 200) {
            txtPrescribeMedication.setText("");
            txtPrescribeDose.setText("");
            txtPrescribeRoute.setText("");
            txtPrescribeDuration.setText("");
            txtPrescribeInstructions.setText("");
            txtPrescribeFrequency.setText("");
            refreshPrescriptionsTable(sel.toString());
        }
    }//GEN-LAST:event_btnPrescribeActionPerformed

    private void btnSendToHospitalizationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendToHospitalizationActionPerformed
        Object sel = cmbHospAppointmentId.getSelectedItem();
        if (sel == null || "Select one".equals(sel.toString())) {
            JOptionPane.showMessageDialog(this, "Seleccione un appointment.");
            return;
        }
        HospitalizationFromAppointmentDTO dto = new HospitalizationFromAppointmentDTO(
                txtHospDate.getText(),
                txtHospRoomType.getText(),
                txtHospReason.getText(),
                txtHospObservations.getText()
        );
        Response<Object> resp = hc.fromAppointment(sel.toString(), session.id(), dto);
        JOptionPane.showMessageDialog(this, resp.getMessage());
        if (resp.getCode() == 200) {
            txtHospDate.setText("");
            txtHospRoomType.setText("");
            txtHospReason.setText("");
            txtHospObservations.setText("");
            populateCombosFromResponse(cmbHospAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "PENDING"));
            populateCombosFromResponse(cmbCompleteAppointmentId, cc.getAppointmentIdsByDoctorAndStatus(session.id(), "PENDING"));
            refreshHospitalizationsTable();
        }
    }//GEN-LAST:event_btnSendToHospitalizationActionPerformed

    private void btnCancelHospitalizationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelHospitalizationActionPerformed
        Object sel = cmbCancelHospitalizationId.getSelectedItem();
        if (sel == null || "Select one".equals(sel.toString())) {
            JOptionPane.showMessageDialog(this, "Seleccione una hospitalización.");
            return;
        }
        Response<Object> resp = hc.cancel(sel.toString(), session.id());
        JOptionPane.showMessageDialog(this, resp.getMessage());
        if (resp.getCode() == 200) {
            populateCombosFromResponse(cmbCancelHospitalizationId, cc.getHospitalizationIdsByStatus("REQUESTED"));
            refreshHospitalizationsTable();
        }
    }//GEN-LAST:event_btnCancelHospitalizationActionPerformed

    private void btnAddMedicationRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMedicationRowActionPerformed
        JOptionPane.showMessageDialog(this,
                "Función no disponible — agregue las prescripciones de a una usando el botón Prescribe.");
    }//GEN-LAST:event_btnAddMedicationRowActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAcceptAppointment;
    private javax.swing.JButton btnAddMedicationRow;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCancelHospitalization;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnCompleteAppointment;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPrescribe;
    private javax.swing.JButton btnRescheduleAppointment;
    private javax.swing.JButton btnSaveProfile;
    private javax.swing.JButton btnSearchPatient;
    private javax.swing.JButton btnSendToHospitalization;
    private javax.swing.JComboBox<String> cmbAcceptAppointmentId;
    private javax.swing.JComboBox<String> cmbCancelHospitalizationId;
    private javax.swing.JComboBox<String> cmbCompleteAppointmentId;
    private javax.swing.JComboBox<String> cmbDoctorSpecialty;
    private javax.swing.JComboBox<String> cmbHospAppointmentId;
    private javax.swing.JComboBox<String> cmbPrescribeAppointmentId;
    private javax.swing.JComboBox<String> cmbRescheduleAppointmentId;
    private javax.swing.JComboBox<String> cmbSearchPatient;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblDoctorId;
    private packagee.PanelRound panelRound1;
    private packagee.PanelRound panelRound2;
    private javax.swing.JRadioButton rdoHospByPatientId;
    private javax.swing.JRadioButton rdoHospRequests;
    private javax.swing.JRadioButton rdoPendingAppointments;
    private javax.swing.JRadioButton rdoTotalAppointments;
    private javax.swing.JTable tblDoctorAppointments;
    private javax.swing.JTable tblHospitalizations;
    private javax.swing.JTable tblPrescriptions;
    private javax.swing.JTextArea txtCompleteDiagnosis;
    private javax.swing.JTextArea txtCompleteFollowUp;
    private javax.swing.JTextArea txtCompleteObservations;
    private javax.swing.JTextArea txtCompleteTreatment;
    private javax.swing.JTextField txtDoctorFirstname;
    private javax.swing.JTextField txtDoctorLastname;
    private javax.swing.JTextField txtDoctorLicenceNumber;
    private javax.swing.JTextField txtDoctorOffice;
    private javax.swing.JTextField txtDoctorPassword;
    private javax.swing.JTextField txtDoctorPasswordConfirm;
    private javax.swing.JTextField txtDoctorUsername;
    private javax.swing.JTextField txtHospDate;
    private javax.swing.JTextArea txtHospObservations;
    private javax.swing.JTextArea txtHospReason;
    private javax.swing.JTextField txtHospRoomType;
    private javax.swing.JTextField txtPrescribeDose;
    private javax.swing.JTextField txtPrescribeDuration;
    private javax.swing.JTextField txtPrescribeFrequency;
    private javax.swing.JTextField txtPrescribeInstructions;
    private javax.swing.JTextField txtPrescribeMedication;
    private javax.swing.JTextField txtPrescribeRoute;
    private javax.swing.JTextField txtRescheduleNewTime;
    private javax.swing.JTextField txtRescheduleReason;
    // End of variables declaration//GEN-END:variables
}
