/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ide;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
/**
 *
 * @author bulme
 */
public class ContextMenuInterface implements EventHandler<ActionEvent>
{
    ContextMenu contextMenu = new ContextMenu();
    private final MenuItem renameFile = new MenuItem("Rename File");
    private final MenuItem deleteFile = new MenuItem("Delete File");
    String originalFileName;
    String newFileName;
    
    MainScreenViewController controller = new MainScreenViewController();
    
    public ContextMenu showContentMenu(TreeView<String> target, double horizontalCoordinate, double verticalCoordinate)
    {
        // Delete action listener.
        renameFile.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event) 
            {
                handleRenameClick(originalFileName, newFileName);
            }
        });
        
        // Delete action listener.
        deleteFile.setOnAction(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent event)
            {
                handleDeleteClick(originalFileName);
            }
        });
    
        // Add MenuItem to ContextMenu.
        contextMenu.getItems().addAll(renameFile, deleteFile);
        // Sets the target and X, Y coordinates of the click.
        contextMenu.show(target, horizontalCoordinate, verticalCoordinate);
        // returns the context menu.
        return contextMenu;
    }
    
    public void handleRenameClick(String clickedFileName, String newFileName)
    {
        // TODO finnished rename code
        File file = new File(clickedFileName);

        File newFile = new File(newFileName);

        // Renames the file denoted by this abstract pathname.
        boolean renamed = file.renameTo(newFile);
            
        DialogBox Open = new DialogBox();
        Open.showRenameBox("Rename", "Rename " + file.getPath());

        int option = Open.getOption();
        if(option == 1)
        {
            System.out.println("originalFileName" + controller.getSelectedFile());
            
            if (renamed) 
            {
                    System.out.println("File renamed to " + newFile.getPath());
            } else {
                    System.out.println("Error renaming file " + file.getPath());
            }


        }
        else if(option == -1)
        {

        }
    }
    
    public void handleDeleteClick(String clickedFileName)
    {
        System.out.println("Delete File");
        
        // TODO finnished delete code
    }
    
    @Override
    public void handle(ActionEvent event)
    {
        
    }
}