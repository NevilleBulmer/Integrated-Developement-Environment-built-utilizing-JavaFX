package ide;

import javafx.stage.FileChooser;
import java.io.*;

public class FileIO 
{
    public File OpenFile()
    {
        FileChooser FC;
        File FileToOpen = null;
        FC = new FileChooser();
        FC.setTitle("Open file...");
        FC.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Source Files", "*.java", "*.fxml", "*.py", "*.cpp", "*.c", "*.c#", "*.html", "*.css", "*.php"),
                new FileChooser.ExtensionFilter("Plain Text Files", "*.txt")
        );
        try {
            FileToOpen = FC.showOpenDialog(null);
        } catch(NullPointerException NPE) 
        {
            new DialogBox().showAlertBox("Error!", "File could not be opened. Try again.");
        }
        return FileToOpen;
    }

    public void SaveFile(File FileToSave, String ContentToSave)
    {
        BufferedWriter BW;
        try {
            BW = new BufferedWriter(new FileWriter(FileToSave));
            BW.write(ContentToSave);
            BW.close();
        } catch(IOException IOE)
        {
            new DialogBox().showAlertBox("Error!", "File could not be saved. Try again.");
        } catch(NullPointerException NPE) {}
    }

    public File SaveFileAs(String ContentToSave)
    {
        FileChooser FC;
        FC = new FileChooser();
        FC.setTitle("Save file as...");
        FC.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Source Files", "*.java", "*fxml", "*.py", "*.cpp", "*.c", "*.html", "*.css", "*.php"),
                new FileChooser.ExtensionFilter("Plain Text Files", "*.txt", "*.rtf")
        );
        File FileToSave = FC.showSaveDialog(null);
        SaveFile(FileToSave, ContentToSave);
        return FileToSave;
    }
}
