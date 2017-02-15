package com.deltabase.everphase.launcher;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Launcher extends JFrame {

    private JPanel rootPanel;
    private JTabbedPane tabbedPane1;
    private JButton start;
    private JCheckBox VSyncCheckBox;
    private JCheckBox anisotropicFilterCheckBox;
    private JCheckBox antialiasingCheckBox;
    private JCheckBox bloomCheckBox;
    private JTextPane console;
    private JCheckBox fullscreenCheckBox;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JEditorPane editorPane1;
    private JScrollPane browserScrollPane;
    private JFXPanel jfxpanel;
    private JComboBox mipmapType;
    private JSlider mipmapLevel;
    private JComboBox shadowQuality;
    private JLabel mipmapLevelValue;
    private JCheckBox debugMessage;
    private JButton btnSave;
    private WebView webView;

    private Thread gameThread;


    private TextAreaOutputStream consoleStream = new TextAreaOutputStream(console);

    public Launcher() {
        super("EverPhase Launcher");
        long timeStamp = System.currentTimeMillis();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentPane(rootPanel);

        Platform.runLater(() -> {
            webView = new WebView();

            Scene scene = createWebScene();
            jfxpanel.setScene(scene);
        });

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1080, 675));
        setMinimumSize(new Dimension(1080, 675));
        setMaximumSize(new Dimension(1080, 675));
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
        mipmapLevelValue.setText(Integer.toString(mipmapLevel.getValue()));

        this.readSettings();

        start.addActionListener((e -> {
            List<String> args = new ArrayList<>();

            args.add("java");
            args.add("-jar");
            args.add("everphase.jar");

            args.add("fullscreen");
            if (VSyncCheckBox.isSelected()) args.add("vsync");
            if (anisotropicFilterCheckBox.isSelected()) args.add("anisotropicfilter");
            if (antialiasingCheckBox.isSelected()) args.add("antialiasing");
            if (bloomCheckBox.isSelected()) args.add("bloom");
            if (debugMessage.isSelected()) args.add("debug");

            args.add("mipmapLevel_" + mipmapLevel.getValue());
            args.add("mipmapType_" + mipmapType.getSelectedIndex());
            args.add("shadowQuality_" + shadowQuality.getSelectedIndex());

            String[] argsArray = args.toArray(new String[0]);

            File file = new File("../log/" + timeStamp + ".log");

            new File(file.getParent()).mkdir();
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            new Thread(() -> {
                while (true) {
                    try {
                        console.setText(new String(Files.readAllBytes(Paths.get("../log/" + timeStamp + ".log"))));
                        Thread.sleep(10);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();

            new Thread(() -> {
                try {
                    ProcessBuilder pb = new ProcessBuilder(argsArray).redirectOutput(file);
                    Process p = pb.start();
                    p.waitFor();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }).start();

            tabbedPane1.setSelectedIndex(1);
        }));
        mipmapLevel.addChangeListener((e) -> mipmapLevelValue.setText(Integer.toString(mipmapLevel.getValue())));
        btnSave.addActionListener(e -> {
            saveSettings();
        });
    }

    public static void main(String[] args) {
        new Launcher();
    }

    private Scene createWebScene() {
        AnchorPane anchorPane = new AnchorPane();

        Scene scene = new Scene(anchorPane, Color.ALICEBLUE);
        WebView webView = new WebView();

        AnchorPane.setTopAnchor(webView, 0.0);
        AnchorPane.setBottomAnchor(webView, 0.0);
        AnchorPane.setLeftAnchor(webView, 0.0);
        AnchorPane.setRightAnchor(webView, 0.0);
        anchorPane.getChildren().add(webView);

        WebEngine engine = webView.getEngine();
        engine.load("https://werwolv98.github.io/");

        return scene;
    }

    public void saveSettings() {
        try {
            File file = new File(System.getProperty("user.home") + "/everphase/settings.dat");
            File parent = file.getParentFile();
            List<String> lines = new ArrayList<>();

            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }

            file.delete();
            file.createNewFile();

            lines.add("fullscreen");
            if (VSyncCheckBox.isSelected()) lines.add("vsync");
            if (anisotropicFilterCheckBox.isSelected()) lines.add("anisotropicfilter");
            if (antialiasingCheckBox.isSelected()) lines.add("antialiasing");
            if (bloomCheckBox.isSelected()) lines.add("bloom");
            if (debugMessage.isSelected()) lines.add("debug");

            lines.add("mipmapLevel:" + mipmapLevel.getValue());
            lines.add("mipmapType:" + mipmapType.getSelectedIndex());
            lines.add("shadowQuality:" + shadowQuality.getSelectedIndex());

            Files.write(file.toPath(), lines, Charset.forName("UTF-8"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readSettings() {
        File file = new File(System.getProperty("user.home") + "/everphase/settings.dat");
        List<String> lines;

        if (!file.exists())
            return;

        try {
            lines = Files.readAllLines(file.toPath());
            lines.forEach(e -> {
                String[] line = e.split(":");

                switch (line[0]) {
                    case "vsync":
                        VSyncCheckBox.setSelected(true);
                        break;
                    case "anisotropicfilter":
                        anisotropicFilterCheckBox.setSelected(true);
                        break;
                    case "antialiasing":
                        antialiasingCheckBox.setSelected(true);
                        break;
                    case "bloom":
                        bloomCheckBox.setSelected(true);
                        break;
                    case "debug":
                        debugMessage.setSelected(true);
                        break;
                    case "mipmapLevel":
                        mipmapLevel.setValue(Integer.parseInt(line[1]));
                        break;
                    case "mipmapType":
                        mipmapType.setSelectedIndex(Integer.parseInt(line[1]));
                        break;
                    case "shadowQuality":
                        shadowQuality.setSelectedIndex(Integer.parseInt(line[1]));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void frameInit() {
        super.frameInit();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
