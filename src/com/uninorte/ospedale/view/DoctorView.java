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
import com.uninorte.ospedale.model.dto.AppointmentRowDTO;
import com.uninorte.ospedale.model.dto.CompleteAppointmentDTO;
import com.uninorte.ospedale.model.dto.DoctorFormDTO;
import com.uninorte.ospedale.model.dto.HospitalizationFromAppointmentDTO;
import com.uninorte.ospedale.model.dto.HospitalizationRowDTO;
import com.uninorte.ospedale.model.dto.PatientComboDTO;
import com.uninorte.ospedale.model.dto.PatientFormDTO;
import com.uninorte.ospedale.model.dto.PrescriptionDTO;
import com.uninorte.ospedale.model.dto.PrescriptionRowDTO;
import com.uninorte.ospedale.model.dto.UserSessionDTO;
import com.uninorte.ospedale.model.observer.EntityEvent;
import com.uninorte.ospedale.model.observer.Observer;
import com.uninorte.ospedale.view.navigation.ViewNavigator;
import java.awt.Color;
import java.util.ArrayList;
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
public class DoctorView extends javax.swing.JFrame implements Observer<EntityEvent> {

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
    private List<PatientComboDTO> patientsCombo = new ArrayList<>();

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
        loadPatientsCombo();

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

        // Botón sin función activa en esta vista — ocultarlo
        btnAddMedicationRow.setVisible(false);

        refreshAppointmentsTable();
        refreshHospitalizationsTable();
    }

    private void populateCombosFromResponse(javax.swing.JComboBox<String> combo, Response<List<String>> resp) {
        combo.removeAllItems();
        combo.addItem("Select one");
        if (resp.getCode() == 200 && resp.getData() != null) {
            for (String item : resp.getData()) {
                combo.addItem(item);
            }
        }
    }

    private void loadPatientsCombo() {
        Response<List<PatientComboDTO>> resp = cc.getPatientsCombo();
        patientsCombo = resp.getCode() == 200 ? resp.getData() : new ArrayList<>();
        cmbSearchPatient.removeAllItems();
        cmbSearchPatient.addItem("Select one");
        for (PatientComboDTO p : patientsCombo) {
            cmbSearchPatient.addItem(p.toString());
        }
    }

    private void refreshAppointmentsTable() {
        boolean onlyPending = rdoPendingAppointments.isSelected();
        Response<List<AppointmentRowDTO>> resp = tc.getDoctorAppointments(session.id(), onlyPending);
        DefaultTableModel model = (DefaultTableModel) tblDoctorAppointments.getModel();
        model.setRowCount(0);
        if (resp.getCode() == 200 && resp.getData() != null) {
            for (AppointmentRowDTO r : resp.getData()) {
                model.addRow(new Object[]{r.id(), r.datetime(), r.patientFullname(), r.specialty(), r.type(), r.status()});
            }
        }
    }

    private void refreshHospitalizationsTable() {
        DefaultTableModel model = (DefaultTableModel) tblHospitalizations.getModel();
        model.setRowCount(0);
        if (rdoHospRequests.isSelected()) {
            Response<List<HospitalizationRowDTO>> resp = tc.getHospitalizationRequests();
            if (resp.getCode() == 200 && resp.getData() != null) {
                for (HospitalizationRowDTO row : resp.getData()) {
                    model.addRow(new Object[]{row.id(), row.date(), row.patientFullname(), row.doctorFullname(), row.roomType(), row.status()});
                }
            }
        } else if (rdoHospByPatientId.isSelected()) {
            int idx = cmbSearchPatient.getSelectedIndex() - 1;
            if (idx >= 0 && idx < patientsCombo.size()) {
                long pid = patientsCombo.get(idx).id();
                Response<List<HospitalizationRowDTO>> resp = tc.getHospitalizationsByPatient(pid);
                if (resp.getCode() == 200 && resp.getData() != null) {
                    for (HospitalizationRowDTO row : resp.getData()) {
                        model.addRow(new Object[]{row.id(), row.date(), row.patientFullname(), row.doctorFullname(), row.roomType(), row.status()});
                    }
                }
            }
        }
    }

    private void refreshPrescriptionsTable(String appointmentId) {
        DefaultTableModel model = (DefaultTableModel) tblPrescriptions.getModel();
        model.setRowCount(0);
        Response<List<PrescriptionRowDTO>> resp = tc.getPrescriptions(appointmentId);
        if (resp.getCode() == 200 && resp.getData() != null) {
            for (PrescriptionRowDTO row : resp.getData()) {
                model.addRow(new Object[]{row.medicationName(), row.dose(), row.administrationRoute(),
                    row.treatmentDuration(), row.additionalInstructions(), row.frequency()});
            }
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

        pnlRoot = new packagee.PanelRound();
        pnlTopbar = new packagee.PanelRound();
        btnClose = new javax.swing.JButton();
        lblDoctorId = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        tabsDoctor = new javax.swing.JTabbedPane();
        pnlAppointmentsVisualizationTab = new javax.swing.JPanel();
        rdoTotalAppointments = new javax.swing.JRadioButton();
        scrDoctorAppointments = new javax.swing.JScrollPane();
        tblDoctorAppointments = new javax.swing.JTable();
        rdoPendingAppointments = new javax.swing.JRadioButton();
        btnLogout = new javax.swing.JButton();
        pnlPatientHistoryTab = new javax.swing.JPanel();
        cmbSearchPatient = new javax.swing.JComboBox<>();
        lblSearchPatient = new javax.swing.JLabel();
        scrHospitalizations = new javax.swing.JScrollPane();
        tblHospitalizations = new javax.swing.JTable();
        btnSearchPatient = new javax.swing.JButton();
        pnlDoctorProfileTab = new javax.swing.JPanel();
        lblDoctorFirstname = new javax.swing.JLabel();
        txtDoctorFirstname = new javax.swing.JTextField();
        lblDoctorLastname = new javax.swing.JLabel();
        txtDoctorLastname = new javax.swing.JTextField();
        lblDoctorSpecialty = new javax.swing.JLabel();
        lblDoctorLicenceNumber = new javax.swing.JLabel();
        txtDoctorLicenceNumber = new javax.swing.JTextField();
        lblDoctorOffice = new javax.swing.JLabel();
        txtDoctorOffice = new javax.swing.JTextField();
        lblDoctorUsername = new javax.swing.JLabel();
        txtDoctorUsername = new javax.swing.JTextField();
        txtDoctorPassword = new javax.swing.JTextField();
        lblDoctorPassword = new javax.swing.JLabel();
        lblDoctorPasswordConfirm = new javax.swing.JLabel();
        txtDoctorPasswordConfirm = new javax.swing.JTextField();
        cmbDoctorSpecialty = new javax.swing.JComboBox<>();
        btnSaveProfile = new javax.swing.JButton();
        pnlRequestsTab = new javax.swing.JPanel();
        lblAcceptAppointmentId = new javax.swing.JLabel();
        lblAcceptSectionTitle = new javax.swing.JLabel();
        cmbAcceptAppointmentId = new javax.swing.JComboBox<>();
        sepRescheduleSection = new javax.swing.JSeparator();
        btnAcceptAppointment = new javax.swing.JButton();
        lblRescheduleSectionTitle = new javax.swing.JLabel();
        lblRescheduleAppointmentId = new javax.swing.JLabel();
        cmbRescheduleAppointmentId = new javax.swing.JComboBox<>();
        btnRescheduleAppointment = new javax.swing.JButton();
        lblRescheduleNewTime = new javax.swing.JLabel();
        txtRescheduleNewTime = new javax.swing.JTextField();
        lblRescheduleReason = new javax.swing.JLabel();
        txtRescheduleReason = new javax.swing.JTextField();
        sepCompleteSection = new javax.swing.JSeparator();
        lblCompleteSectionTitle = new javax.swing.JLabel();
        lblCompleteAppointmentId = new javax.swing.JLabel();
        cmbCompleteAppointmentId = new javax.swing.JComboBox<>();
        lblCompleteDiagnosis = new javax.swing.JLabel();
        lblCompleteObservations = new javax.swing.JLabel();
        lblCompleteTreatment = new javax.swing.JLabel();
        lblCompleteFollowUp = new javax.swing.JLabel();
        btnCompleteAppointment = new javax.swing.JButton();
        lblHospSectionTitle = new javax.swing.JLabel();
        lblHospReason = new javax.swing.JLabel();
        lblHospDate = new javax.swing.JLabel();
        txtHospDate = new javax.swing.JTextField();
        lblHospRoomType = new javax.swing.JLabel();
        txtHospRoomType = new javax.swing.JTextField();
        lblHospObservations = new javax.swing.JLabel();
        scrHospObservations = new javax.swing.JScrollPane();
        txtHospObservations = new javax.swing.JTextArea();
        btnSendToHospitalization = new javax.swing.JButton();
        cmbHospAppointmentId = new javax.swing.JComboBox<>();
        rdoHospRequests = new javax.swing.JRadioButton();
        rdoHospByPatientId = new javax.swing.JRadioButton();
        scrCompleteDiagnosis = new javax.swing.JScrollPane();
        txtCompleteDiagnosis = new javax.swing.JTextArea();
        scrCompleteObservations = new javax.swing.JScrollPane();
        txtCompleteObservations = new javax.swing.JTextArea();
        scrCompleteTreatment = new javax.swing.JScrollPane();
        txtCompleteTreatment = new javax.swing.JTextArea();
        scrCompleteFollowUp = new javax.swing.JScrollPane();
        txtCompleteFollowUp = new javax.swing.JTextArea();
        sepHospSection = new javax.swing.JSeparator();
        btnApproveHospitalization = new javax.swing.JButton();
        btnCancelHospitalization = new javax.swing.JButton();
        cmbCancelHospitalizationId = new javax.swing.JComboBox<>();
        scrPrescriptions = new javax.swing.JScrollPane();
        txtHospReason = new javax.swing.JTextArea();
        pnlPrescribeTab = new javax.swing.JPanel();
        lblPrescribeAppointmentId = new javax.swing.JLabel();
        lblPrescribeMedication = new javax.swing.JLabel();
        txtPrescribeMedication = new javax.swing.JTextField();
        lblPrescribeDose = new javax.swing.JLabel();
        txtPrescribeDose = new javax.swing.JTextField();
        lblPrescribeRoute = new javax.swing.JLabel();
        txtPrescribeRoute = new javax.swing.JTextField();
        lblPrescribeFrequency = new javax.swing.JLabel();
        txtPrescribeFrequency = new javax.swing.JTextField();
        lblPrescribeDuration = new javax.swing.JLabel();
        txtPrescribeDuration = new javax.swing.JTextField();
        lblPrescribeInstructions = new javax.swing.JLabel();
        txtPrescribeInstructions = new javax.swing.JTextField();
        scrHospReason = new javax.swing.JScrollPane();
        tblPrescriptions = new javax.swing.JTable();
        btnAddMedicationRow = new javax.swing.JButton();
        btnPrescribe = new javax.swing.JButton();
        cmbPrescribeAppointmentId = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        pnlRoot.setRadius(50);

        pnlTopbar.setRadius(50);
        pnlTopbar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                pnlTopbarMouseDragged(evt);
            }
        });
        pnlTopbar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlTopbarMousePressed(evt);
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

        javax.swing.GroupLayout pnlTopbarLayout = new javax.swing.GroupLayout(pnlTopbar);
        pnlTopbar.setLayout(pnlTopbarLayout);
        pnlTopbarLayout.setHorizontalGroup(
            pnlTopbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopbarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDoctorId)
                .addGap(32, 32, 32)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClose)
                .addGap(19, 19, 19))
        );
        pnlTopbarLayout.setVerticalGroup(
            pnlTopbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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
        scrDoctorAppointments.setViewportView(tblDoctorAppointments);

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

        javax.swing.GroupLayout pnlAppointmentsVisualizationTabLayout = new javax.swing.GroupLayout(pnlAppointmentsVisualizationTab);
        pnlAppointmentsVisualizationTab.setLayout(pnlAppointmentsVisualizationTabLayout);
        pnlAppointmentsVisualizationTabLayout.setHorizontalGroup(
            pnlAppointmentsVisualizationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentsVisualizationTabLayout.createSequentialGroup()
                .addGroup(pnlAppointmentsVisualizationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLogout)
                    .addGroup(pnlAppointmentsVisualizationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlAppointmentsVisualizationTabLayout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addComponent(rdoTotalAppointments)
                            .addGap(18, 18, 18)
                            .addComponent(rdoPendingAppointments))
                        .addGroup(pnlAppointmentsVisualizationTabLayout.createSequentialGroup()
                            .addGap(108, 108, 108)
                            .addComponent(scrDoctorAppointments, javax.swing.GroupLayout.PREFERRED_SIZE, 1035, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(152, Short.MAX_VALUE))
        );
        pnlAppointmentsVisualizationTabLayout.setVerticalGroup(
            pnlAppointmentsVisualizationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentsVisualizationTabLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(pnlAppointmentsVisualizationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoTotalAppointments)
                    .addComponent(rdoPendingAppointments))
                .addGap(18, 18, 18)
                .addComponent(scrDoctorAppointments, javax.swing.GroupLayout.PREFERRED_SIZE, 504, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addGap(23, 23, 23))
        );

        tabsDoctor.addTab("Appointments visualization", pnlAppointmentsVisualizationTab);

        cmbSearchPatient.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbSearchPatient.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        lblSearchPatient.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblSearchPatient.setText("Patient");

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
        scrHospitalizations.setViewportView(tblHospitalizations);

        btnSearchPatient.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnSearchPatient.setText("Search");
        btnSearchPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchPatientActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlPatientHistoryTabLayout = new javax.swing.GroupLayout(pnlPatientHistoryTab);
        pnlPatientHistoryTab.setLayout(pnlPatientHistoryTabLayout);
        pnlPatientHistoryTabLayout.setHorizontalGroup(
            pnlPatientHistoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientHistoryTabLayout.createSequentialGroup()
                .addGroup(pnlPatientHistoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPatientHistoryTabLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(lblSearchPatient)
                        .addGap(18, 18, 18)
                        .addComponent(cmbSearchPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPatientHistoryTabLayout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(scrHospitalizations, javax.swing.GroupLayout.PREFERRED_SIZE, 1133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(99, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPatientHistoryTabLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnSearchPatient)
                .addGap(601, 601, 601))
        );
        pnlPatientHistoryTabLayout.setVerticalGroup(
            pnlPatientHistoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientHistoryTabLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(pnlPatientHistoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearchPatient)
                    .addComponent(cmbSearchPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(scrHospitalizations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(btnSearchPatient)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        tabsDoctor.addTab("History Appointments of a patient", pnlPatientHistoryTab);

        lblDoctorFirstname.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblDoctorFirstname.setText("Firstname");

        txtDoctorFirstname.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblDoctorLastname.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblDoctorLastname.setText("Lastname");

        txtDoctorLastname.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblDoctorSpecialty.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblDoctorSpecialty.setText("Specialty");

        lblDoctorLicenceNumber.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblDoctorLicenceNumber.setText("License Number");

        txtDoctorLicenceNumber.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblDoctorOffice.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblDoctorOffice.setText("Assigned office");

        txtDoctorOffice.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblDoctorUsername.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblDoctorUsername.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDoctorUsername.setText("User");

        txtDoctorUsername.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        txtDoctorPassword.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblDoctorPassword.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblDoctorPassword.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDoctorPassword.setText("Password");

        lblDoctorPasswordConfirm.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblDoctorPasswordConfirm.setText("Password confirmation");

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

        javax.swing.GroupLayout pnlDoctorProfileTabLayout = new javax.swing.GroupLayout(pnlDoctorProfileTab);
        pnlDoctorProfileTab.setLayout(pnlDoctorProfileTabLayout);
        pnlDoctorProfileTabLayout.setHorizontalGroup(
            pnlDoctorProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDoctorProfileTabLayout.createSequentialGroup()
                .addGroup(pnlDoctorProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDoctorProfileTabLayout.createSequentialGroup()
                        .addGap(211, 211, 211)
                        .addComponent(lblDoctorFirstname)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDoctorFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblDoctorLastname)
                        .addGap(18, 18, 18)
                        .addComponent(txtDoctorLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblDoctorSpecialty)
                        .addGap(18, 18, 18)
                        .addComponent(cmbDoctorSpecialty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDoctorProfileTabLayout.createSequentialGroup()
                        .addGap(351, 351, 351)
                        .addComponent(lblDoctorLicenceNumber)
                        .addGap(18, 18, 18)
                        .addComponent(txtDoctorLicenceNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblDoctorOffice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDoctorOffice, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDoctorProfileTabLayout.createSequentialGroup()
                        .addGap(558, 558, 558)
                        .addGroup(pnlDoctorProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDoctorPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlDoctorProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtDoctorUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                .addComponent(lblDoctorUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblDoctorPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(pnlDoctorProfileTabLayout.createSequentialGroup()
                        .addGap(521, 521, 521)
                        .addComponent(lblDoctorPasswordConfirm))
                    .addGroup(pnlDoctorProfileTabLayout.createSequentialGroup()
                        .addGap(576, 576, 576)
                        .addComponent(btnSaveProfile))
                    .addGroup(pnlDoctorProfileTabLayout.createSequentialGroup()
                        .addGap(561, 561, 561)
                        .addComponent(txtDoctorPasswordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(269, Short.MAX_VALUE))
        );
        pnlDoctorProfileTabLayout.setVerticalGroup(
            pnlDoctorProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDoctorProfileTabLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(pnlDoctorProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDoctorFirstname)
                    .addComponent(txtDoctorFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDoctorLastname)
                    .addComponent(txtDoctorLastname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDoctorSpecialty)
                    .addComponent(cmbDoctorSpecialty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlDoctorProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDoctorLicenceNumber)
                    .addComponent(txtDoctorLicenceNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDoctorOffice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDoctorOffice))
                .addGap(30, 30, 30)
                .addComponent(lblDoctorUsername)
                .addGap(18, 18, 18)
                .addComponent(txtDoctorUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblDoctorPassword)
                .addGap(27, 27, 27)
                .addComponent(txtDoctorPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblDoctorPasswordConfirm)
                .addGap(18, 18, 18)
                .addComponent(txtDoctorPasswordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnSaveProfile)
                .addContainerGap(161, Short.MAX_VALUE))
        );

        tabsDoctor.addTab("Modify info", pnlDoctorProfileTab);

        lblAcceptAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblAcceptAppointmentId.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAcceptAppointmentId.setText("Appointment ID");

        lblAcceptSectionTitle.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblAcceptSectionTitle.setText("Accept medical appointment");

        cmbAcceptAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbAcceptAppointmentId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        sepRescheduleSection.setOrientation(javax.swing.SwingConstants.VERTICAL);

        btnAcceptAppointment.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnAcceptAppointment.setText("Accept");
        btnAcceptAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcceptAppointmentActionPerformed(evt);
            }
        });

        lblRescheduleSectionTitle.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblRescheduleSectionTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRescheduleSectionTitle.setText("Reschedule medical appointment");

        lblRescheduleAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblRescheduleAppointmentId.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRescheduleAppointmentId.setText("Appointment");

        cmbRescheduleAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbRescheduleAppointmentId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        btnRescheduleAppointment.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnRescheduleAppointment.setText("Accept");
        btnRescheduleAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRescheduleAppointmentActionPerformed(evt);
            }
        });

        lblRescheduleNewTime.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblRescheduleNewTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRescheduleNewTime.setText("New time appointment");

        txtRescheduleNewTime.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblRescheduleReason.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblRescheduleReason.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRescheduleReason.setText("Reason for appointment");

        txtRescheduleReason.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        sepCompleteSection.setOrientation(javax.swing.SwingConstants.VERTICAL);

        lblCompleteSectionTitle.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblCompleteSectionTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCompleteSectionTitle.setText("Complete medical appointment");

        lblCompleteAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblCompleteAppointmentId.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCompleteAppointmentId.setText("Appointment");

        cmbCompleteAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbCompleteAppointmentId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        lblCompleteDiagnosis.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblCompleteDiagnosis.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCompleteDiagnosis.setText("Diagnosis");

        lblCompleteObservations.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblCompleteObservations.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCompleteObservations.setText("Observations");

        lblCompleteTreatment.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblCompleteTreatment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCompleteTreatment.setText("Recommended treatment");

        lblCompleteFollowUp.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblCompleteFollowUp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCompleteFollowUp.setText("Follow-up indication");

        btnCompleteAppointment.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCompleteAppointment.setText("Complete");
        btnCompleteAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompleteAppointmentActionPerformed(evt);
            }
        });

        lblHospSectionTitle.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblHospSectionTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHospSectionTitle.setText("Hospitalization");

        lblHospReason.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblHospReason.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHospReason.setText("Reason for hospitalization");

        lblHospDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblHospDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHospDate.setText("Date of entry");

        txtHospDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblHospRoomType.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblHospRoomType.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHospRoomType.setText("Room type");

        txtHospRoomType.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblHospObservations.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblHospObservations.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHospObservations.setText("Observations");

        txtHospObservations.setColumns(20);
        txtHospObservations.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtHospObservations.setRows(5);
        scrHospObservations.setViewportView(txtHospObservations);

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
        scrCompleteDiagnosis.setViewportView(txtCompleteDiagnosis);

        txtCompleteObservations.setColumns(20);
        txtCompleteObservations.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtCompleteObservations.setRows(5);
        scrCompleteObservations.setViewportView(txtCompleteObservations);

        txtCompleteTreatment.setColumns(20);
        txtCompleteTreatment.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtCompleteTreatment.setRows(5);
        scrCompleteTreatment.setViewportView(txtCompleteTreatment);

        txtCompleteFollowUp.setColumns(20);
        txtCompleteFollowUp.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtCompleteFollowUp.setRows(5);
        scrCompleteFollowUp.setViewportView(txtCompleteFollowUp);

        sepHospSection.setOrientation(javax.swing.SwingConstants.VERTICAL);

        btnApproveHospitalization.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnApproveHospitalization.setText("Approve");
        btnApproveHospitalization.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApproveHospitalizationActionPerformed(evt);
            }
        });

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
        scrPrescriptions.setViewportView(txtHospReason);

        javax.swing.GroupLayout pnlRequestsTabLayout = new javax.swing.GroupLayout(pnlRequestsTab);
        pnlRequestsTab.setLayout(pnlRequestsTabLayout);
        pnlRequestsTabLayout.setHorizontalGroup(
            pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRequestsTabLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRequestsTabLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRequestsTabLayout.createSequentialGroup()
                                        .addComponent(btnAcceptAppointment)
                                        .addGap(87, 87, 87))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRequestsTabLayout.createSequentialGroup()
                                        .addComponent(cmbAcceptAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(67, 67, 67))))
                            .addComponent(lblAcceptAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(sepRescheduleSection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1))
                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(lblAcceptSectionTitle)
                        .addGap(22, 22, 22)))
                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblRescheduleSectionTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblRescheduleAppointmentId, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRescheduleNewTime, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRescheduleReason, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                            .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                    .addGap(90, 90, 90)
                                    .addComponent(cmbRescheduleAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                    .addGap(99, 99, 99)
                                    .addComponent(txtRescheduleNewTime, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                    .addGap(98, 98, 98)
                                    .addComponent(txtRescheduleReason, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                    .addGap(112, 112, 112)
                                    .addComponent(btnRescheduleAppointment)))
                            .addGap(91, 91, 91))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sepCompleteSection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(btnCompleteAppointment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                        .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRequestsTabLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lblCompleteAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblCompleteSectionTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                        .addGap(99, 99, 99)
                                        .addComponent(cmbCompleteAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 25, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRequestsTabLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCompleteDiagnosis, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblCompleteObservations, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblCompleteFollowUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblCompleteTreatment, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                        .addGap(42, 42, 42)
                                        .addComponent(scrCompleteDiagnosis, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                        .addGap(41, 41, 41)
                                        .addComponent(scrCompleteObservations, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                        .addGap(42, 42, 42)
                                        .addComponent(scrCompleteTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                        .addGap(43, 43, 43)
                                        .addComponent(scrCompleteFollowUp, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(sepHospSection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                        .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblHospSectionTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblHospDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblHospRoomType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblHospObservations, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                .addGap(121, 121, 121)
                                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtHospDate, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtHospRoomType, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                .addComponent(btnApproveHospitalization)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancelHospitalization)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSendToHospitalization))
                            .addComponent(scrHospObservations, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(56, Short.MAX_VALUE))
                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                        .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(cmbHospAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(rdoHospRequests)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRequestsTabLayout.createSequentialGroup()
                                .addComponent(rdoHospByPatientId, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRequestsTabLayout.createSequentialGroup()
                                .addComponent(cmbCancelHospitalizationId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29))))
                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblHospReason, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRequestsTabLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(scrPrescriptions, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47))))
        );
        pnlRequestsTabLayout.setVerticalGroup(
            pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sepRescheduleSection)
            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sepCompleteSection)
                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(lblCompleteSectionTitle)
                        .addGap(10, 10, 10)
                        .addComponent(lblCompleteAppointmentId)
                        .addGap(18, 18, 18)
                        .addComponent(cmbCompleteAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblCompleteDiagnosis)
                        .addGap(18, 18, 18)
                        .addComponent(scrCompleteDiagnosis, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblCompleteObservations)
                        .addGap(18, 18, 18)
                        .addComponent(scrCompleteObservations, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblCompleteTreatment)
                        .addGap(18, 18, 18)
                        .addComponent(scrCompleteTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblCompleteFollowUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrCompleteFollowUp, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCompleteAppointment)
                        .addGap(12, 12, 12))
                    .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                        .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(lblAcceptSectionTitle)
                                .addGap(18, 18, 18)
                                .addComponent(lblAcceptAppointmentId)
                                .addGap(18, 18, 18)
                                .addComponent(cmbAcceptAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(btnAcceptAppointment))
                            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(lblRescheduleSectionTitle)
                                .addGap(18, 18, 18)
                                .addComponent(lblRescheduleAppointmentId)
                                .addGap(18, 18, 18)
                                .addComponent(cmbRescheduleAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblRescheduleNewTime)
                                .addGap(18, 18, 18)
                                .addComponent(txtRescheduleNewTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblRescheduleReason)
                                .addGap(18, 18, 18)
                                .addComponent(txtRescheduleReason, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(btnRescheduleAppointment)))
                        .addGap(18, 18, Short.MAX_VALUE))))
            .addGroup(pnlRequestsTabLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblHospSectionTitle)
                .addGap(18, 18, 18)
                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoHospRequests)
                    .addComponent(rdoHospByPatientId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbHospAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbCancelHospitalizationId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblHospReason)
                .addGap(16, 16, 16)
                .addComponent(scrPrescriptions, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblHospDate)
                .addGap(18, 18, 18)
                .addComponent(txtHospDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblHospRoomType)
                .addGap(18, 18, 18)
                .addComponent(txtHospRoomType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblHospObservations)
                .addGap(18, 18, 18)
                .addComponent(scrHospObservations, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlRequestsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnApproveHospitalization)
                    .addComponent(btnSendToHospitalization)
                    .addComponent(btnCancelHospitalization))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(sepHospSection, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        tabsDoctor.addTab("Request/Appointments", pnlRequestsTab);

        lblPrescribeAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPrescribeAppointmentId.setText("Appointment ID");

        lblPrescribeMedication.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPrescribeMedication.setText("Medication name");

        txtPrescribeMedication.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblPrescribeDose.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPrescribeDose.setText("Dose");

        txtPrescribeDose.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblPrescribeRoute.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPrescribeRoute.setText("Administration route");

        txtPrescribeRoute.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblPrescribeFrequency.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPrescribeFrequency.setText("Frecuency");

        txtPrescribeFrequency.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblPrescribeDuration.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPrescribeDuration.setText("Treatment duration");

        txtPrescribeDuration.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblPrescribeInstructions.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPrescribeInstructions.setText("Additional instructions");

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
        scrHospReason.setViewportView(tblPrescriptions);

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

        javax.swing.GroupLayout pnlPrescribeTabLayout = new javax.swing.GroupLayout(pnlPrescribeTab);
        pnlPrescribeTab.setLayout(pnlPrescribeTabLayout);
        pnlPrescribeTabLayout.setHorizontalGroup(
            pnlPrescribeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrescribeTabLayout.createSequentialGroup()
                .addGroup(pnlPrescribeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPrescribeTabLayout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addGroup(pnlPrescribeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(scrHospReason, javax.swing.GroupLayout.PREFERRED_SIZE, 1125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlPrescribeTabLayout.createSequentialGroup()
                                .addGroup(pnlPrescribeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlPrescribeTabLayout.createSequentialGroup()
                                        .addComponent(lblPrescribeAppointmentId)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cmbPrescribeAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(9, 9, 9)
                                        .addComponent(lblPrescribeMedication))
                                    .addGroup(pnlPrescribeTabLayout.createSequentialGroup()
                                        .addComponent(lblPrescribeDuration)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtPrescribeDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlPrescribeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(pnlPrescribeTabLayout.createSequentialGroup()
                                        .addComponent(lblPrescribeInstructions)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtPrescribeInstructions, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblPrescribeFrequency)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtPrescribeFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(pnlPrescribeTabLayout.createSequentialGroup()
                                        .addComponent(txtPrescribeMedication, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblPrescribeDose)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtPrescribeDose, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblPrescribeRoute)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtPrescribeRoute, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAddMedicationRow))))
                    .addGroup(pnlPrescribeTabLayout.createSequentialGroup()
                        .addGap(583, 583, 583)
                        .addComponent(btnPrescribe)))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        pnlPrescribeTabLayout.setVerticalGroup(
            pnlPrescribeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrescribeTabLayout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(pnlPrescribeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrescribeAppointmentId)
                    .addComponent(lblPrescribeMedication)
                    .addComponent(txtPrescribeMedication, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPrescribeDose)
                    .addComponent(txtPrescribeDose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPrescribeRoute)
                    .addComponent(txtPrescribeRoute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddMedicationRow)
                    .addComponent(cmbPrescribeAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlPrescribeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrescribeDuration)
                    .addComponent(txtPrescribeDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPrescribeInstructions)
                    .addComponent(txtPrescribeInstructions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPrescribeFrequency)
                    .addComponent(txtPrescribeFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(scrHospReason, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(btnPrescribe)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        tabsDoctor.addTab("Prescribe medications", pnlPrescribeTab);

        javax.swing.GroupLayout pnlRootLayout = new javax.swing.GroupLayout(pnlRoot);
        pnlRoot.setLayout(pnlRootLayout);
        pnlRootLayout.setHorizontalGroup(
            pnlRootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRootLayout.createSequentialGroup()
                .addGroup(pnlRootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlTopbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabsDoctor))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlRootLayout.setVerticalGroup(
            pnlRootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRootLayout.createSequentialGroup()
                .addComponent(pnlTopbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabsDoctor))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlRoot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlRoot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pnlTopbarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_panelRound2MousePressed

    private void pnlTopbarMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseDragged
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
        int idx = cmbSearchPatient.getSelectedIndex() - 1;
        if (idx < 0 || idx >= patientsCombo.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente.");
            return;
        }
        long pid = patientsCombo.get(idx).id();
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
        if (resp.getCode() == 200) {
            txtDoctorPassword.setText("");
            txtDoctorPasswordConfirm.setText("");
        }
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

    private void btnApproveHospitalizationActionPerformed(java.awt.event.ActionEvent evt) {
        Object sel = cmbCancelHospitalizationId.getSelectedItem();
        if (sel == null || "Select one".equals(sel.toString())) {
            JOptionPane.showMessageDialog(this, "Seleccione una hospitalización.");
            return;
        }
        Response<Object> resp = hc.approve(sel.toString(), session.id());
        JOptionPane.showMessageDialog(this, resp.getMessage());
        if (resp.getCode() == 200) {
            populateCombosFromResponse(cmbCancelHospitalizationId, cc.getHospitalizationIdsByStatus("REQUESTED"));
            refreshHospitalizationsTable();
        }
    }

    @Override
    public void onNotify(EntityEvent event) {
        if (!isDisplayable()) return;
        javax.swing.SwingUtilities.invokeLater(() -> {
            refreshAppointmentsTable();
            refreshHospitalizationsTable();
        });
    }

    private void btnAddMedicationRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMedicationRowActionPerformed
        JOptionPane.showMessageDialog(this,
                "Función no disponible — agregue las prescripciones de a una usando el botón Prescribe.");
    }//GEN-LAST:event_btnAddMedicationRowActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAcceptAppointment;
    private javax.swing.JButton btnAddMedicationRow;
    private javax.swing.JButton btnApproveHospitalization;
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
    private javax.swing.JLabel lblDoctorPassword;
    private javax.swing.JLabel lblDoctorPasswordConfirm;
    private javax.swing.JLabel lblAcceptSectionTitle;
    private javax.swing.JLabel lblAcceptAppointmentId;
    private javax.swing.JLabel lblRescheduleSectionTitle;
    private javax.swing.JLabel lblRescheduleAppointmentId;
    private javax.swing.JLabel lblRescheduleNewTime;
    private javax.swing.JLabel lblRescheduleReason;
    private javax.swing.JLabel lblCompleteSectionTitle;
    private javax.swing.JLabel lblDoctorFirstname;
    private javax.swing.JLabel lblCompleteAppointmentId;
    private javax.swing.JLabel lblCompleteDiagnosis;
    private javax.swing.JLabel lblCompleteObservations;
    private javax.swing.JLabel lblCompleteTreatment;
    private javax.swing.JLabel lblCompleteFollowUp;
    private javax.swing.JLabel lblHospSectionTitle;
    private javax.swing.JLabel lblHospReason;
    private javax.swing.JLabel lblHospDate;
    private javax.swing.JLabel lblHospRoomType;
    private javax.swing.JLabel lblDoctorLastname;
    private javax.swing.JLabel lblHospObservations;
    private javax.swing.JLabel lblPrescribeAppointmentId;
    private javax.swing.JLabel lblPrescribeMedication;
    private javax.swing.JLabel lblPrescribeDose;
    private javax.swing.JLabel lblPrescribeRoute;
    private javax.swing.JLabel lblPrescribeFrequency;
    private javax.swing.JLabel lblPrescribeDuration;
    private javax.swing.JLabel lblPrescribeInstructions;
    private javax.swing.JLabel lblSearchPatient;
    private javax.swing.JLabel lblDoctorSpecialty;
    private javax.swing.JLabel lblDoctorLicenceNumber;
    private javax.swing.JLabel lblDoctorOffice;
    private javax.swing.JLabel lblDoctorUsername;
    private javax.swing.JPanel pnlRequestsTab;
    private javax.swing.JPanel pnlPrescribeTab;
    private javax.swing.JPanel pnlDoctorProfileTab;
    private javax.swing.JPanel pnlAppointmentsVisualizationTab;
    private javax.swing.JPanel pnlPatientHistoryTab;
    private javax.swing.JScrollPane scrHospObservations;
    private javax.swing.JScrollPane scrPrescriptions;
    private javax.swing.JScrollPane scrHospReason;
    private javax.swing.JScrollPane scrDoctorAppointments;
    private javax.swing.JScrollPane scrHospitalizations;
    private javax.swing.JScrollPane scrCompleteDiagnosis;
    private javax.swing.JScrollPane scrCompleteObservations;
    private javax.swing.JScrollPane scrCompleteTreatment;
    private javax.swing.JScrollPane scrCompleteFollowUp;
    private javax.swing.JSeparator sepRescheduleSection;
    private javax.swing.JSeparator sepCompleteSection;
    private javax.swing.JSeparator sepHospSection;
    private javax.swing.JTabbedPane tabsDoctor;
    private javax.swing.JLabel lblDoctorId;
    private packagee.PanelRound pnlRoot;
    private packagee.PanelRound pnlTopbar;
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
