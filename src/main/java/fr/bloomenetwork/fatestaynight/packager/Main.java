package fr.bloomenetwork.fatestaynight.packager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main extends JFrame {
	
	//Parameters
    private String outputFolder = "package";
    
    //JFrame Components
    private JButton connectionButton;
    private JButton generateButton;
    private JTextField outputFolderTextField;
    private JTextArea textOutput;
    private JProgressBar progressBar;
    
    private GoogleAPI googleAPI = null;
    
    public Main() {
    	connectionButton = new JButton("Connexion");
    	generateButton = new JButton("Go !");
    	generateButton.setEnabled(false);
    	outputFolderTextField = new JTextField(outputFolder);
    	textOutput = new JTextArea();
    	textOutput.setRows(15);
    	textOutput.setEditable(false);
    	PrintStream printStream = new PrintStream(new TextAreaOutputStream(textOutput), true, StandardCharsets.UTF_8);
    	System.setOut(printStream);
    	System.setErr(printStream);
    	JPanel topPane = new JPanel();
    	progressBar = new JProgressBar();
    	progressBar.setMinimum(0);
    	progressBar.setStringPainted(true);
    	
    	connectionButton.addActionListener(e -> {
			try {
				googleAPI = new GoogleAPI();
				connectionButton.setEnabled(false);
				connectionButton.setText("Connecté");
				generateButton.setEnabled(true);
				textOutput.append("Connecté à l'API Google Drive.\n");
			} catch (GeneralSecurityException | IOException e1) {
				System.out.println(e1.toString());
			}
		});
    	
    	generateButton.addActionListener(e -> {
    		generateButton.setEnabled(false);
    		FetchingThread ft = new FetchingThread(googleAPI, progressBar, generateButton);
    		ft.setOutputFolder(this.outputFolder);
    		Thread t = new Thread(ft);
    		t.start();
    	});
    	
    	this.setTitle("Fate/Stay Night Packager");
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.setSize(600, 400);
    	topPane.add(new JLabel("API Google : "));
    	topPane.add(connectionButton);
    	topPane.add(new JLabel("Générer les fichiers : "));
    	topPane.add(generateButton);
    	topPane.add(new JLabel("Répertoire de sortie : "));
    	topPane.add(outputFolderTextField);
    	topPane.setLayout(new GridLayout(3, 2));    
    	
    	this.add(topPane, BorderLayout.NORTH);
    	this.add(progressBar, BorderLayout.CENTER);
    	JScrollPane js = new JScrollPane(textOutput);
    	js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    	this.add(js, BorderLayout.SOUTH);
    	
    	this.setLocationRelativeTo(null);
    	this.setVisible(true);
    }


	public static void main(String[] args) {
		Main main = new Main();
	}

}
