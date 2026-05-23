/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.uninorte.ospedale.view;

import com.uninorte.ospedale.controller.AppointmentController;
import com.uninorte.ospedale.controller.ComboDataController;
import com.uninorte.ospedale.controller.HospitalizationController;
import com.uninorte.ospedale.controller.PatientController;
import com.uninorte.ospedale.controller.TableDataController;
import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.model.dto.AppointmentRequestDTO;
import com.uninorte.ospedale.model.dto.AppointmentRowDTO;
import com.uninorte.ospedale.model.dto.DoctorComboDTO;
import com.uninorte.ospedale.model.dto.HospitalizationRequestDTO;
import com.uninorte.ospedale.model.dto.PatientFormDTO;
import com.uninorte.ospedale.model.dto.UserSessionDTO;
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
 */
public class PatientView extends javax.swing.JFrame {

    private int x, y;

    private final UserSessionDTO session;
    private final ViewNavigator navigator;
    private final PatientController pc;
    private final AppointmentController ac;
    private final HospitalizationController hc;
    private final TableDataController tc;
    private final ComboDataController cc;
    private final boolean openedByAdmin;

    // Doctor cache — index-based lookup avoids parsing in the view
    private List<DoctorComboDTO> apptDoctors = new ArrayList<>();
    private List<DoctorComboDTO> hospDoctors = new ArrayList<>();
    private List<String> cancelableApptIds = new ArrayList<>();
    private boolean apptByDoctor = false;

    private boolean initializing = true;

    public PatientView(UserSessionDTO session, ViewNavigator navigator,
            PatientController pc, AppointmentController ac, HospitalizationController hc,
            TableDataController tc, ComboDataController cc, boolean openedByAdmin) {
        initComponents();
        this.session = session;
        this.navigator = navigator;
        this.pc = pc;
        this.ac = ac;
        this.hc = hc;
        this.tc = tc;
        this.cc = cc;
        this.openedByAdmin = openedByAdmin;

        btnBack.setVisible(openedByAdmin);

        // Autopoblar profile
        Response<PatientFormDTO> profileResp = pc.getProfile(session.id());
        if (profileResp.code == 200) {
            PatientFormDTO p = profileResp.data;
            txtProfileFirstname.setText(p.firstname() != null ? p.firstname() : "");
            txtProfileLastname.setText(p.lastname() != null ? p.lastname() : "");
            txtProfileBirthdate.setText(p.birthdate() != null ? p.birthdate() : "");
            txtProfileEmail.setText(p.email() != null ? p.email() : "");
            txtProfilePhone.setText(p.phone() != null ? p.phone() : "");
            txtProfileAddress.setText(p.address() != null ? p.address() : "");
            txtProfileUsername.setText(p.username() != null ? p.username() : "");
            if (p.gender() != null) {
                cmbProfileGender.setSelectedItem(p.gender());
            }
        }
        jLabel1.setText("PATIENT VIEW — ID: " + session.id());

        // Poblar cmbAppointmentType
        cmbAppointmentType.removeAllItems();
        cmbAppointmentType.addItem("In person");
        cmbAppointmentType.addItem("Remote");

        // Poblar cmbProfileGender
        cmbProfileGender.removeAllItems();
        cmbProfileGender.addItem("Female");
        cmbProfileGender.addItem("Male");
        cmbProfileGender.addItem("Select one");
        if (profileResp.code == 200 && profileResp.data.gender() != null) {
            cmbProfileGender.setSelectedItem(profileResp.data.gender());
        }

        // Poblar cmbHospRoomType
        cmbHospRoomType.removeAllItems();
        @SuppressWarnings("unchecked")
        List<String> roomTypes = (List<String>) cc.getRoomTypes().data;
        if (roomTypes != null) {
            for (String rt : roomTypes) {
                cmbHospRoomType.addItem(rt);
            }
        }

        // Poblar cmbHospDoctor
        hospDoctors = cc.getDoctorsCombo();
        cmbHospDoctor.removeAllItems();
        for (DoctorComboDTO doc : hospDoctors) {
            cmbHospDoctor.addItem(doc.toString());
        }

        // Poblar cmbAppointmentDoctor con todos los doctores (estado inicial byDoctor)
        apptDoctors = cc.getDoctorsCombo();
        cmbAppointmentDoctor.removeAllItems();
        for (DoctorComboDTO doc : apptDoctors) {
            cmbAppointmentDoctor.addItem(doc.toString());
        }

        // Poblar combo de citas cancelables
        refreshCancelCombo();

        // Cargar tabla de citas
        refreshAppointmentsTable();

        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(null);
        initializing = false;
    }

    private void refreshAppointmentsTable() {
        List<AppointmentRowDTO> rows = tc.getPatientAppointments(session.id());
        DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();
        model.setRowCount(0);
        for (AppointmentRowDTO r : rows) {
            model.addRow(new Object[]{r.id(), r.datetime(), r.doctorFullname(), r.specialty(), r.type(), r.status()});
        }
    }

    private void refreshCancelCombo() {
        cancelableApptIds = cc.getAppointmentIdsByPatientAndCancelable(session.id());
        cmbCancelAppointmentId.removeAllItems();
        for (String id : cancelableApptIds) {
            cmbCancelAppointmentId.addItem(id);
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
        jLabel1 = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAppointments = new javax.swing.JTable();
        btnRefreshAppointments = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtProfileFirstname = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtProfileLastname = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtProfileBirthdate = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtProfileEmail = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtProfilePhone = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtProfileAddress = new javax.swing.JTextField();
        txtProfilePassword = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtProfilePasswordConfirm = new javax.swing.JTextField();
        btnSaveProfile = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtProfileUsername = new javax.swing.JTextField();
        cmbProfileGender = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        rdoBySpecialty = new javax.swing.JRadioButton();
        rdoByDoctor = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        txtAppointmentDate = new javax.swing.JTextField();
        txtAppointmentTime = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        cmbAppointmentType = new javax.swing.JComboBox<>();
        btnCreateAppointment = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        cmbHospDoctor = new javax.swing.JComboBox<>();
        txtHospDate = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        cmbHospRoomType = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtHospObservations = new javax.swing.JTextArea();
        btnCreateHospitalization = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        btnCancelAppointment = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtHospReason = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtAppointmentReason = new javax.swing.JTextArea();
        cmbCancelAppointmentId = new javax.swing.JComboBox<>();
        cmbAppointmentDoctor = new javax.swing.JComboBox<>();

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

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N
        jLabel1.setText("PATIENT VIEW");

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
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addGap(29, 29, 29)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClose)
                .addGap(19, 19, 19))
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnClose))
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBack)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tblAppointments.setAutoCreateRowSorter(true);
        tblAppointments.setModel(new javax.swing.table.DefaultTableModel(
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
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblAppointments);

        btnRefreshAppointments.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnRefreshAppointments.setText("Refresh");
        btnRefreshAppointments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshAppointmentsActionPerformed(evt);
            }
        });

        btnLogout.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(602, 602, 602)
                .addComponent(btnRefreshAppointments)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addGap(78, 78, 78))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefreshAppointments)
                    .addComponent(btnLogout))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Appointment history", jPanel3);

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel2.setText("Firstname");

        txtProfileFirstname.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel3.setText("Lastname");

        txtProfileLastname.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel5.setText("Birthdate");

        txtProfileBirthdate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel6.setText("Gender");

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel7.setText("Email");

        txtProfileEmail.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel8.setText("Phone");

        txtProfilePhone.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel9.setText("Address");

        txtProfileAddress.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        txtProfilePassword.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel10.setText("Password");

        jLabel11.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel11.setText("Password confirmation");

        txtProfilePasswordConfirm.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        btnSaveProfile.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnSaveProfile.setText("Save");
        btnSaveProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveProfileActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel12.setText("User");

        txtProfileUsername.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        cmbProfileGender.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbProfileGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one", "Female", "Male" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtProfileFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(txtProfilePhone, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(txtProfileAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtProfileLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtProfileBirthdate, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbProfileGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(txtProfileEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(141, 141, 141))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(516, 516, 516)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(btnSaveProfile))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(txtProfilePasswordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel11)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jLabel10))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtProfileUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(39, 39, 39)
                                    .addComponent(jLabel12)))
                            .addComponent(txtProfilePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtProfileFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtProfileLastname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtProfileBirthdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(txtProfileEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbProfileGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtProfilePhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtProfileAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(66, 66, 66)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(txtProfileUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(txtProfilePassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(txtProfilePasswordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(btnSaveProfile)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Modify info", jPanel1);

        jLabel13.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel13.setText("Request medical appointment");

        rdoBySpecialty.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rdoBySpecialty.setText("Specialty");
        rdoBySpecialty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoBySpecialtyActionPerformed(evt);
            }
        });

        rdoByDoctor.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rdoByDoctor.setText("Doctor");
        rdoByDoctor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoByDoctorActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel14.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel14.setText("Appointment date");

        txtAppointmentDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        txtAppointmentTime.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel15.setText("Appointment time");

        jLabel16.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel16.setText("Appointment type");

        jLabel17.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel17.setText("Appointment reason");

        cmbAppointmentType.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbAppointmentType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one", "Remote", "In-person" }));

        btnCreateAppointment.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCreateAppointment.setText("Create");
        btnCreateAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateAppointmentActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel18.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Request hospitalization");

        jLabel19.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Hospitalization reason");

        jLabel20.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Attending doctor");

        cmbHospDoctor.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbHospDoctor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        txtHospDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel21.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Estimated date of admission");
        jLabel21.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel22.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Desired room type");

        cmbHospRoomType.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbHospRoomType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        jLabel23.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Observations");

        txtHospObservations.setColumns(20);
        txtHospObservations.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtHospObservations.setRows(5);
        jScrollPane1.setViewportView(txtHospObservations);

        btnCreateHospitalization.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCreateHospitalization.setText("Create");
        btnCreateHospitalization.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateHospitalizationActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel24.setText("Cancel appointment");

        jLabel25.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel25.setText("ID appointment");

        jLabel26.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel26.setText("Observations");

        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        btnCancelAppointment.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCancelAppointment.setText("Cancel");
        btnCancelAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelAppointmentActionPerformed(evt);
            }
        });

        txtHospReason.setColumns(20);
        txtHospReason.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtHospReason.setRows(5);
        jScrollPane4.setViewportView(txtHospReason);

        txtAppointmentReason.setColumns(20);
        txtAppointmentReason.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtAppointmentReason.setRows(5);
        jScrollPane5.setViewportView(txtAppointmentReason);

        cmbCancelAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbCancelAppointmentId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        cmbAppointmentDoctor.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbAppointmentDoctor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(44, 44, 44)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(rdoBySpecialty)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(rdoByDoctor))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(63, 63, 63)
                                    .addComponent(txtAppointmentDate, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(47, 47, 47)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel15)
                                        .addComponent(jLabel14)
                                        .addComponent(cmbAppointmentDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(63, 63, 63)
                                    .addComponent(txtAppointmentTime, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(38, 38, 38)
                                    .addComponent(jLabel17))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(46, 46, 46)
                                    .addComponent(jLabel16))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(55, 55, 55)
                                    .addComponent(cmbAppointmentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(42, 42, 42)
                            .addComponent(jLabel13)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(btnCreateAppointment)))
                .addGap(69, 69, 69)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(211, 211, 211)
                            .addComponent(btnCreateHospitalization))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(127, 127, 127)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                                .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addGap(127, 127, 127)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(190, 190, 190)
                        .addComponent(cmbHospDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(200, 200, 200)
                        .addComponent(txtHospDate, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(191, 191, 191)
                        .addComponent(cmbHospRoomType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel24))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(77, 77, 77)
                                .addComponent(btnCancelAppointment))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cmbCancelAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel25)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(jLabel26)))
                        .addGap(49, 49, 49)))
                .addGap(81, 81, 81))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel20)
                        .addGap(18, 18, 18)
                        .addComponent(cmbHospDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtHospDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel22)
                        .addGap(18, 18, 18)
                        .addComponent(cmbHospRoomType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCreateHospitalization)
                        .addGap(15, 15, 15))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(rdoBySpecialty)
                                    .addComponent(rdoByDoctor))
                                .addGap(18, 18, 18)
                                .addComponent(cmbAppointmentDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtAppointmentDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(txtAppointmentTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addGap(24, 24, 24)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(39, 39, 39)
                                .addComponent(jLabel25)
                                .addGap(18, 18, 18)
                                .addComponent(cmbCancelAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel26)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(56, 56, 56)
                                .addComponent(btnCancelAppointment)))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(cmbAppointmentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(btnCreateAppointment)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("Request/Cancel", jPanel2);

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRound2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
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

    private void btnRefreshAppointmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshAppointmentsActionPerformed
        refreshAppointmentsTable();
    }//GEN-LAST:event_btnRefreshAppointmentsActionPerformed

    private void btnSaveProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveProfileActionPerformed
        String firstname = txtProfileFirstname.getText().trim();
        String lastname = txtProfileLastname.getText().trim();
        String birthdate = txtProfileBirthdate.getText().trim();
        String email = txtProfileEmail.getText().trim();
        String phone = txtProfilePhone.getText().trim();
        String address = txtProfileAddress.getText().trim();
        String username = txtProfileUsername.getText().trim();
        String password = txtProfilePassword.getText();
        String passwordConfirm = txtProfilePasswordConfirm.getText();
        String gender = (String) cmbProfileGender.getSelectedItem();
        PatientFormDTO dto = new PatientFormDTO(
                String.valueOf(session.id()),
                username, firstname, lastname, password, passwordConfirm,
                email, birthdate, gender, phone, address
        );
        Response<?> r = pc.update(session.id(), dto);
        JOptionPane.showMessageDialog(this, r.message);
    }//GEN-LAST:event_btnSaveProfileActionPerformed

    private void rdoBySpecialtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoBySpecialtyActionPerformed
        if (initializing) return;
        rdoByDoctor.setSelected(false);
        apptByDoctor = false;
        @SuppressWarnings("unchecked")
        List<String> specs = (List<String>) cc.getSpecialties().data;
        apptDoctors.clear();
        cmbAppointmentDoctor.removeAllItems();
        if (specs != null) {
            for (String s : specs) {
                cmbAppointmentDoctor.addItem(s);
            }
        }
    }//GEN-LAST:event_rdoBySpecialtyActionPerformed

    private void rdoByDoctorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoByDoctorActionPerformed
        if (initializing) return;
        rdoBySpecialty.setSelected(false);
        apptByDoctor = true;
        apptDoctors = cc.getDoctorsCombo();
        cmbAppointmentDoctor.removeAllItems();
        for (DoctorComboDTO doc : apptDoctors) {
            cmbAppointmentDoctor.addItem(doc.toString());
        }
    }//GEN-LAST:event_rdoByDoctorActionPerformed

    private void btnCreateAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateAppointmentActionPerformed
        String date = txtAppointmentDate.getText().trim();
        String time = txtAppointmentTime.getText().trim();
        String reason = txtAppointmentReason.getText().trim();
        String apptTypeStr = (String) cmbAppointmentType.getSelectedItem();
        String typeCode = "In person".equals(apptTypeStr) ? "IN_PERSON" : "REMOTE";

        AppointmentRequestDTO dto;
        if (rdoByDoctor.isSelected() && !apptDoctors.isEmpty()) {
            int idx = cmbAppointmentDoctor.getSelectedIndex();
            if (idx < 0 || idx >= apptDoctors.size()) {
                JOptionPane.showMessageDialog(this, "Seleccione un médico");
                return;
            }
            long doctorId = apptDoctors.get(idx).id();
            dto = new AppointmentRequestDTO(session.id(), "DOCTOR", doctorId, null, date, time, typeCode, reason);
        } else {
            String specialty = (String) cmbAppointmentDoctor.getSelectedItem();
            if (specialty == null || specialty.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione una especialidad");
                return;
            }
            dto = new AppointmentRequestDTO(session.id(), "SPECIALTY", null, specialty, date, time, typeCode, reason);
        }
        Response<Object> r = ac.request(dto);
        JOptionPane.showMessageDialog(this, r.message);
        if (r.code == 200) {
            txtAppointmentDate.setText("");
            txtAppointmentTime.setText("");
            txtAppointmentReason.setText("");
            refreshAppointmentsTable();
            refreshCancelCombo();
        }
    }//GEN-LAST:event_btnCreateAppointmentActionPerformed

    private void btnCreateHospitalizationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateHospitalizationActionPerformed
        int idx = cmbHospDoctor.getSelectedIndex();
        if (idx < 0 || idx >= hospDoctors.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un médico");
            return;
        }
        long doctorId = hospDoctors.get(idx).id();
        String date = txtHospDate.getText().trim();
        String roomType = (String) cmbHospRoomType.getSelectedItem();
        String reason = txtHospReason.getText().trim();
        String observations = txtHospObservations.getText().trim();
        HospitalizationRequestDTO dto = new HospitalizationRequestDTO(
                session.id(), doctorId, date, roomType, reason, observations);
        Response<Object> r = hc.request(dto);
        JOptionPane.showMessageDialog(this, r.message);
        if (r.code == 200) {
            txtHospDate.setText("");
            txtHospReason.setText("");
            txtHospObservations.setText("");
        }
    }//GEN-LAST:event_btnCreateHospitalizationActionPerformed

    private void btnCancelAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelAppointmentActionPerformed
        String id = (String) cmbCancelAppointmentId.getSelectedItem();
        if (id == null || id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay citas cancelables");
            return;
        }
        Response<Object> r = ac.cancel(id, session.id());
        JOptionPane.showMessageDialog(this, r.message);
        if (r.code == 200) {
            refreshAppointmentsTable();
            refreshCancelCombo();
        }
    }//GEN-LAST:event_btnCancelAppointmentActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCancelAppointment;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnCreateAppointment;
    private javax.swing.JButton btnCreateHospitalization;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnRefreshAppointments;
    private javax.swing.JButton btnSaveProfile;
    private javax.swing.JComboBox<String> cmbAppointmentDoctor;
    private javax.swing.JComboBox<String> cmbAppointmentType;
    private javax.swing.JComboBox<String> cmbCancelAppointmentId;
    private javax.swing.JComboBox<String> cmbHospDoctor;
    private javax.swing.JComboBox<String> cmbHospRoomType;
    private javax.swing.JComboBox<String> cmbProfileGender;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton rdoByDoctor;
    private javax.swing.JRadioButton rdoBySpecialty;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblAppointments;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea txtAppointmentReason;
    private javax.swing.JTextArea txtHospObservations;
    private javax.swing.JTextArea txtHospReason;
    private javax.swing.JTextField txtAppointmentDate;
    private javax.swing.JTextField txtAppointmentTime;
    private javax.swing.JTextField txtHospDate;
    private javax.swing.JTextField txtProfileAddress;
    private javax.swing.JTextField txtProfileBirthdate;
    private javax.swing.JTextField txtProfileEmail;
    private javax.swing.JTextField txtProfileFirstname;
    private javax.swing.JTextField txtProfileLastname;
    private javax.swing.JTextField txtProfilePassword;
    private javax.swing.JTextField txtProfilePasswordConfirm;
    private javax.swing.JTextField txtProfilePhone;
    private javax.swing.JTextField txtProfileUsername;
    private packagee.PanelRound panelRound1;
    private packagee.PanelRound panelRound2;
    // End of variables declaration//GEN-END:variables
}
