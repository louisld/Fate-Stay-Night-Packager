package fr.bloomenetwork.fatestaynight.packager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
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
	
	//Paramètres
    private String outputFolder = "package";
    
    //Composants graphiques
    private JButton connectionButton;
    private JButton generateButton;
    private JTextField outputFolderTextField;
    private JTextArea textOutput;
    private JProgressBar progressBar;
    
    //Gestion de l'API Google Drive
    private GoogleAPI googleAPI = null;
    
    public Main() {
    	
    	//Configuration des diverses éléments graphique
    	connectionButton = new JButton("Connexion");
    	generateButton = new JButton("Go !");
    	generateButton.setEnabled(false);
    	outputFolderTextField = new JTextField(outputFolder);
    	textOutput = new JTextArea();
    	textOutput.setRows(15);
    	textOutput.setEditable(false);
    	System.setOut(new PrintStreamCapturer(textOutput, System.out));
    	System.setErr(new PrintStreamCapturer(textOutput, System.err, "[ERROR]"));
    	JPanel topPane = new JPanel();
    	progressBar = new JProgressBar();
    	progressBar.setMinimum(0);
    	progressBar.setStringPainted(true);
    	
    	//Listener sur le premier bouton qui permet d'initialiser le service de l'API Google
    	connectionButton.addActionListener(e -> {
			try {
				googleAPI = new GoogleAPI();
				connectionButton.setEnabled(false);
				connectionButton.setText("Connecté");
				generateButton.setEnabled(true);
				Utils.print("Connecté à l'API Google Drive.\n");
			} catch (GeneralSecurityException | IOException e1) {
				Utils.print(e1.toString(), Utils.ERROR);
			}
		});
    	
    	//Listener sur le deuxième bouton qui lance un second thread
    	//Ce thread se charge de télécharger tous les fichiers de script
    	generateButton.addActionListener(e -> {
    		generateButton.setEnabled(false);
    		FetchingThread ft = new FetchingThread(googleAPI, progressBar, generateButton);
    		ft.setOutputFolder(this.outputFolder);
    		Thread t = new Thread(ft);
    		t.start();
    	});
    	
    	//Mise en page de la fenêtre
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
