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
        lblPatientViewTitle.setText("PATIENT VIEW — ID: " + session.id());

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
        Response<List<String>> rtResp = cc.getRoomTypes();
        if (rtResp.code == 200 && rtResp.data != null) {
            for (String rt : rtResp.data) {
                cmbHospRoomType.addItem(rt);
            }
        }

        // Poblar cmbHospDoctor
        Response<List<DoctorComboDTO>> hospDocResp = cc.getDoctorsCombo();
        hospDoctors = hospDocResp.code == 200 ? hospDocResp.data : new ArrayList<>();
        cmbHospDoctor.removeAllItems();
        for (DoctorComboDTO doc : hospDoctors) {
            cmbHospDoctor.addItem(doc.toString());
        }

        // Poblar cmbAppointmentDoctor con todos los doctores (estado inicial byDoctor)
        Response<List<DoctorComboDTO>> apptDocResp = cc.getDoctorsCombo();
        apptDoctors = apptDocResp.code == 200 ? apptDocResp.data : new ArrayList<>();
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
        Response<List<AppointmentRowDTO>> resp = tc.getPatientAppointments(session.id());
        List<AppointmentRowDTO> rows = resp.code == 200 ? resp.data : new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();
        model.setRowCount(0);
        for (AppointmentRowDTO r : rows) {
            model.addRow(new Object[]{r.id(), r.datetime(), r.doctorFullname(), r.specialty(), r.type(), r.status()});
        }
    }

    private void refreshCancelCombo() {
        Response<List<String>> resp = cc.getAppointmentIdsByPatientAndCancelable(session.id());
        cancelableApptIds = resp.code == 200 ? resp.data : new ArrayList<>();
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

        pnlRoot = new packagee.PanelRound();
        pnlTopbar = new packagee.PanelRound();
        btnClose = new javax.swing.JButton();
        lblPatientViewTitle = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        tabsPatient = new javax.swing.JTabbedPane();
        pnlAppointmentHistoryTab = new javax.swing.JPanel();
        scrPatientAppointments = new javax.swing.JScrollPane();
        tblAppointments = new javax.swing.JTable();
        btnRefreshAppointments = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        pnlProfileTab = new javax.swing.JPanel();
        lblProfileFirstname = new javax.swing.JLabel();
        txtProfileFirstname = new javax.swing.JTextField();
        lblProfileLastname = new javax.swing.JLabel();
        txtProfileLastname = new javax.swing.JTextField();
        lblProfileBirthdate = new javax.swing.JLabel();
        txtProfileBirthdate = new javax.swing.JTextField();
        lblProfileGender = new javax.swing.JLabel();
        lblProfileEmail = new javax.swing.JLabel();
        txtProfileEmail = new javax.swing.JTextField();
        lblProfilePhone = new javax.swing.JLabel();
        txtProfilePhone = new javax.swing.JTextField();
        lblProfileAddress = new javax.swing.JLabel();
        txtProfileAddress = new javax.swing.JTextField();
        txtProfilePassword = new javax.swing.JTextField();
        lblProfilePassword = new javax.swing.JLabel();
        lblProfilePasswordConfirm = new javax.swing.JLabel();
        txtProfilePasswordConfirm = new javax.swing.JTextField();
        btnSaveProfile = new javax.swing.JButton();
        lblProfileUsername = new javax.swing.JLabel();
        txtProfileUsername = new javax.swing.JTextField();
        cmbProfileGender = new javax.swing.JComboBox<>();
        pnlAppointmentRequestTab = new javax.swing.JPanel();
        lblAppointmentSectionTitle = new javax.swing.JLabel();
        rdoBySpecialty = new javax.swing.JRadioButton();
        rdoByDoctor = new javax.swing.JRadioButton();
        sepAppointmentSection = new javax.swing.JSeparator();
        lblAppointmentDate = new javax.swing.JLabel();
        txtAppointmentDate = new javax.swing.JTextField();
        txtAppointmentTime = new javax.swing.JTextField();
        lblAppointmentTime = new javax.swing.JLabel();
        lblAppointmentType = new javax.swing.JLabel();
        lblAppointmentReason = new javax.swing.JLabel();
        cmbAppointmentType = new javax.swing.JComboBox<>();
        btnCreateAppointment = new javax.swing.JButton();
        sepCancelSection = new javax.swing.JSeparator();
        lblHospSectionTitle = new javax.swing.JLabel();
        lblHospReason = new javax.swing.JLabel();
        lblHospDoctor = new javax.swing.JLabel();
        cmbHospDoctor = new javax.swing.JComboBox<>();
        txtHospDate = new javax.swing.JTextField();
        lblHospDate = new javax.swing.JLabel();
        lblHospRoomType = new javax.swing.JLabel();
        cmbHospRoomType = new javax.swing.JComboBox<>();
        lblHospObservations = new javax.swing.JLabel();
        scrAppointmentReason = new javax.swing.JScrollPane();
        txtHospObservations = new javax.swing.JTextArea();
        btnCreateHospitalization = new javax.swing.JButton();
        lblCancelSectionTitle = new javax.swing.JLabel();
        lblCancelAppointmentId = new javax.swing.JLabel();
        lblCancelObservations = new javax.swing.JLabel();
        scrHospReason = new javax.swing.JScrollPane();
        txtCancelDetails = new javax.swing.JTextArea();
        btnCancelAppointment = new javax.swing.JButton();
        scrHospObservations = new javax.swing.JScrollPane();
        txtHospReason = new javax.swing.JTextArea();
        scrCancelDetails = new javax.swing.JScrollPane();
        txtAppointmentReason = new javax.swing.JTextArea();
        cmbCancelAppointmentId = new javax.swing.JComboBox<>();
        cmbAppointmentDoctor = new javax.swing.JComboBox<>();

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

        lblPatientViewTitle.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N
        lblPatientViewTitle.setText("PATIENT VIEW");

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
                .addGap(15, 15, 15)
                .addComponent(lblPatientViewTitle)
                .addGap(29, 29, 29)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClose)
                .addGap(19, 19, 19))
        );
        pnlTopbarLayout.setVerticalGroup(
            pnlTopbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopbarLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnClose))
            .addGroup(pnlTopbarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBack)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(lblPatientViewTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        scrPatientAppointments.setViewportView(tblAppointments);

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

        javax.swing.GroupLayout pnlAppointmentHistoryTabLayout = new javax.swing.GroupLayout(pnlAppointmentHistoryTab);
        pnlAppointmentHistoryTab.setLayout(pnlAppointmentHistoryTabLayout);
        pnlAppointmentHistoryTabLayout.setHorizontalGroup(
            pnlAppointmentHistoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentHistoryTabLayout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(scrPatientAppointments, javax.swing.GroupLayout.PREFERRED_SIZE, 1167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
            .addGroup(pnlAppointmentHistoryTabLayout.createSequentialGroup()
                .addGap(602, 602, 602)
                .addComponent(btnRefreshAppointments)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addGap(78, 78, 78))
        );
        pnlAppointmentHistoryTabLayout.setVerticalGroup(
            pnlAppointmentHistoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentHistoryTabLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(scrPatientAppointments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addGroup(pnlAppointmentHistoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefreshAppointments)
                    .addComponent(btnLogout))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        tabsPatient.addTab("Appointment history", pnlAppointmentHistoryTab);

        lblProfileFirstname.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblProfileFirstname.setText("Firstname");

        txtProfileFirstname.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblProfileLastname.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblProfileLastname.setText("Lastname");

        txtProfileLastname.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblProfileBirthdate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblProfileBirthdate.setText("Birthdate");

        txtProfileBirthdate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblProfileGender.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblProfileGender.setText("Gender");

        lblProfileEmail.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblProfileEmail.setText("Email");

        txtProfileEmail.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblProfilePhone.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblProfilePhone.setText("Phone");

        txtProfilePhone.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblProfileAddress.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblProfileAddress.setText("Address");

        txtProfileAddress.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        txtProfilePassword.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblProfilePassword.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblProfilePassword.setText("Password");

        lblProfilePasswordConfirm.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblProfilePasswordConfirm.setText("Password confirmation");

        txtProfilePasswordConfirm.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        btnSaveProfile.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnSaveProfile.setText("Save");
        btnSaveProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveProfileActionPerformed(evt);
            }
        });

        lblProfileUsername.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblProfileUsername.setText("User");

        txtProfileUsername.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        cmbProfileGender.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbProfileGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one", "Female", "Male" }));

        javax.swing.GroupLayout pnlProfileTabLayout = new javax.swing.GroupLayout(pnlProfileTab);
        pnlProfileTab.setLayout(pnlProfileTabLayout);
        pnlProfileTabLayout.setHorizontalGroup(
            pnlProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProfileTabLayout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(lblProfileFirstname)
                .addGap(18, 18, 18)
                .addComponent(txtProfileFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(lblProfileLastname)
                .addGap(18, 18, 18)
                .addGroup(pnlProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlProfileTabLayout.createSequentialGroup()
                        .addComponent(lblProfilePhone)
                        .addGap(18, 18, 18)
                        .addComponent(txtProfilePhone, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblProfileAddress)
                        .addGap(18, 18, 18)
                        .addComponent(txtProfileAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlProfileTabLayout.createSequentialGroup()
                        .addComponent(txtProfileLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblProfileBirthdate)
                        .addGap(18, 18, 18)
                        .addComponent(txtProfileBirthdate, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblProfileGender)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbProfileGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(lblProfileEmail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(txtProfileEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(141, 141, 141))
            .addGroup(pnlProfileTabLayout.createSequentialGroup()
                .addGap(516, 516, 516)
                .addGroup(pnlProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlProfileTabLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(btnSaveProfile))
                    .addGroup(pnlProfileTabLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(txtProfilePasswordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblProfilePasswordConfirm)
                    .addGroup(pnlProfileTabLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(lblProfilePassword))
                    .addGroup(pnlProfileTabLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(pnlProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtProfileUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(pnlProfileTabLayout.createSequentialGroup()
                                    .addGap(39, 39, 39)
                                    .addComponent(lblProfileUsername)))
                            .addComponent(txtProfilePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlProfileTabLayout.setVerticalGroup(
            pnlProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProfileTabLayout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addGroup(pnlProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProfileFirstname)
                    .addComponent(txtProfileFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProfileLastname)
                    .addComponent(txtProfileLastname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProfileBirthdate)
                    .addComponent(txtProfileBirthdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProfileGender)
                    .addComponent(lblProfileEmail)
                    .addComponent(txtProfileEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbProfileGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlProfileTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProfilePhone)
                    .addComponent(txtProfilePhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProfileAddress)
                    .addComponent(txtProfileAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(66, 66, 66)
                .addComponent(lblProfileUsername)
                .addGap(18, 18, 18)
                .addComponent(txtProfileUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblProfilePassword)
                .addGap(18, 18, 18)
                .addComponent(txtProfilePassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblProfilePasswordConfirm)
                .addGap(18, 18, 18)
                .addComponent(txtProfilePasswordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(btnSaveProfile)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        tabsPatient.addTab("Modify info", pnlProfileTab);

        lblAppointmentSectionTitle.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblAppointmentSectionTitle.setText("Request medical appointment");

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

        sepAppointmentSection.setOrientation(javax.swing.SwingConstants.VERTICAL);

        lblAppointmentDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblAppointmentDate.setText("Appointment date");

        txtAppointmentDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        txtAppointmentTime.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblAppointmentTime.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblAppointmentTime.setText("Appointment time");

        lblAppointmentType.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblAppointmentType.setText("Appointment type");

        lblAppointmentReason.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblAppointmentReason.setText("Appointment reason");

        cmbAppointmentType.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbAppointmentType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one", "Remote", "In-person" }));

        btnCreateAppointment.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCreateAppointment.setText("Create");
        btnCreateAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateAppointmentActionPerformed(evt);
            }
        });

        sepCancelSection.setOrientation(javax.swing.SwingConstants.VERTICAL);

        lblHospSectionTitle.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblHospSectionTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHospSectionTitle.setText("Request hospitalization");

        lblHospReason.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblHospReason.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHospReason.setText("Hospitalization reason");

        lblHospDoctor.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblHospDoctor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHospDoctor.setText("Attending doctor");

        cmbHospDoctor.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbHospDoctor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        txtHospDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblHospDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblHospDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHospDate.setText("Estimated date of admission");
        lblHospDate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblHospRoomType.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblHospRoomType.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHospRoomType.setText("Desired room type");

        cmbHospRoomType.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbHospRoomType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        lblHospObservations.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblHospObservations.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHospObservations.setText("Observations");

        txtHospObservations.setColumns(20);
        txtHospObservations.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtHospObservations.setRows(5);
        scrAppointmentReason.setViewportView(txtHospObservations);

        btnCreateHospitalization.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCreateHospitalization.setText("Create");
        btnCreateHospitalization.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateHospitalizationActionPerformed(evt);
            }
        });

        lblCancelSectionTitle.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblCancelSectionTitle.setText("Cancel appointment");

        lblCancelAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblCancelAppointmentId.setText("ID appointment");

        lblCancelObservations.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblCancelObservations.setText("Observations");

        txtCancelDetails.setColumns(20);
        txtCancelDetails.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtCancelDetails.setRows(5);
        scrHospReason.setViewportView(txtCancelDetails);

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
        scrHospObservations.setViewportView(txtHospReason);

        txtAppointmentReason.setColumns(20);
        txtAppointmentReason.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtAppointmentReason.setRows(5);
        scrCancelDetails.setViewportView(txtAppointmentReason);

        cmbCancelAppointmentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbCancelAppointmentId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        cmbAppointmentDoctor.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cmbAppointmentDoctor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        javax.swing.GroupLayout pnlAppointmentRequestTabLayout = new javax.swing.GroupLayout(pnlAppointmentRequestTab);
        pnlAppointmentRequestTab.setLayout(pnlAppointmentRequestTabLayout);
        pnlAppointmentRequestTabLayout.setHorizontalGroup(
            pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                            .addGap(44, 44, 44)
                            .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                    .addComponent(rdoBySpecialty)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(rdoByDoctor))
                                .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                    .addGap(63, 63, 63)
                                    .addComponent(txtAppointmentDate, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                    .addGap(47, 47, 47)
                                    .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblAppointmentTime)
                                        .addComponent(lblAppointmentDate)
                                        .addComponent(cmbAppointmentDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                    .addGap(63, 63, 63)
                                    .addComponent(txtAppointmentTime, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                    .addGap(38, 38, 38)
                                    .addComponent(lblAppointmentReason))
                                .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                    .addGap(46, 46, 46)
                                    .addComponent(lblAppointmentType))
                                .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                    .addGap(55, 55, 55)
                                    .addComponent(cmbAppointmentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                            .addGap(42, 42, 42)
                            .addComponent(lblAppointmentSectionTitle)))
                    .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(scrCancelDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(btnCreateAppointment)))
                .addGap(69, 69, 69)
                .addComponent(sepAppointmentSection, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                            .addGap(211, 211, 211)
                            .addComponent(btnCreateHospitalization))
                        .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                            .addGap(127, 127, 127)
                            .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblHospReason, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(scrHospObservations, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(lblHospSectionTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                                .addComponent(lblHospDoctor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAppointmentRequestTabLayout.createSequentialGroup()
                            .addGap(127, 127, 127)
                            .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblHospObservations, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblHospDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(scrAppointmentReason, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblHospRoomType, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                        .addGap(190, 190, 190)
                        .addComponent(cmbHospDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                        .addGap(200, 200, 200)
                        .addComponent(txtHospDate, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                        .addGap(191, 191, 191)
                        .addComponent(cmbHospRoomType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                .addComponent(sepCancelSection, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrHospReason, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                        .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(lblCancelSectionTitle))
                            .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                .addGap(77, 77, 77)
                                .addComponent(btnCancelAppointment))
                            .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cmbCancelAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblCancelAppointmentId)))
                            .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(lblCancelObservations)))
                        .addGap(49, 49, 49)))
                .addGap(81, 81, 81))
        );
        pnlAppointmentRequestTabLayout.setVerticalGroup(
            pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sepAppointmentSection)
            .addComponent(sepCancelSection)
            .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                        .addComponent(lblHospSectionTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addComponent(lblHospReason)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrHospObservations, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblHospDoctor)
                        .addGap(18, 18, 18)
                        .addComponent(cmbHospDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblHospDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtHospDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(lblHospRoomType)
                        .addGap(18, 18, 18)
                        .addComponent(cmbHospRoomType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblHospObservations)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(scrAppointmentReason, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCreateHospitalization)
                        .addGap(15, 15, 15))
                    .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                        .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                .addComponent(lblAppointmentSectionTitle)
                                .addGap(18, 18, 18)
                                .addGroup(pnlAppointmentRequestTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(rdoBySpecialty)
                                    .addComponent(rdoByDoctor))
                                .addGap(18, 18, 18)
                                .addComponent(cmbAppointmentDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblAppointmentDate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtAppointmentDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(lblAppointmentTime)
                                .addGap(18, 18, 18)
                                .addComponent(txtAppointmentTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblAppointmentReason)
                                .addGap(24, 24, 24)
                                .addComponent(scrCancelDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlAppointmentRequestTabLayout.createSequentialGroup()
                                .addComponent(lblCancelSectionTitle)
                                .addGap(39, 39, 39)
                                .addComponent(lblCancelAppointmentId)
                                .addGap(18, 18, 18)
                                .addComponent(cmbCancelAppointmentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblCancelObservations)
                                .addGap(18, 18, 18)
                                .addComponent(scrHospReason, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(56, 56, 56)
                                .addComponent(btnCancelAppointment)))
                        .addGap(18, 18, 18)
                        .addComponent(lblAppointmentType)
                        .addGap(18, 18, 18)
                        .addComponent(cmbAppointmentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(btnCreateAppointment)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        tabsPatient.addTab("Request/Cancel", pnlAppointmentRequestTab);

        javax.swing.GroupLayout pnlRootLayout = new javax.swing.GroupLayout(pnlRoot);
        pnlRoot.setLayout(pnlRootLayout);
        pnlRootLayout.setHorizontalGroup(
            pnlRootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTopbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabsPatient)
        );
        pnlRootLayout.setVerticalGroup(
            pnlRootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRootLayout.createSequentialGroup()
                .addComponent(pnlTopbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabsPatient))
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
        if (r.code == 200) {
            txtProfilePassword.setText("");
            txtProfilePasswordConfirm.setText("");
        }
    }//GEN-LAST:event_btnSaveProfileActionPerformed

    private void rdoBySpecialtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoBySpecialtyActionPerformed
        if (initializing) return;
        rdoByDoctor.setSelected(false);
        apptByDoctor = false;
        Response<List<String>> specResp = cc.getSpecialties();
        List<String> specs = specResp.code == 200 ? specResp.data : new ArrayList<>();
        apptDoctors.clear();
        cmbAppointmentDoctor.removeAllItems();
        for (String s : specs) {
            cmbAppointmentDoctor.addItem(s);
        }
    }//GEN-LAST:event_rdoBySpecialtyActionPerformed

    private void rdoByDoctorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoByDoctorActionPerformed
        if (initializing) return;
        rdoBySpecialty.setSelected(false);
        apptByDoctor = true;
        Response<List<DoctorComboDTO>> docResp = cc.getDoctorsCombo();
        apptDoctors = docResp.code == 200 ? docResp.data : new ArrayList<>();
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
    private javax.swing.JLabel lblPatientViewTitle;
    private javax.swing.JLabel lblProfilePassword;
    private javax.swing.JLabel lblProfilePasswordConfirm;
    private javax.swing.JLabel lblProfileUsername;
    private javax.swing.JLabel lblAppointmentSectionTitle;
    private javax.swing.JLabel lblAppointmentDate;
    private javax.swing.JLabel lblAppointmentTime;
    private javax.swing.JLabel lblAppointmentType;
    private javax.swing.JLabel lblAppointmentReason;
    private javax.swing.JLabel lblHospSectionTitle;
    private javax.swing.JLabel lblHospReason;
    private javax.swing.JLabel lblProfileFirstname;
    private javax.swing.JLabel lblHospDoctor;
    private javax.swing.JLabel lblHospDate;
    private javax.swing.JLabel lblHospRoomType;
    private javax.swing.JLabel lblHospObservations;
    private javax.swing.JLabel lblCancelSectionTitle;
    private javax.swing.JLabel lblCancelAppointmentId;
    private javax.swing.JLabel lblCancelObservations;
    private javax.swing.JLabel lblProfileLastname;
    private javax.swing.JLabel lblProfileBirthdate;
    private javax.swing.JLabel lblProfileGender;
    private javax.swing.JLabel lblProfileEmail;
    private javax.swing.JLabel lblProfilePhone;
    private javax.swing.JLabel lblProfileAddress;
    private javax.swing.JPanel pnlProfileTab;
    private javax.swing.JPanel pnlAppointmentRequestTab;
    private javax.swing.JPanel pnlAppointmentHistoryTab;
    private javax.swing.JRadioButton rdoByDoctor;
    private javax.swing.JRadioButton rdoBySpecialty;
    private javax.swing.JScrollPane scrAppointmentReason;
    private javax.swing.JScrollPane scrHospReason;
    private javax.swing.JScrollPane scrPatientAppointments;
    private javax.swing.JScrollPane scrHospObservations;
    private javax.swing.JScrollPane scrCancelDetails;
    private javax.swing.JSeparator sepAppointmentSection;
    private javax.swing.JSeparator sepCancelSection;
    private javax.swing.JTabbedPane tabsPatient;
    private javax.swing.JTable tblAppointments;
    private javax.swing.JTextArea txtCancelDetails;
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
    private packagee.PanelRound pnlRoot;
    private packagee.PanelRound pnlTopbar;
    // End of variables declaration//GEN-END:variables
}
