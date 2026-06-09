/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Class;

import Traduction.JC_LanguageManager;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

public class JF_SendEmail extends javax.swing.JFrame {

    String[] secureFiles = {
        // Text files
        ".pdf", ".docx", ".xlsx", ".pptx", ".txt", ".odt", ".rtf", ".html", ".md",
        // Compressed files
        ".zip", ".rar", ".7z", ".tar", ".gz", ".bz2",
        // Video files
        ".mp4", ".avi", ".mkv", ".mov", ".flv", ".wmv", ".webm", ".mpeg",
        // Audio files
        ".mp3", ".wav", ".aac", ".flac", ".ogg", ".wma", ".m4a",
        // Image files
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tiff", ".svg"
    };

    final String from = "ejempjava@gmail.com";
    final String to = "miguelangelavilagarcia13@gmail.com";
    final String subject = "Test email";
    final String content = "This is a test email";

    private File[] arrFiles;
    private String nameFiles = null;

    private JF_Registry JF_Registry;  // Instancia del JFrame de historial

    /**
     * Creates new form JF_SentEmail
     */
    public JF_SendEmail() {
        initComponents();
        jComboBox1.setVisible(false);
        setLocationRelativeTo(null);
        properties = new Properties();
        JF_Registry = new JF_Registry(); // Inicializa el JFrame de historial
    }

    private boolean validFile(String file, int i) {

        if (i > arrFiles.length) {
            optionPane(JC_LanguageManager.getText("OP_ValidFile"));
        } else if (file.endsWith(secureFiles[i])) {
            return true;
        } else {
            validFile(file, i + 1);
        }

        return false;
    }

    private Properties properties;
    private Session session;
    private MimeMessage message;

    private void configureProperties() throws NullPointerException {
        properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.user", jTF_From.getText());
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.auth", "true");

    }

//    public void createEmail() throws MessagingException {
//        
//            MimeMultipart elementsFiles = new MimeMultipart();
//            MimeBodyPart contentFiles = new MimeBodyPart();
//            contentFiles.setContent(content, "text/html; charset=utf-8");
//            elementsFiles.addBodyPart(contentFiles);
//            
//            MimeBodyPart attachedElements = null;
//            if(arrFiles != null){
//                    for (int i = 0; i < arrFiles.length; i++) {
//                    attachedElements = new MimeBodyPart();
//                    attachedElements.setDataHandler(new DataHandler(new FileDataSource(arrFiles[i].getAbsolutePath())));
//                    attachedElements.setFileName(arrFiles[i].getName());
//            }
//                    elementsFiles.addBodyPart(attachedElements);
//                }
//            
//        session = Session.getDefaultInstance(properties);
//        message = new MimeMessage(session);
//        message.setFrom(new InternetAddress(jTF_From.getText()));
//        message.setRecipient(Message.RecipientType.TO, new InternetAddress(jTF_To.getText()));
//        message.setSubject(jTF_Subject.getText());
//        message.setContent(elementsFiles);
//    }
    public void createEmail() throws MessagingException {
        MimeMultipart elementsFiles = new MimeMultipart();
        MimeBodyPart contentFiles = new MimeBodyPart();
        contentFiles.setContent(jTA_Content.getText(), "text/html; charset=utf-8");
        elementsFiles.addBodyPart(contentFiles);

        // Adjuntar archivos si existen
        if (arrFiles != null) {
            for (File file : arrFiles) {
                MimeBodyPart attachedElements = new MimeBodyPart();
                attachedElements.setDataHandler(new DataHandler(new FileDataSource(file.getAbsolutePath())));
                attachedElements.setFileName(file.getName());
                elementsFiles.addBodyPart(attachedElements);
            }
        }

        session = Session.getDefaultInstance(properties);
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(jTF_From.getText()));

        // Obtener correos del JTextField y del JComboBox
        String emailTextField = jTF_To.getText().trim();
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) jComboBox1.getModel();
        int size = model.getSize();

        ArrayList<InternetAddress> recipientList = new ArrayList<>();

        // Agregar primero el correo del JTextField si no está vacío
        if (!emailTextField.isEmpty() && emailTextField.contains("@")) {
            recipientList.add(new InternetAddress(emailTextField));
        }

        // Luego agregar los correos del JComboBox si hay
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                recipientList.add(new InternetAddress(model.getElementAt(i)));
            }
        }

        // Si la lista sigue vacía, lanzar un error
        if (recipientList.isEmpty()) {
            //      throw new MessagingException("No hay destinatarios válidos.");
            optionPane(JC_LanguageManager.getText("MessagingException"));
        }

        // Convertir lista a array y asignarla como destinatarios
        InternetAddress[] recipientAddresses = recipientList.toArray(new InternetAddress[0]);
        message.setRecipients(Message.RecipientType.TO, recipientAddresses);
        message.setSubject(jTF_Subject.getText());
        message.setContent(elementsFiles);
    }

    private void sendEmail() throws SendFailedException, MessagingException, IOException {

        Transport transport = session.getTransport("smtp");
        transport.connect(jTF_From.getText(), "phmfjyrutxutjdrl");
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();

        optionPane(JC_LanguageManager.getText("OP_EmailSent"));
    }

    public void deleteEmail() {

        jTF_To.setText(JC_LanguageManager.getText("jL_To"));
        jTF_Subject.setText(JC_LanguageManager.getText("jTF_Subject"));
        jTA_Content.setText(JC_LanguageManager.getText("jTA_Content"));

        arrFiles = null;
        nameFiles = null;
        jTF_AttachedFile.setText(JC_LanguageManager.getText("jTF_AttachFile"));

        optionPane(JC_LanguageManager.getText("OP_EmailDeleted "));

    }

    public void optionPane(String message) {

        JLabel labelEmergent = null;
        UIManager emergent = null;

        emergent.put("OptionPane.background", new Color(255, 253, 245));

        labelEmergent = new JLabel(message, SwingConstants.CENTER);
        labelEmergent.setFont(new Font("Times New Roman", Font.BOLD, 16));
        labelEmergent.setForeground(new Color(91, 75, 56));
        JOptionPane.showMessageDialog(null, labelEmergent, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    public void isGray(JTextField textField) {

        if (!textField.isEditable()) {
            textField.setText("");
        }

        textField.setForeground(new Color(91, 75, 56));
        textField.setEditable(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jP_SentEmail = new javax.swing.JPanel();
        jL_From = new javax.swing.JLabel();
        jL_To = new javax.swing.JLabel();
        jTF_From = new javax.swing.JTextField();
        jTF_To = new javax.swing.JTextField();
        jTF_SapaceBlank1 = new javax.swing.JTextField();
        jTF_SapaceBlank = new javax.swing.JTextField();
        jTF_Subject = new javax.swing.JTextField();
        jSP_Content = new javax.swing.JScrollPane();
        jTA_Content = new javax.swing.JTextArea();
        jB_Send = new javax.swing.JButton();
        jB_Attach = new javax.swing.JButton();
        jB_Delete = new javax.swing.JButton();
        jTF_AttachedFile = new javax.swing.JTextField();
        jButAñadir = new javax.swing.JButton();
        jButDeletePart = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jMB_SendEmail = new javax.swing.JMenuBar();
        jMB_Help = new javax.swing.JMenu();
        jMI_Tutorial = new javax.swing.JMenuItem();
        jMI_AutoComplete = new javax.swing.JMenuItem();
        jMenuHistorial = new javax.swing.JMenuItem();
        jM_Back = new javax.swing.JMenu();
        jM_Language = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jP_SentEmail.setBackground(new java.awt.Color(255, 253, 245));

        jL_From.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jL_From.setForeground(new java.awt.Color(91, 75, 56));
        jL_From.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jL_From.setText(JC_LanguageManager.getText("jL_From"));

        jL_To.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jL_To.setForeground(new java.awt.Color(91, 75, 56));
        jL_To.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jL_To.setText(JC_LanguageManager.getText("jL_To"));

        jTF_From.setEditable(false);
        jTF_From.setBackground(new java.awt.Color(255, 253, 245));
        jTF_From.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTF_From.setForeground(new java.awt.Color(91, 75, 56));
        jTF_From.setText("ejempjava@gmail.com");
        jTF_From.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(91, 75, 56)));

        jTF_To.setBackground(new java.awt.Color(255, 253, 245));
        jTF_To.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTF_To.setForeground(new java.awt.Color(91, 75, 56));
        jTF_To.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(91, 75, 56)));

        jTF_SapaceBlank1.setEditable(false);
        jTF_SapaceBlank1.setBackground(new java.awt.Color(255, 253, 245));
        jTF_SapaceBlank1.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTF_SapaceBlank1.setForeground(new java.awt.Color(91, 75, 56));
        jTF_SapaceBlank1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(91, 75, 56)));

        jTF_SapaceBlank.setEditable(false);
        jTF_SapaceBlank.setBackground(new java.awt.Color(255, 253, 245));
        jTF_SapaceBlank.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTF_SapaceBlank.setForeground(new java.awt.Color(91, 75, 56));
        jTF_SapaceBlank.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(91, 75, 56)));
        jTF_SapaceBlank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTF_SapaceBlankActionPerformed(evt);
            }
        });

        jTF_Subject.setEditable(false);
        jTF_Subject.setBackground(new java.awt.Color(255, 253, 245));
        jTF_Subject.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTF_Subject.setForeground(new java.awt.Color(204, 204, 204));
        jTF_Subject.setText(JC_LanguageManager.getText("jTF_Subject"));
        jTF_Subject.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(91, 75, 56)));
        jTF_Subject.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTF_SubjectMouseClicked(evt);
            }
        });

        jTA_Content.setEditable(false);
        jTA_Content.setBackground(new java.awt.Color(255, 253, 245));
        jTA_Content.setColumns(20);
        jTA_Content.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTA_Content.setForeground(new java.awt.Color(204, 204, 204));
        jTA_Content.setRows(5);
        jTA_Content.setText(JC_LanguageManager.getText("jTA_Content"));
        jTA_Content.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 1, 1, new java.awt.Color(91, 75, 56)), JC_LanguageManager.getText("jBorder_Content"), javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Times New Roman", 1, 14), new java.awt.Color(91, 75, 56)));
        jTA_Content.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTA_ContentMouseClicked(evt);
            }
        });
        jSP_Content.setViewportView(jTA_Content);

        jB_Send.setBackground(new java.awt.Color(91, 75, 56));
        jB_Send.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        jB_Send.setForeground(new java.awt.Color(255, 253, 245));
        jB_Send.setText(JC_LanguageManager.getText("jB_Send"));
        jB_Send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_SendActionPerformed(evt);
            }
        });

        jB_Attach.setBackground(new java.awt.Color(91, 75, 56));
        jB_Attach.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Attach.png"))); // NOI18N
        jB_Attach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_AttachActionPerformed(evt);
            }
        });

        jB_Delete.setBackground(new java.awt.Color(91, 75, 56));
        jB_Delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Delete.png"))); // NOI18N
        jB_Delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_DeleteActionPerformed(evt);
            }
        });

        jTF_AttachedFile.setEditable(false);
        jTF_AttachedFile.setBackground(new java.awt.Color(255, 253, 245));
        jTF_AttachedFile.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTF_AttachedFile.setForeground(new java.awt.Color(204, 204, 204));
        jTF_AttachedFile.setText(JC_LanguageManager.getText("jTF_AttachFile"));
        jTF_AttachedFile.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(91, 75, 56)));
        jTF_AttachedFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTF_AttachedFileMouseClicked(evt);
            }
        });
        jTF_AttachedFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTF_AttachedFileActionPerformed(evt);
            }
        });

        jButAñadir.setBackground(new java.awt.Color(255, 253, 245));
        jButAñadir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/pluspartner.png"))); // NOI18N
        jButAñadir.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jButAñadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButAñadirActionPerformed(evt);
            }
        });

        jButDeletePart.setBackground(new java.awt.Color(255, 253, 245));
        jButDeletePart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/deletepart.png"))); // NOI18N
        jButDeletePart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jButDeletePart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButDeletePartActionPerformed(evt);
            }
        });

        jComboBox1.setBackground(new java.awt.Color(255, 253, 245));
        jComboBox1.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N

        javax.swing.GroupLayout jP_SentEmailLayout = new javax.swing.GroupLayout(jP_SentEmail);
        jP_SentEmail.setLayout(jP_SentEmailLayout);
        jP_SentEmailLayout.setHorizontalGroup(
            jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_SentEmailLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jSP_Content, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                    .addComponent(jTF_Subject, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_SentEmailLayout.createSequentialGroup()
                        .addComponent(jB_Send, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jB_Attach)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jB_Delete))
                    .addComponent(jTF_AttachedFile)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jP_SentEmailLayout.createSequentialGroup()
                        .addGroup(jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jL_To, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jL_From, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jP_SentEmailLayout.createSequentialGroup()
                                .addGroup(jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTF_SapaceBlank1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTF_SapaceBlank, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0)
                                .addGroup(jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTF_From, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                                    .addComponent(jTF_To))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButAñadir, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButDeletePart, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jP_SentEmailLayout.setVerticalGroup(
            jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_SentEmailLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTF_From)
                    .addGroup(jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jL_From, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTF_SapaceBlank)))
                .addGap(18, 18, 18)
                .addGroup(jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jL_To)
                        .addComponent(jTF_To)
                        .addComponent(jTF_SapaceBlank1))
                    .addGroup(jP_SentEmailLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButAñadir, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButDeletePart, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTF_Subject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSP_Content, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jTF_AttachedFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jP_SentEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jB_Attach, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jB_Delete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jB_Send, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44))
        );

        jMB_Help.setText(JC_LanguageManager.getText("jM_Help"));

        jMI_Tutorial.setText(JC_LanguageManager.getText("jMI_Tutorial"));
        jMI_Tutorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_TutorialActionPerformed(evt);
            }
        });
        jMB_Help.add(jMI_Tutorial);

        jMI_AutoComplete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMI_AutoComplete.setText(JC_LanguageManager.getText("jMI_AutoComplete"));
        jMI_AutoComplete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_AutoCompleteActionPerformed(evt);
            }
        });
        jMB_Help.add(jMI_AutoComplete);

        jMenuHistorial.setText(JC_LanguageManager.getText("jMenuHistorial"));
        jMenuHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuHistorialActionPerformed(evt);
            }
        });
        jMB_Help.add(jMenuHistorial);

        jMB_SendEmail.add(jMB_Help);

        jM_Back.setText(JC_LanguageManager.getText("jM_Back"));
        jM_Back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jM_BackMouseClicked(evt);
            }
        });
        jMB_SendEmail.add(jM_Back);

        jM_Language.setText(JC_LanguageManager.getText("jM_Language"));
        jM_Language.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jM_LanguageMouseClicked(evt);
            }
        });
        jMB_SendEmail.add(jM_Language);

        setJMenuBar(jMB_SendEmail);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jP_SentEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jP_SentEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMI_AutoCompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_AutoCompleteActionPerformed

        isGray(jTF_Subject);
        jTF_From.setText(from);
        jTF_To.setText(to);

        if (!jTA_Content.isEditable()) {
            jTA_Content.setText("");
            jTA_Content.setForeground(new Color(91, 75, 56));
            jTA_Content.setEditable(true);
        }

        jTF_Subject.setText(subject);
        jTA_Content.setText(content);

    }//GEN-LAST:event_jMI_AutoCompleteActionPerformed

    private void jTF_SapaceBlankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTF_SapaceBlankActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTF_SapaceBlankActionPerformed

    private void jM_BackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jM_BackMouseClicked
        JF_Login login = new JF_Login();
        login.setVisible(true);

        dispose();
    }//GEN-LAST:event_jM_BackMouseClicked

    private void jTF_SubjectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTF_SubjectMouseClicked
        isGray(jTF_Subject);
    }//GEN-LAST:event_jTF_SubjectMouseClicked

    private void jTA_ContentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTA_ContentMouseClicked
        if (!jTA_Content.isEditable()) {
            jTA_Content.setText("");
            jTA_Content.setForeground(new Color(91, 75, 56));
            jTA_Content.setEditable(true);
        }
    }//GEN-LAST:event_jTA_ContentMouseClicked

    private void jB_DeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_DeleteActionPerformed
        deleteEmail();
    }//GEN-LAST:event_jB_DeleteActionPerformed

    private void jB_SendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_SendActionPerformed

        if (jTF_To.getText().equals("")) {

            optionPane(JC_LanguageManager.getText("OP_NoSender"));

        } else {

            try {
                configureProperties();
                createEmail();
                sendEmail();

                String to = jTF_To.getText(); // Obtener el destinatario del campo de texto
                String selectedComboBoxItem = (String) jComboBox1.getSelectedItem(); // Obtener el elemento seleccionado del JComboBox

// Si hay algo seleccionado en el JComboBox, lo agregamos al destinatario
                if (selectedComboBoxItem != null && !selectedComboBoxItem.isEmpty()) {
                    to += "-" + selectedComboBoxItem; // Concatenar el destinatario del JComboBox
                }

                JF_Registry.saveEmailLog(jTF_From.getText(), to, jTF_Subject.getText(), jTA_Content.getText(), jTF_AttachedFile.getText());
               
                //JF_Registry.saveEmailLog(jTF_From.getText(),
             //           jTF_To.getText() + "," + jComboBox1.getItemAt(WIDTH), jTF_Subject.getText(), jTA_Content.getText(), jTF_AttachedFile.getText());
                clearComboBox();
            } catch (MessagingException ex) {
                optionPane(JC_LanguageManager.getText("EX_MessagingException"));
            } catch (UnknownHostException ex) {
                optionPane(JC_LanguageManager.getText("EX_UnknownHostException"));
            } catch (IOException | NullPointerException ex) {
                optionPane(JC_LanguageManager.getText("EX_IOExceptionORNullPointerException"));
            }

        }


    }//GEN-LAST:event_jB_SendActionPerformed

    private void jTF_AttachedFileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTF_AttachedFileMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTF_AttachedFileMouseClicked

    private void jB_AttachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_AttachActionPerformed

        JFileChooser chooser = new JFileChooser();

        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (chooser.showOpenDialog(this) != JFileChooser.CANCEL_OPTION) {

            arrFiles = chooser.getSelectedFiles();

            long totalSize = 0;
            int i = 0;
            for (File file : arrFiles) {

                totalSize += file.length();

                if (validFile(arrFiles[i].getName(), 0)) {

                    if (totalSize >= 23000000) {

                        arrFiles = null;
                        nameFiles = null;

                        optionPane(JC_LanguageManager.getText("OP_FileSize"));
                        break;
                    }

                    nameFiles += file.getName() + "  ";
                }

            }

            jTF_AttachedFile.setText(nameFiles);
        }


    }//GEN-LAST:event_jB_AttachActionPerformed

    private void addEmailToComboBox() {
        String email = jTF_To.getText().trim(); // Obtener el texto del JTextField

        if (!email.isEmpty() && email.contains("@")) { // Validar que sea un correo
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) jComboBox1.getModel();

            // Verificar si el correo ya está en la lista
            boolean exists = false;
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getElementAt(i).equals(email)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                model.addElement(email); // Agregar al JComboBox
                jTF_To.setText(""); // Limpiar el campo de texto
                updateComboBoxVisibility();
            } else {
                // JOptionPane.showMessageDialog(this, "Este correo ya fue agregado.", "Duplicado", JOptionPane.WARNING_MESSAGE);
                optionPane(JC_LanguageManager.getText("MailAdded"));
            }
        } else {
            //  JOptionPane.showMessageDialog(this, "Ingrese un correo válido.", "Error", JOptionPane.ERROR_MESSAGE);
            optionPane(JC_LanguageManager.getText("MailValid"));

        }
    }

    private void removeEmailFromComboBox() {
        int selectedIndex = jComboBox1.getSelectedIndex(); // Obtener el índice seleccionado

        if (selectedIndex != -1) { // Verificar si hay un elemento seleccionado
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) jComboBox1.getModel();
            model.removeElementAt(selectedIndex); // Eliminar el elemento seleccionado
            updateComboBoxVisibility();
        } else {
            //    JOptionPane.showMessageDialog(this, "No hay correos por eliminar.", "Error", JOptionPane.WARNING_MESSAGE);
            optionPane(JC_LanguageManager.getText("NoMails"));
        }
    }

    private void updateComboBoxVisibility() {
        if (jComboBox1.getItemCount() == 0) {
            jComboBox1.setVisible(false); // Oculta el JComboBox si está vacío
        } else {
            jComboBox1.setVisible(true);  // Muestra el JComboBox si tiene elementos
        }
    }

    private void clearComboBox() {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) jComboBox1.getModel();
        model.removeAllElements(); // Elimina todos los elementos
        updateComboBoxVisibility(); // Oculta el JComboBox si está vacío
    }


    private void jM_LanguageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jM_LanguageMouseClicked

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FlatLightLaf.setup();
                JC_LanguageManager.setLanguage(JC_LanguageManager.showLanguageSelectionDialog());
                dispose();
                new JF_SendEmail().setVisible(true);
            }
        });


    }//GEN-LAST:event_jM_LanguageMouseClicked

    private void jMenuHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuHistorialActionPerformed
        // TODO add your handling code here:

        JF_Registry.setVisible(true);  // Hacer visible el JFrame de historial

    }//GEN-LAST:event_jMenuHistorialActionPerformed

    private void jMI_TutorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_TutorialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMI_TutorialActionPerformed

    private void jButAñadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButAñadirActionPerformed
        // TODO add your handling code here:
        addEmailToComboBox();
    }//GEN-LAST:event_jButAñadirActionPerformed

    private void jButDeletePartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButDeletePartActionPerformed
        // TODO add your handling code here:
        removeEmailFromComboBox();
    }//GEN-LAST:event_jButDeletePartActionPerformed

    private void jTF_AttachedFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTF_AttachedFileActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_jTF_AttachedFileActionPerformed
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JF_SendEmail.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JF_SendEmail.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JF_SendEmail.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JF_SendEmail.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FlatLightLaf.setup();

                try {
                    JC_LanguageManager.setLanguage(JF_Login.getLocal());
                } catch (Exception e) {
                    JC_LanguageManager.setLanguage(JC_LanguageManager.showLanguageSelectionDialog());
                }

                new JF_SendEmail().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_Attach;
    private javax.swing.JButton jB_Delete;
    private javax.swing.JButton jB_Send;
    private javax.swing.JButton jButAñadir;
    private javax.swing.JButton jButDeletePart;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jL_From;
    private javax.swing.JLabel jL_To;
    private javax.swing.JMenu jMB_Help;
    private javax.swing.JMenuBar jMB_SendEmail;
    private javax.swing.JMenuItem jMI_AutoComplete;
    private javax.swing.JMenuItem jMI_Tutorial;
    private javax.swing.JMenu jM_Back;
    private javax.swing.JMenu jM_Language;
    private javax.swing.JMenuItem jMenuHistorial;
    private javax.swing.JPanel jP_SentEmail;
    private javax.swing.JScrollPane jSP_Content;
    private javax.swing.JTextArea jTA_Content;
    private javax.swing.JTextField jTF_AttachedFile;
    private javax.swing.JTextField jTF_From;
    private javax.swing.JTextField jTF_SapaceBlank;
    private javax.swing.JTextField jTF_SapaceBlank1;
    private javax.swing.JTextField jTF_Subject;
    private javax.swing.JTextField jTF_To;
    // End of variables declaration//GEN-END:variables
}
