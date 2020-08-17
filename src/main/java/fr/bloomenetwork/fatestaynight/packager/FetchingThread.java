package fr.bloomenetwork.fatestaynight.packager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JProgressBar;

import com.google.api.services.drive.model.File;

public class FetchingThread implements Runnable {
	
	private GoogleAPI googleAPI;
    private static Pattern pattern;
    private static Matcher matcher;
    
    private static final String[] routes = {"セイバー", "凛", "桜"};
    
    private String outputFolder;
    private JProgressBar progressBar;
    private JButton generateButton;
	
	public FetchingThread(GoogleAPI googleAPI, JProgressBar progressBar, JButton generateButton) {
		this.googleAPI = googleAPI;
		this.outputFolder = "package";
		this.progressBar = progressBar;
		this.generateButton = generateButton;
	}
	
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}
	
	public void run() {
		String rootFolder = null;
		try {
			rootFolder = googleAPI.getFolderIdByName("Fate Stay Night");
		} catch (Exception e1) {
			System.out.println(e1.toString());
		}
		
		if(rootFolder != null) {
			List<File> routeFolders = null;
			try {
				routeFolders = googleAPI.getSubFiles(rootFolder, " and mimeType = 'application/vnd.google-apps.folder'");
			} catch (IOException e1) {
				System.out.println(e1.toString());
			}
			
			ArrayList<File> listGdocs = new ArrayList<File>();
			
			for(File routeFolder : routeFolders) {
				try {
					List<File> dayFolders = googleAPI.getSubFiles(routeFolder.getId(), " and mimeType = 'application/vnd.google-apps.folder'");
					for(File dayFolder : dayFolders) {
						listGdocs.addAll(googleAPI.getSubFiles(dayFolder.getId(), " and mimeType = 'application/vnd.google-apps.document'"));
					}
				} catch (IOException e1) {
					System.out.println(e1.toString());
				}
			}
			
			progressBar.setMaximum(listGdocs.size());
			
			int i = 0;
			
			for(File file : listGdocs) {
				i++;
				progressBar.setValue(i);
				try {
					String content = googleAPI.getGdoc(file.getId());
					System.out.println("Évaluation du fichier " + file.getName());
					
					pattern = Pattern.compile("@resetvoice route=(\\w+) day=(\\d+) scene=(\\d+)");
					matcher = pattern.matcher(content);
					
					ArrayList<String> scriptInfos = new ArrayList<String>();
					while(matcher.find()) {
						scriptInfos.add(matcher.group(1));
						scriptInfos.add(matcher.group(2));
						scriptInfos.add(matcher.group(3));
					}
					
					String filename = "";
					switch(scriptInfos.get(0)) {
						case "saber":
							filename += routes[0];
							break;
						case "rin":
							filename += routes[1];
							break;
						case "sakura":
							filename += routes[2];
							break;
						default:
							break;
					}
					filename += "ルート";
					filename += Utils.numberToJapaneseString(Integer.parseInt(scriptInfos.get(1))) + "日目";
					filename += "-" + String.format("%02d", Integer.parseInt(scriptInfos.get(2))) + ".ks";
					
					java.nio.file.Files.write(Paths.get(outputFolder + "/" + filename), content.getBytes(StandardCharsets.UTF_8));
					System.out.println("\tFichier " + filename +" écrit.");
					
				} catch (IOException e1) {
					System.out.println("Erreur lors de l'écriture.");
				} catch (Exception e1) {
					System.out.println("Fichier invalide.");
				}
			}
			System.out.println("Fini !");
		} else {
			System.out.println("Le répertoire de base n'a pas été trouvé.");
		}
		generateButton.setEnabled(true);
	}
	
	
}
