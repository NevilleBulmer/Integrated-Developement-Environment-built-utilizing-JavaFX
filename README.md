## Integrates developement built using JavaFX.

A fully fnctional integrated development environment using javafx, this IDE has completed functions and partially-fnctions.

### Fully Functional.
The IDE has full syntax highlighting for a handfull of languages along with language auto detection.
The IDE has compilation fully incoporated for windows and mac (providing that the Java JDK is installed).

The compilation is incorporated using variables to store the location of the javac, this is done by auto-detecting the running operating system, below is an example of the variables and the implementation.

Lines 151 - 160 Class MainScreenViewController.java
```
private final String MAC_PATH_TO_JC = "/Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/bin/javac ";

private final String MAC_PATH_TO_JAVA = "/Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/bin/java ";

private final String WINDOWS_PATH_TO_JC = "C:\\Program Files\\Java\\jdk1.8.0_191\\bin\\javac.exe ";

private final String WINDOWS_PATH_TO_JAVA = "C:\\Program Files\\Java\\jdk1.8.0_191\\bin\\java.exe ";

```

Lines 271 - 188 Class MainScreenViewController.java
```
if(detectedOS.equals("Windows 10"))
{
    pathToJavaCompile = WINDOWS_PATH_TO_JC;

    pathToJavaRuntime = WINDOWS_PATH_TO_JAVA;
}

else if(detectedOS.equals("Mac OS X"))
{
    pathToJavaCompile = MAC_PATH_TO_JC;

    pathToJavaRuntime = MAC_PATH_TO_JAVA;
}

```


### Partially_Functional.
Syntax highlighting is in place for a hand full of programming languages, more work needed.

### Future Development Ideas.
Incorporate syntax highlighting for most if not all programming languages.
Implement compilations and interpreters for most if not all programming languages.

### Other
With this IDE utilizing RichTextFX, ControlsFX there is a dependencies  folder which will include everything needed to get up and running with the project.

### Checklist of thing to complete
More syntax highlighting (language spesific)
More language compilation and running functionality
