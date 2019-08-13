/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ide;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author neville
 */
public class AboutScreenViewController implements Initializable 
{
    @FXML
    private TextArea informationSection;
    @FXML
    private Hyperlink facebookLink,
                      twitterLink,
                      githubLink,
                      websiteLink;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        informationSection.setEditable(false);
        
        Desktop d = Desktop.getDesktop();
        
        facebookLink.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            try {
                d.browse(new URI("https://www.google.com/"));
            } catch (URISyntaxException | IOException ex) 
            {
                Logger.getLogger(AboutScreenViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        twitterLink.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            try {
                d.browse(new URI("https://www.google.com/"));
            } catch (URISyntaxException | IOException ex) 
            {
                Logger.getLogger(AboutScreenViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        githubLink.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            try {
                d.browse(new URI("https://www.google.com/"));
            } catch (URISyntaxException | IOException ex) 
            {
                Logger.getLogger(AboutScreenViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        websiteLink.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            try {
                d.browse(new URI("https://www.google.com/"));
            } catch (URISyntaxException | IOException ex) 
            {
                Logger.getLogger(AboutScreenViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }        
}
