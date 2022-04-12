package com.commandoby.swingImagesBrowser;

import java.util.List;

import javax.swing.SwingUtilities;

import com.commandoby.swingImagesBrowser.components.ImageUnit;
import com.commandoby.swingImagesBrowser.service.ImageReader;
import com.commandoby.swingImagesBrowser.service.impl.ImageReaderImpl;
import com.commandoby.swingImagesBrowser.swing.ImageBrowser;

public class Application {
private static final ImageReader imageReader = new ImageReaderImpl();
    
    public static String path = "assets";
    public static List<ImageUnit> images;

    public static void main(String[] args) {
    	images = imageReader.read(path);
        SwingUtilities.invokeLater(new ImageBrowser()::run);
    }
}
