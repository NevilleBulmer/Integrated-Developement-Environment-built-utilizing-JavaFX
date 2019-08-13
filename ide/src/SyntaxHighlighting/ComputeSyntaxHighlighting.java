/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SyntaxHighlighting;

import java.util.regex.Pattern;

/**
 *
 * @author neville
 */
public class ComputeSyntaxHighlighting 
{
    
    private static final String[] JAVA_KEYWORDS = new String[]
    {
        "abstract", "assert", "boolean", "break", "byte",
        "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else",
        "enum", "extends", "final", "finally", "float",
        "for", "goto", "if", "implements", "import",
        "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super",
        "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while"
    };
    
    private static final String[] FXML_KEYWORDS = new String[]
    {
        "import", "AnchorPane", "children", "BorderPane", "bottom", 
        "SplitPane", "top", "left", "bottom", "right", "VBox", "center", 
        "HBox", "MenuBar", "Menu", "menus", "item", "items",
        "MenuItem", "SeparatorMenuItem", "padding", "font", "Font",
        "Insets", "ImageView", "Image", "TextField", "ScrollPane", 
        "WebView", "Label", "Pane", "Hyperlink", "MenuButton",
        "image", "content"
        
    };

    public String[] ComputeJavaSyntax()
    {
        return JAVA_KEYWORDS;
    }
    
    public String[] ComputeFXMLSyntax()
    {
        return FXML_KEYWORDS;
    }
}
