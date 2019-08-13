package ide;

import SyntaxHighlighting.ComputeSyntaxHighlighting;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author bulme
 */
public class MainScreenViewController implements Initializable, ControlledScreen
{
    ScreensController myController;
    
    @FXML
    private MenuItem handleMakeNewFile,
            handleOpenFile,
            handleOpenFolder,
            handleCloseFile,
            handleSaveFile,
            handleSaveAs,
            handlePreferences,
            handleQuit,
            handleUndo,
            handleRedo,
            handleCut,
            handleCopy,
            handlePaste,
            handleFind,
            handleDelete,
            handleSelectAll,
            handleUnselectAll,
            runCurrentFile,
            compileCurrentFile,
            handleAbout,
            handleThemes,
            
            selectJavaSyntax,
            selectFXMLSyntax,
            selectHTMLSyntax,
            selectCSSSyntax,
            selectPHPSyntax,
            selectJavaScriptSyntax,
            selectCSyntax,
            selectCPPSyntax,
            selectCSSyntax,
            selectPySyntax;
    @FXML
    private ImageView playSourceImage,
                      compileSourceImage,
            
                      toggleRightPaneSelectorImage,
                      toggleBottomPaneSelectorImage,
                      toggleLeftPaneSelectorImage,

                      searchTermQuerieInitiator;
    @FXML
    private TextField findTextField,
                      userSearchTextField;
    @FXML
    private Label fileType,
            caretPosition,
            checkTextWrap,
            textWrapStatus;
    boolean text = false;
    @FXML
    private Font x31;
    @FXML
    private BorderPane rootViewWindow,
                       testBorderPane;
    @FXML
    private AnchorPane  
            fileViewWindow, 
            mainEditorViewWindow,
            webContainer,
            terminalViewWindow;
    @FXML
    private ScrollPane mainEditorContainer,
                       terminalEditorContainer;
    @FXML
    private WebView webViewWindow;
    @FXML
    private Hyperlink searchTargetSelectorGoogle,
                      searchTargetSelectorStack;
   
    // The path to java compiler on a macOS based machine.
    private final String MAC_PATH_TO_JC = "/Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/bin/javac ";
    
    // Path to the java runtime for mac.
    private final String MAC_PATH_TO_JAVA = "/Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/bin/java ";
    
    // The path to java compiler on a windows based machine.
    private final String WINDOWS_PATH_TO_JC = "C:\\Program Files\\Java\\jdk1.8.0_191\\bin\\javac.exe ";
    
    // The path to java compiler on a windows based machine.
    private final String WINDOWS_PATH_TO_JAVA = "C:\\Program Files\\Java\\jdk1.8.0_191\\bin\\java.exe ";
  
    // Depending on the os the path to the java compiler.
    private String pathToJavaCompile,
                   pathToJavaRuntime;
    // Runtime variable for compilation and run code.
    Runtime r;
    
    // codeArea, RichTextFX text area.
    private CodeArea codeArea,
                     terminalCodeArea;
    // executor, used for a second thread which handles the syntax highlighting.
    private ExecutorService executor;
    // Instantiates a Clipboard object.
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    // Instantiates a ClipboardContent object.
    final ClipboardContent content = new ClipboardContent();
    // TreeItem array which holds the file paths for the treeview.
    TreeItem<String> root = new TreeItem<>();
    // Global File assignment I.e. a selected file equals selectedFile.
    private File selectedFile;
    
    private boolean isModified, isFileOpen;
    boolean googleSelected = false;
    boolean stackSelected = false;
    private String FileContent;
    
    private TreeView<String> treeViewDirectoryListings;
    private TreeItem<String> OpenedFileRoot;
    private HashMap<String, String> FileAddresses;
    private ArrayList<String> SupportedFileTypes;
    
    ComputeSyntaxHighlighting compute = new ComputeSyntaxHighlighting();

    private final String KEYWORD_PATTERN = "\\b(" + String.join("|", compute.ComputeJavaSyntax()) + ")\\b";
    private final String PAREN_PATTERN = "\\(|\\)";
    private final String BRACE_PATTERN = "\\{|\\}";
    private final String BRACKET_PATTERN = "\\[|\\]";
    private final String SEMICOLON_PATTERN = "\\;";
    private final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    
    private final Pattern PATTERN = Pattern.compile
    (
          "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
        + "|(?<PAREN>" + PAREN_PATTERN + ")"
        + "|(?<BRACE>" + BRACE_PATTERN + ")"
        + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
        + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
        + "|(?<STRING>" + STRING_PATTERN + ")"
        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    
    private final String MAIN_TEMPLATE = String.join("\n", new String[] 
    {
        "public class YourClassName",
        "{",
        "    /*",
        "     * multi-line comment",
        "     */",
        "    public static void main(String[] args)",
        "    {",
        "        // single-line comment",
        "        System.out.println(\"I am a template.\");",
        "    }",
        "}"
    });
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        r= Runtime.getRuntime();
        mainEditorContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainEditorContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Treeview, Holds the files to be shown in the treeview.
        treeViewDirectoryListings = new TreeView<>();
        // File addresses hashmap.
        FileAddresses = new HashMap<>();
        // Supported file extensions array.
        SupportedFileTypes = new ArrayList<>();
        // Supported file extensions java.
        SupportedFileTypes.add("java");
        // Supported file extensions fxml.
        SupportedFileTypes.add("fxml");
        // Supported file extensions cpp.
        SupportedFileTypes.add("cpp");
        // Supported file extensions c.
        SupportedFileTypes.add("c");
        // Supported file extensions c#.
        SupportedFileTypes.add("c#");
        // Supported file extensions py.
        SupportedFileTypes.add("py");
        // Supported file extensions html.
        SupportedFileTypes.add("html");
        // Supported file extensions css.
        SupportedFileTypes.add("css");
        // Supported file extensions php.
        SupportedFileTypes.add("php");
        // Supported file extensions txt.
        SupportedFileTypes.add("txt");
    
        // Detects the os name.
        String detectedOS = System.getProperty("os.name");
        // Check if the detected OS is windows 10, if it is it sets the path
        // to the compiler to the windows one.
        if(detectedOS.equals("Windows 10"))
        {
            // Path to the java compiler.
            pathToJavaCompile = WINDOWS_PATH_TO_JC;
            
            // Path to the java runtime.
            pathToJavaRuntime = WINDOWS_PATH_TO_JAVA;
        }
        // Check if the detected OS is Mac OS X, if it is it sets the path
        // to the compiler to the mac one.
        else if(detectedOS.equals("Mac OS X"))
        {
            // Path to the java compiler.
            pathToJavaCompile = MAC_PATH_TO_JC;
            
            // Path to the java runtime.
            pathToJavaRuntime = MAC_PATH_TO_JAVA;
        }
        // Sets executor to a new single thread.
        executor = Executors.newSingleThreadExecutor();
        // Editor code area
        codeArea = new CodeArea();
        // Addds the line numbers to the codearea
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        
        Subscription cleanupWhenDone = codeArea.multiPlainChanges()
            .successionEnds(java.time.Duration.ofMillis(500))
            .supplyTask(this::computeHighlightingAsync)
            .awaitLatest(codeArea.multiPlainChanges())
            .filterMap(t -> {
                if(t.isSuccess())
                {
                    return Optional.of(t.get());
                } else {
                    return Optional.empty();
                }
            })
            .subscribe(this::applyHighlighting);

        mainEditorViewWindow.getChildren().add(codeArea);
        
        mainEditorViewWindow.prefWidthProperty().bind(mainEditorContainer.widthProperty());
        mainEditorViewWindow.prefHeightProperty().bind(mainEditorContainer.heightProperty());
        
        codeArea.prefWidthProperty().bind(mainEditorViewWindow.widthProperty());
        codeArea.prefHeightProperty().bind(mainEditorViewWindow.heightProperty());
        
        webViewWindow.prefWidthProperty().bind(webContainer.widthProperty());
        webViewWindow.prefHeightProperty().bind(webContainer.heightProperty());

        fileViewWindow.prefWidthProperty().bind(mainEditorViewWindow.widthProperty());
        fileViewWindow.prefHeightProperty().bind(mainEditorViewWindow.heightProperty());
        
        treeViewDirectoryListings.getSelectionModel().selectedItemProperty().addListener(
            (v, oldVal, newVal) ->
            {
                saveChangesToCurrentFile();        // Saves current file before opening new.
                File Temp;
                try {
                    Temp = new File(FileAddresses.get(newVal.getValue()));    // Calls ReadOpenFile only if tree item is a file.
                    if(Temp.isFile()) {
                        StringBuffer SB = new StringBuffer(Temp.getName());
                        int dotPosition = SB.lastIndexOf(".");
                        String FileType = SB.substring(dotPosition + 1, SB.length());
                        
                        if(SupportedFileTypes.contains(FileType)) {
                            selectedFile = Temp;
                            ReadOpenedFile();
                        } else {
                            new DialogBox().showAlertBox("Error!", "This file is not spported.");
                        }
                    }
                } catch(NullPointerException NPE){}
            }
        );
        
        rootViewWindow.setLeft(treeViewDirectoryListings);
        
        checkTextWrap.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> 
        {
            System.out.println("TextWrap");
            if(text == false)
            {
                text = true;
                textWrapStatus.setText("Off");
                codeArea.setWrapText(false);
            }else if(text == true)
            {
                text = false;
                textWrapStatus.setText("On");
                codeArea.setWrapText(true);
            }
            event.consume();
        });
        
        
        
//        DragResizeMod.makeResizable(terminalEditorContainer);
//        DragResizeMod.makeResizable(webContainer);
        
        setupTreeViewContextMenu();
        //
        StartupSettingsLoader();
        //
        setEditor();
        // Menu selection.
        menuItemSelection();
        // Syntax selectors.
        syntaxSelectors();
        // Menu key binds.
        keyBindsForMenuOperators();
        // Button listeners I.e.
        // Play
        // Compile
        // Toggle left panel
        // Toggle top panel
        // Toggle right panel
        imageButtonListeners();
        // Terminal method, Holds the code for the terminal
        TerminalMethod();
    }
    
    public void TerminalMethod()
    {
        // Terminal code area
        terminalCodeArea = new CodeArea();
        
        // Scroll bar binds to disable scroll bars for the terminal
        terminalEditorContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        terminalEditorContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        terminalViewWindow.getChildren().add(terminalCodeArea);
        
        terminalViewWindow.prefWidthProperty().bind(terminalEditorContainer.widthProperty());
        terminalViewWindow.prefHeightProperty().bind(terminalEditorContainer.heightProperty());
        
        terminalCodeArea.prefWidthProperty().bind(terminalViewWindow.widthProperty());
        terminalCodeArea.prefHeightProperty().bind(terminalViewWindow.heightProperty());
        
        terminalCodeArea.replaceText(0, 0, "C:\\" + "\\");
    }
    
    public void setupTreeViewContextMenu()
    {
        //ContextMenuInterface contextMenu = new ContextMenuInterface();
        
        treeViewDirectoryListings.setOnContextMenuRequested((ContextMenuEvent event) ->
        {
            ContextMenu menu = new ContextMenu();
            MenuItem rename = new MenuItem("Rename");
            MenuItem delete = new MenuItem("Delete");
            menu.getItems().addAll(rename, delete);
            
            DialogBox Open = new DialogBox();
            
            treeViewDirectoryListings.setContextMenu(menu);
            
            rename.setOnAction(e ->
            {
                System.out.println("Rename");
                
                Open.showRenameBox("Rename...", "Rename " + selectedFile.getName());
                int option = Open.getOption();
                if(option == 1) 
                {
                    System.out.println("Yes");
                } else if(option == -1) 
                {
                    System.out.println("No");
                }
            });
            
            delete.setOnAction(e ->
            {
                System.out.println("Delete");
                
                Open.showDeleteBox("Delete...", "Delete " + selectedFile.getName());
                int option = Open.getOption();
                if(option == 1)
                {
                    selectedFile.delete();
                    FileAddresses.remove(selectedFile.getName());
                    treeViewDirectoryListings.refresh();
                } else if(option == -1)
                {
                    System.out.println("No");
                }
            });
        });
    }
    
    public void handleWebSearchTerms()
    {
        String userSearchQuerie;
        userSearchQuerie = userSearchTextField.getText(); 
        
        final WebEngine webEngine = webViewWindow.getEngine();
                
        if(googleSelected == true)
        {
            webEngine.load("https://www.google.com/search?q=" + userSearchQuerie);            
        } else if(stackSelected == true)
        {
            webEngine.load("https://stackoverflow.com/search?q=" + userSearchQuerie);
        } else {
            // Sets the default search engine for the querie 
            webEngine.load("https://www.google.com/search?q=" + userSearchQuerie);
        }
    }
    
    public void imageButtonListeners()
    {
        searchTermQuerieInitiator.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> 
        {
            handleWebSearchTerms();
            googleSelected = false;
            stackSelected = false;
            event.consume();
        });
        
        searchTargetSelectorGoogle.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> 
        {
            googleSelected = true;
            event.consume();
        });
        
        searchTargetSelectorStack.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> 
        {
            stackSelected = true;
            event.consume();
        });
        
        playSourceImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> 
        {
            compileProject();
            event.consume();
        });
        
        compileSourceImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> 
        {
            compileProject();
            event.consume();
        });
        
        toggleLeftPaneSelectorImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            if(!treeViewDirectoryListings.isVisible())
            {
                fileViewWindow.setVisible(true);
                treeViewDirectoryListings.setVisible(true);

                rootViewWindow.setLeft(treeViewDirectoryListings);
            } else {
                fileViewWindow.setVisible(false);
                treeViewDirectoryListings.setVisible(false);

                rootViewWindow.setLeft(null);
            }
            event.consume();
        });
            
        toggleBottomPaneSelectorImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            if(!terminalCodeArea.isVisible())
            {
                terminalViewWindow.setVisible(true);
                terminalCodeArea.setVisible(true);

                testBorderPane.setBottom(terminalEditorContainer);
            } else {
                terminalViewWindow.setVisible(false);
                terminalCodeArea.setVisible(false);

                testBorderPane.setBottom(null);
            }
            event.consume();
        });
        
        toggleRightPaneSelectorImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            if(!webViewWindow.isVisible())
            {
                webContainer.setVisible(true);
                webViewWindow.setVisible(true);

                rootViewWindow.setRight(webContainer);
            } else {
                webContainer.setVisible(false);
                webViewWindow.setVisible(false);

                rootViewWindow.setRight(null);
            }
            event.consume();
        });
    }
    
    public void keyBindsForMenuOperators()
    {
        codeArea.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() 
        {
            @Override
            public void handle(KeyEvent event) 
            {
                final KeyCombination keysForTab = new KeyCodeCombination(KeyCode.TAB);
                if (keysForTab.match(event)) 
                {
                    codeArea.insertText(codeArea.getCaretPosition(), "    ");
                    event.consume();
                }
                
                // Key code assignment for ctrl s I.e. save.
                final KeyCombination keysForSave = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
                if(keysForSave.match((KeyEvent) event))
                {
                    saveChangesToCurrentFile();
                    event.consume();
                }
            
                // Key code assignment for ctrl z I.e. undo.
                final KeyCombination keysForUndo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
                if(keysForUndo.match((KeyEvent) event))
                {
                    undoOperation();
                    event.consume();
                }

                // Key code assignment for ctrl y I.e. redo.
                final KeyCombination keysForRedo = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
                if(keysForRedo.match((KeyEvent) event))
                {
                    redoOperation();
                    event.consume();
                }

                // Key code assignment for ctrl c I.e. copy.
                final KeyCombination keysForCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
                if(keysForCopy.match((KeyEvent) event))
                {
                    copyOperation();
                    event.consume();
                }

                // Key code assignment for ctrl v I.e. paste.
                final KeyCombination keysForPaste = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
                if(keysForPaste.match((KeyEvent) event))
                {
                    pasteOperation();
                    event.consume();
                }

                // Key code assignment for ctrl x I.e. cut.
                final KeyCombination keysForCut = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);
                if(keysForCut.match((KeyEvent) event))
                {
                    cutOperation();
                    event.consume();
                }
                
                // Key code assignment for ctrl f I.e. find.
                final KeyCombination keysForFind = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
                if(keysForFind.match((KeyEvent) event))
                {
                    findOperation();
                    event.consume();
                }

                // Key code assignment for ctrl a I.e. selectAll.
                final KeyCombination keysForSelectAll = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
                if(keysForSelectAll.match((KeyEvent) event))
                {
                    selectAllOperation();
                    event.consume();
                }

                // Key code assignment for ctrl a I.e. unSelectAll.
                final KeyCombination keysForUnSelectAll = new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN);
                if(keysForUnSelectAll.match((KeyEvent) event))
                {
                    unselectAllOperation();
                    event.consume();
                }
                
                
                // Finish implementing this, brace autocompletion.
//                final KeyCombination keysForAutoCompleteBracket = new KeyCodeCombination(KeyCode.OPEN_BRACKET, KeyCombination.SHIFT_DOWN);
//                if(keysForAutoCompleteBracket.match((KeyEvent) event))
//                {
//                    
//                    Subscription s = codeArea.plainTextChanges().subscribe(tc -> 
//                    {
//                        String removed = tc.getRemoved();
//                        String inserted = tc.getInserted();
//
//                        if (!removed.isEmpty() && inserted.isEmpty())
//                        {
//                            // Deletion
//                        } else if (!inserted.isEmpty() && removed.isEmpty()) 
//                        {
//                            // Insertion
//                            if(inserted.equals("{"))
//                            {
//                                codeArea.replaceText("\n\n}");
//                            }
//                        } else {
//                            // Replacement
//                        }
//                    });
//                    event.consume();
//                }
            }
        });
    }
    protected static String CurrentTheme;
    
//    private void setTheme(String selectedTheme)
//    {
//        
//        UIScene.getStylesheets().clear();
//        UIScene.getStylesheets().add("Themes/" + selectedTheme);
//    }
    
    // Menu selection which adds a seOnAction to each of the menu sub items.
    public void menuItemSelection()
    {            
//        handleThemes.setOnAction(e -> {
//            new ThemesMenu().display(CurrentTheme);
//            CurrentTheme = ThemesMenu.SelectedTheme;
//            setTheme(CurrentTheme);
//        });
        // Action listener for handleOpenFile
        handleOpenFile.setOnAction(e -> openAFile());
        
        // Action listener for handleOpenFolder
        handleOpenFolder.setOnAction(e -> openAFolder());
        
        // Action listener for handleCloseFile    
        handleCloseFile.setOnAction(e -> closeFile());
        
        // Action listener for handleSaveFile
        handleSaveFile.setOnAction(e -> saveChangesToCurrentFile());
        
        // Action listener for handleSaveAs
        handleSaveAs.setOnAction(e -> saveChangesToCurrentFile());
        
        // EDIT ENTRIES
        // Action listener for handleUndo.
        handleUndo.setOnAction(e -> undoOperation());
        
        // Action listener for handleRedo
        handleRedo.setOnAction(e -> redoOperation());
        
        // Action listener for handleCut
        handleCut.setOnAction(e -> cutOperation());
        
        // Action listener for handleCopy
        handleCopy.setOnAction(e -> copyOperation());
        
        // Action listener for handlePaste
        handlePaste.setOnAction(e -> pasteOperation());
        
        // Action listener for handleFind.
        handleFind.setOnAction(e -> findOperation());
        
        // Action listener for handleDelete
        handleDelete.setOnAction(e -> deleteOperation());
        
        // Action listener for handleSelectAll
        handleSelectAll.setOnAction(e -> selectAllOperation());
        
        // Action listener for handleUnselectAll
        handleUnselectAll.setOnAction(e -> unselectAllOperation());
        
        // Action listener for handlePreferences
        handlePreferences.setOnAction(e -> {
            try {
                openThePreferencesOption();
            } catch (IOException IOE) 
            {
                Logger.getLogger(MainScreenViewController.class.getName()).log(Level.SEVERE, null, IOE);
            }
        });
        
        // Action listener for handleMakeNewFile
        handleMakeNewFile.setOnAction(e -> makeNewOperation());
        
        // Code for running a file I.e. run
        runCurrentFile.setOnAction(e -> runProject());
        
        // Code for compiling a file I.e. compile
        compileCurrentFile.setOnAction(e -> compileProject());
        
        handleAbout.setOnAction(e -> openAboutOption());
        // Action listener for quitProgramOption
        handleQuit.setOnAction(e -> exitProgram());
    }
    
    public void syntaxSelectors()
    {
        // Syntax selectors.
        // Java syntax selection.
        selectJavaSyntax.setOnAction(e -> System.out.println("Java"));
        // FXML syntax selection.
        selectFXMLSyntax.setOnAction(e -> System.out.println("FXML"));
        // HTML syntax selection.
        selectHTMLSyntax.setOnAction(e -> System.out.println("HTML"));
        // CSS syntax selection.
        selectCSSSyntax.setOnAction(e -> System.out.println("CSS"));
        // PHP syntax selection.
        selectPHPSyntax.setOnAction(e -> System.out.println("PHP"));
        // JavaScript syntax selection.
        selectJavaScriptSyntax.setOnAction(e -> System.out.println("JavaScript"));
        // C Sharp syntax selection.
        selectCSyntax.setOnAction(e -> System.out.println("C Sharp"));
        // C ++ syntax selection.
        selectCPPSyntax.setOnAction(e -> System.out.println("C ++"));
        // CSS syntax selection.
        selectCSSyntax.setOnAction(e -> System.out.println("CSS"));
        // Python syntax selection.
        selectPySyntax.setOnAction(e -> System.out.println("Python"));
    }
    
    // Code for opening a file.
    public void openAFile()
    {
        if(isModified && isFileOpen)
        {       // File is opened and modified. Will need 'Save'.
            DialogBox Open = new DialogBox();
            Open.showOptionBox("Save...", "Do you wish to save your current file before opening a new one?");
            int option = Open.getOption();
            if(option == 1){
                new FileIO().SaveFile(selectedFile, codeArea.getText());
                selectedFile = new FileIO().OpenFile();
                ReadOpenedFile();
            }
            else if(option == -1){
                selectedFile = new FileIO().OpenFile();
                ReadOpenedFile();
            }
        }
        else if(isModified && !isFileOpen) // File not opened but modified. Will need 'Save As'.
        {     
            DialogBox Open = new DialogBox();
            Open.showOptionBox("Save As...", "Do you wish to save your current file before opening a new one?");
            int option = Open.getOption();
            if(option == 1){
                new FileIO().SaveFileAs(codeArea.getText());
                selectedFile = new FileIO().OpenFile();
                ReadOpenedFile();
            }
            else if(option == -1){
                selectedFile = new FileIO().OpenFile();
                ReadOpenedFile();
            }
        }
        else{
            selectedFile = new FileIO().OpenFile();
            ReadOpenedFile();
        }
        if(selectedFile != null)
        {
            setFileTree(selectedFile);
        }
    }
    
    private void EditorRefresh(String filename) 
    {
        try {
            codeArea.clear();
        } catch(Exception E) {}
    }
    
    private void EditorRefresh() 
    {
        EditorRefresh("Untitled");
    }
    
    private void ReadOpenedFile()
    {
        if(selectedFile != null) 
        {
            BufferedReader BR = null;
            FileContent = "";
            try {
                BR = new BufferedReader(new FileReader(selectedFile));
            } catch (IOException IOE) 
            {
                new DialogBox().showAlertBox("Error!", "File not found.");
            } catch (NullPointerException NPE) {
            }
            try {
                String Line;
                while ((Line = BR.readLine()) != null)
                    FileContent += Line + "\n";
                EditorRefresh(selectedFile.getPath());
            } catch (IOException IOE) 
            {
                new DialogBox().showAlertBox("Error!", "File not found.");
            } catch (NullPointerException NPE) 
            {
                Logger.getLogger(MainScreenViewController.class.getName()).log(Level.SEVERE, null, NPE);
            }
            codeArea.appendText(FileContent);// TODO this may need to be replaceText(0, FileContent)
            
            updateCaretPosition();      // No event happens, hence needs to be manually called.
            
            StringBuffer SB = new StringBuffer(selectedFile.getName());
            int dotPosition = SB.lastIndexOf(".");
            String FileType = SB.substring(dotPosition + 1, SB.length());
            
            String FileName = selectedFile.getName().replaceFirst("[.][^.]+$", "");
            
            switch (FileType) 
            {
                case "c":
                    fileType.setText("File type: C");
                    break;
                case "cpp":
                    fileType.setText("File type: C++");
                    break;
                case "java":
                    fileType.setText("File type: Java");
                    break;
                case "fxml":
                    fileType.setText("File type: fxml");
                    break;
                case "py":
                    fileType.setText("File type: Python");
                    break;
                case "html":
                    fileType.setText("File type: HyperText Markup Language");
                    break;
                case "css":
                    fileType.setText("File type: Cascading Style Sheets");
                    break;
                case "php":
                    fileType.setText("File type: php");
                    break;
                case "txt":
                    fileType.setText("File type: Plain Text");
                    break;
                default:
                    fileType.setText("");
            }
            
            if (selectedFile != null)
            {
                isFileOpen = true;
            } else {
                isFileOpen = false;
            }
        codeArea.moveTo(0, 0);
        codeArea.requestFollowCaret();
        }
    }
    
    private void setEditor()
    {
        codeArea.setOnKeyPressed(e ->
        {
            updateCaretPosition();
            
            if(isModified == false)
            {
                isModified = CheckForModifications();
            }
        });
        
        codeArea.setOnMouseClicked(e -> 
            updateCaretPosition()
        );
    }
    
    private boolean CheckForModifications() 
    {
        if(isFileOpen)
        {
            if(codeArea.getText().equals(FileContent))
            {
                System.out.println("Something");
                return false;
            } else {
                System.out.println("Nothing");
                return true;
            }
        } else {
            if(codeArea.getText() == null)
            {
                return false;
            } else {
                return true;
            }
        }
    }
    
    private void setFileTree(File FileOpened)
    {
        if(selectedFile != null)
        {
            OpenedFileRoot = new TreeItem<>(FileOpened.getParentFile().getName());
            OpenedFileRoot.setExpanded(true);
            treeViewDirectoryListings.setRoot(OpenedFileRoot);
            OpenedFileRoot.getChildren().addAll(getDirectoryContents(FileOpened.getParentFile()));
        }
        else
        {
            treeViewDirectoryListings.setRoot(null);
        }
    }
    
    // Code for opening a folder / directory.
    public void openAFolder()
    {
        DirectoryChooser dc = new DirectoryChooser();
        File choice = dc.showDialog(null);
        
        if(choice == null || ! choice.isDirectory())
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("File type invalid.");

            alert.showAndWait();
        } else {
            
        }
        
        if(isModified && isFileOpen)
        {       // File is opened and modified. Will need 'Save'.
            DialogBox Open = new DialogBox();
            Open.showOptionBox("Save...", "Do you wish to save your current file before opening a new one?");
            int option = Open.getOption();
            if(option == 1){
                new FileIO().SaveFile(selectedFile, codeArea.getText());
                selectedFile = new FileIO().OpenFile();
                ReadOpenedFile();
            }
            else if(option == -1){
                selectedFile = new FileIO().OpenFile();
                ReadOpenedFile();
            }
        }
        else if(isModified && !isFileOpen) // File not opened but modified. Will need 'Save As'.
        {     
            DialogBox Open = new DialogBox();
            Open.showOptionBox("Save As...", "Do you wish to save your current file before opening a new one?");
            int option = Open.getOption();
            if(option == 1){
                new FileIO().SaveFileAs(codeArea.getText());
                selectedFile = new FileIO().OpenFile();
                ReadOpenedFile();
            }
            else if(option == -1){
                selectedFile = new FileIO().OpenFile();
                ReadOpenedFile();
            }
        }
        else{
            selectedFile = new FileIO().OpenFile();
            ReadOpenedFile();
        }
        if(selectedFile != null)
        {
            setFileTree(selectedFile);
        }
    }
    
    public void closeFile()
    {
        saveChangesToCurrentFile();
        codeArea.clear();
    }
    
    // Save files function, will also work for the folder code as it saves the files in 
    // a stand alone manner.
    private void saveChangesToCurrentFile()
    {
        if (isFileOpen)
        {
            new FileIO().SaveFile(selectedFile, codeArea.getText());
        } else {
            selectedFile = new FileIO().SaveFileAs(codeArea.getText());
            ReadOpenedFile();
            setFileTree(selectedFile);
        }
    }
    
    public void undoOperation()
    {
        codeArea.undo();
    }
    
    public void redoOperation()
    {
        codeArea.redo();
    }
    
    public void cutOperation()
    {
        // cut method taken directly from the RichTextFX codeArea.
        codeArea.cut();
    }
    
    public void copyOperation()
    {
        // copy method taken directly from the RichTextFX codeArea.
        codeArea.copy();
    }
    
    public void pasteOperation()
    {
        // paste method taken directly from the RichTextFX codeArea.
        codeArea.paste();
    }
    
    public void findOperation()
    {
//        // TODO Implement recursiveness.
        if (findTextField.getText() != null && !findTextField.getText().isEmpty())
        {
            int index = codeArea.getText().indexOf(findTextField.getText());
            
            if (index == -1) 
            {
                System.out.println("Search key Not in the text");
            } else {
                codeArea.selectRange(index, index + findTextField.getLength());    
            }
        } else {
            System.out.println("Missing search key");
        }
    }
    
    public void selectAllOperation()
    {
        // selectAll method taken directly from the RichTextFX codeArea.
        codeArea.selectAll();
    }
    
    public void unselectAllOperation()
    {
        // unselectAll method taken directly from the RichTextFX codeArea.
        codeArea.deselect();
    }
    
    public void deleteOperation()
    {
        System.out.println("Delete");
        
        selectedFile.delete();
        
        TreeItem c = (TreeItem)treeViewDirectoryListings.getSelectionModel().getSelectedItem();
        c.getParent().getChildren().remove(c);

        // setFileTree(selectedFile);
    }
    
    public void makeNewOperation()
    {
        // TODO Finnish this
        if(isModified && isFileOpen)
        {       // File opened and modified
            DialogBox New = new DialogBox();
            New.showOptionBox("Save...", "Do you wish to save your current file before opening a new one?");
            int option = New.getOption();
            if(option == 1) 
            {
                new FileIO().SaveFile(selectedFile, codeArea.getText());
                EditorRefresh();
            } else if(option == -1)
            {
                EditorRefresh();
            }
        } else if(isModified && !isFileOpen)
        {     // No file opened but modified
            DialogBox New = new DialogBox();
            New.showOptionBox("Save...", "Do you wish to save your current file before opening a new one?");
            int option = New.getOption();
            if(option == 1)
            {
                new FileIO().SaveFileAs(codeArea.getText());
                EditorRefresh();
            } else if(option == -1)
            {
                EditorRefresh();
            }
        } else
        {
            EditorRefresh();
            isFileOpen = false;
            selectedFile = null;
            saveChangesToCurrentFile();
            setFileTree(selectedFile);
        }
    }
    
    public void openThePreferencesOption() throws IOException
    {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PreferencesScreenView.fxml"));
            Parent rootPreferences = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(rootPreferences));  
            stage.show();
            
        } catch(IOException e) 
        {
            Logger.getLogger(MainScreenViewController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void openAboutOption()
    {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AboutScreenView.fxml"));
            Parent rootPreferences = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(rootPreferences));  
            stage.show();
            
        } catch(IOException IOE) 
        {
            Logger.getLogger(MainScreenViewController.class.getName()).log(Level.SEVERE, null, IOE);
        }
    }
    
    public void runProject()
    {   
        System.out.println("Run project");
        String result = "";
        String resultError = "";
        
        try{
            String FileName = selectedFile.getName().replaceFirst("[.][^.]+$", "");
            
            System.out.println(selectedFile.getName().replaceFirst("[.][^.]+$", ""));
            
            Process p = r.exec(pathToJavaRuntime + FileName);
            BufferedReader error;
            try (BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()))) 
            {
                error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while(true)
                {
                    String temp = output.readLine();
                    if(temp != null)
                    {
                        result += temp;
                        result += "\n";
                    }else{
                        break;
                    }
                }   
                
               while(true)
                {
                    String temp = error.readLine();
                    if(temp != null)
                    {
                        resultError += temp;
                        resultError += "\n";
                    }else{
                        break;
                    }
                }
            }
            
        error.close();
        System.out.println("Result" + result + "\n" + resultError);

        }catch(IOException ex) 
        {
            Logger.getLogger(MainScreenViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void compileProject()
    {
        System.out.println("Compile project");
        String result = "";

        try{
            //saveChangesToCurrentFile();
            
            Process error = r.exec(pathToJavaCompile + selectedFile.getAbsoluteFile());
            BufferedReader err = new BufferedReader(new InputStreamReader(error.getErrorStream()));
            
            while(true)
            {
                String temp= err.readLine();

                if(temp != null)
                {
                    result += temp;
                    result += "\n";
                } else {

                    break;
                }
            }

            if(result.equals(""))
            {
                Notifications notification = Notifications.create();
                
                    notification.title("Compile");
                    notification.text(selectedFile.getName() + " compiled successfully!");
                    notification.graphic(null);
                    notification.hideAfter(Duration.seconds(2));
                    notification.darkStyle();
                    notification.show();
                
                System.out.println("Compilation Successful " + selectedFile.getName());
                err.close();
            } else {
                System.out.println(result);
            }
        }catch(IOException ex) 
        {
            Logger.getLogger(MainScreenViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void StartupSettingsLoader()
    {
        BufferedReader BR;
        
//        try {
//            File SettingsFile = new File("Settings.properties");
//            Properties Settings = new Properties();
//            BR = new BufferedReader(new FileReader(SettingsFile.getAbsolutePath()));
//            Settings.load(BR);
//
//            if(Settings.getProperty("OpenedFile").equals("NoFileFound"))
//            {
//                //throw new IOException();
//                // TODO possibly use this for something
//            } else {
//                selectedFile = new File(Settings.getProperty("OpenedFile"));
//                ReadOpenedFile();
//                setFileTree(selectedFile);
//            }
//            BR.close();
//        } catch (IOException IOE)
//        {
//            Logger.getLogger(MainScreenViewController.class.getName()).log(Level.SEVERE, null, IOE);
//        }
    }
    
    public void exitProgram()
    {
        executor.shutdown();
        
        try {
            BufferedWriter BW;
            File ThemesConfig = new File("Settings.properties");
            BW = new BufferedWriter(new FileWriter(ThemesConfig.getAbsolutePath()));
            Properties Settings = new Properties();
            
            if(isFileOpen)
            {
                // When the program exits, this saves the current file path, file name to OpenedFile.
                Settings.setProperty("OpenedFile", selectedFile.getAbsolutePath());
                
                // When the program exits, this saves the current paragraph position of the caret to CurrentCaretParagraph.
                Settings.setProperty("CurrentCaretParagraph", Integer.toString(codeArea.getCurrentParagraph() + 1));
                // When the program exits, this saves the current column position of the caret to CurrentCaretColumn.
                Settings.setProperty("CurrentCaretColumn", Integer.toString(codeArea.getCaretColumn() + 1));
            } else {
                // When the program exits, if there was no file open this save OpenedFile to NoFileFound.
                Settings.setProperty("OpenedFile", "NoFileFound");
            }
            Settings.store(BW, null);
            BW.close();
        } catch (IOException IOE) 
        {
            Logger.getLogger(MainScreenViewController.class.getName()).log(Level.SEVERE, null, IOE);
        } 
        catch (NullPointerException NPE) 
        {
            Logger.getLogger(MainScreenViewController.class.getName()).log(Level.SEVERE, null, NPE);
        }
        
        if (isModified && isFileOpen) 
        {
            saveChangesToCurrentFile();
        } else if(isModified) 
        {
            DialogBox Exit = new DialogBox();
            Exit.showOptionBox("Save...", "Do you wish to save your current file before exiting?");
            int option = Exit.getOption();
            if(option == 1)
            {
                new FileIO().SaveFile(selectedFile, codeArea.getText());
            }
        }
        
        System.exit(0);
    }
    
    private void updateCaretPosition()
    {
        caretPosition.setText("Line: " + Integer.toString(codeArea.getCurrentParagraph() + 1) 
                + " | " + "Column:" + Integer.toString(codeArea.getCaretColumn() + 1));
    }
    
    private TreeItem<String>[] getDirectoryContents(File RootDir)
    {
        int RootLength = RootDir.listFiles().length;
        TreeItem[] RootNodes;

        if(RootLength == 0)
        {
            RootNodes = new TreeItem[1];
            RootNodes[0] = new TreeItem("Directory empty");
        } else{
            RootNodes = new TreeItem[RootLength];
            File[] RootFiles = RootDir.listFiles();
            int i = 0;
            for(File RF: RootFiles)
            {
                if(RF.isFile()) 
                {
                    RootNodes[i] = new TreeItem();
                    RootNodes[i].setValue(RF.getName());
                    FileAddresses.put(RF.getName(), RF.getAbsolutePath());
                } else if(RF.isDirectory())
                {
                    RootNodes[i] = new TreeItem(RF.getAbsoluteFile().getName());
                    RootNodes[i].getChildren().addAll(getDirectoryContents(RF));
                }
                i++;
            }
        }
        return RootNodes;
    }
    
    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync()
    {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>()
        {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception
            {
                return computeJavaHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) 
    {
        codeArea.setStyleSpans(0, highlighting);
    }

    private StyleSpans<Collection<String>> computeJavaHighlighting(String text) 
    {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) 
        {
            String styleClass =
                matcher.group("KEYWORD") != null ? "keyword" :
                matcher.group("PAREN") != null ? "paren" :
                matcher.group("BRACE") != null ? "brace" :
                matcher.group("BRACKET") != null ? "bracket" :
                matcher.group("SEMICOLON") != null ? "semicolon" :
                matcher.group("STRING") != null ? "string" :
                matcher.group("COMMENT") != null ? "comment" :
                null; /* never happens */ assert styleClass != null;
                
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    
    public String getSelectedFile()
    {
        return selectedFile.getName();
    }
    
    public void setSelectedFile(String fileName)
    {
        fileName = selectedFile.getName();
    }
    
    @Override
    public void setScreenParent(ScreensController screenParent)
    {
        myController = screenParent;
    }
}