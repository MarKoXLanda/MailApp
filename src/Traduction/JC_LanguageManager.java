/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Traduction;

import java.awt.Color;
import java.awt.Font;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

/**
 *
 * @author angel
 */
public class JC_LanguageManager {
    
    private static ResourceBundle bundle;
    
    public static void setLanguage(Locale local){
        bundle = ResourceBundle.getBundle("Traduction.message", local);
    }
    
    public static String getText(String key) {
        if (bundle != null) {
            return bundle.getString(key);
        } else {
            return """
                   Error: No language selected.
                    -- Error: Idioma no seleccionado""";
        }
    }
    
        public static Locale showLanguageSelectionDialog(){
        // Configurar el fondo del OptionPane
        UIManager.put("OptionPane.background", new Color(255, 253, 245));
        UIManager.put("Panel.background", new Color(255, 253, 245));

        // Opciones de idioma
        String[] languages = {"English (US)", "Español (MX)"};

        // Crear el ComboBox con las opciones de idioma y aplicar el estilo
        JComboBox<String> languageComboBox = new JComboBox<>(languages);
        languageComboBox.setFont(new Font("Times New Roman", Font.BOLD, 16));
        languageComboBox.setForeground(new Color(91, 75, 56));
        languageComboBox.setBackground(new Color(255, 253, 245));
        languageComboBox.setBorder(new MatteBorder(0, 0, 2, 0, new Color(91, 75, 56)));
        
        // Crear botones personalizados
        UIManager.put("Button.font", new Font("Times New Roman", Font.BOLD, 16));
        UIManager.put("Button.background", new Color(91, 75, 56));
        UIManager.put("Button.foreground", new Color(255, 253, 245));

        // Mostrar el Option Pane con el ComboBox
        int result = JOptionPane.showConfirmDialog(null, languageComboBox, "Seleccione un idioma / Select Language", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // Obtener la selección del usuario
        Locale local = null;
        if (result == JOptionPane.OK_OPTION){

            String selectedLanguage = (String) languageComboBox.getSelectedItem();
            System.out.println("Idioma seleccionado: " + selectedLanguage);
            
            switch(languageComboBox.getSelectedItem().toString()){
            
                case "English (US)" -> {
                    return local = new Locale("en","US");
                }
                
                case "Español (MX)" -> {
                    return local = new Locale("es","MX");
                }

            }
            
        }
        
        return null;
    }

}
