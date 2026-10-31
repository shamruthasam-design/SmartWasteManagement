package main;

import ui.Theme;

import javax.swing.*;

/** Application entry point. Run this class from IntelliJ IDEA. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.install();
            new SplashScreen().start();
        });
    }
}
