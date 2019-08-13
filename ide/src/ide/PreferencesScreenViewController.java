/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ide;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author neville
 */
public class PreferencesScreenViewController implements Initializable, ControlledScreen
{
    ScreensController myController;
    
    @FXML
    private Label selectThemesView,
                  selectLanguageView;
    @FXML
    private AnchorPane displayTesting;
    
    Scene newScene; //then we create a new scene with our new layout
    Stage mainWindow; //Here is the magic. We get the reference to main Stage.

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        selectThemesView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> 
        {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ThemesForPreferences.fxml"));
                Pane cmdPane = (Pane) fxmlLoader.load();
                try {
                    displayTesting.getChildren().clear();
                    displayTesting.getChildren().add(cmdPane);
                } catch (Exception e) 
                {
                    Logger.getLogger(PreferencesScreenViewController.class.getName()).log(Level.SEVERE, null, e);
                }
            } catch (IOException IOE)
            {
                Logger.getLogger(PreferencesScreenViewController.class.getName()).log(Level.SEVERE, null, IOE);
            }   
        });
        
        selectLanguageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> 
        {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LanguageSettingForPreference.fxml"));
                Pane cmdPane = (Pane) fxmlLoader.load();
                try {
                    displayTesting.getChildren().clear();
                    displayTesting.getChildren().add(cmdPane);
                } catch (Exception e)
                {
                    Logger.getLogger(PreferencesScreenViewController.class.getName()).log(Level.SEVERE, null, e);
                }
            } catch (IOException IOE) 
            {
                Logger.getLogger(PreferencesScreenViewController.class.getName()).log(Level.SEVERE, null, IOE);
            }
        });
    }
    
    @Override
    public void setScreenParent(ScreensController screenParent)
    {
        myController = screenParent;
    }
    
}
