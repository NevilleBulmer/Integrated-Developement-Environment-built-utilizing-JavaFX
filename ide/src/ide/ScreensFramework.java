package ide;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Neville Bulmer
 */
public class ScreensFramework extends Application 
{
//    public static String MainViewID = "MainScreenView";
//    public static String MainViewFile = "MainScreenView.fxml";
    
    @Override
    public void start(Stage primaryStage) throws IOException
    {
//        ScreensController mainContainer = new ScreensController();
//        mainContainer.loadScreen(ScreensFramework.MainViewID, ScreensFramework.MainViewFile);
//        
//        mainContainer.setScreen(ScreensFramework.MainViewID);
//        
//        Group root = new Group();
//        root.getChildren().addAll(mainContainer);
        
        Parent root = FXMLLoader.load(getClass().getResource("MainScreenView.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("StyleSheets/originStyleSheet.css").toExternalForm());
        primaryStage.getIcons().add(new Image(getClass().getResource("Assets/Logo.png").toExternalForm()));
        primaryStage.setTitle("StarLight");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        launch(args);
    }
}
