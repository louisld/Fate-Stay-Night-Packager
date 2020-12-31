package fr.bloomenetwork.fatestaynight.packager;

import java.io.IOException;
import java.io.InputStream;
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
    
    //Listes du nom des routes pour le nom des fichiers
    private static final String[] routes = {"セイバー", "凛", "桜"};
    
    //Composants graphiques
    private String outputFolder;
    private JProgressBar progressBar;
    private JButton generateButton;
	
	public FetchingThread(GoogleAPI googleAPI, JProgressBar progressBar, JButton generateButton) {
		this.googleAPI = googleAPI;
		this.outputFolder = "package";
		this.progressBar = progressBar;
		this.generateButton = generateButton;
	}
	
	//Permet de définir le répertoire de sortie
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}
	
	//Implémentation de l'interface Runnable
	//Thread qui télécharge les scripts
	public void run() {
		
		//Récupération du dossier racine grâce à son nom
		String rootFolder = null;
		try {
			rootFolder = googleAPI.getFolderIdByName("Fate Stay Night");
		} catch (Exception e1) {
			Utils.print(e1.toString(), Utils.ERROR);
		}
		
		if(rootFolder != null) {
			//On récupère les sous-dossiers, qui correspondent aux différents routes
			List<File> routeFolders = null;
			try {
				routeFolders = googleAPI.getSubFiles(rootFolder, " and mimeType = 'application/vnd.google-apps.folder'");
			} catch (IOException e1) {
				Utils.print(e1.toString(), Utils.ERROR);
			}
			
			//On récupère ensuite tous les Google Docs qui se trouve dans les sous-dossiers,
			//ceux correspondants aux jours, des dossiers des routes.
			ArrayList<File> listGdocs = new ArrayList<File>();
			
			for(File routeFolder : routeFolders) {
				try {
					List<File> dayFolders = googleAPI.getSubFiles(routeFolder.getId(), " and mimeType = 'application/vnd.google-apps.folder'");
					for(File dayFolder : dayFolders) {
						listGdocs.addAll(googleAPI.getSubFiles(dayFolder.getId(), " and mimeType = 'application/vnd.google-apps.document'"));
					}
				} catch (IOException e1) {
					Utils.print(e1.toString(), Utils.ERROR);
				}
			}
			
			progressBar.setMaximum(listGdocs.size());
			Utils.print("Nombre de fichiers à télécharger : " + listGdocs.size() + ".");
			
			int i = 0;
			
			//Boucle qui télécharge chaque Google Doc
			for(File file : listGdocs) {
				//Màj de la progress bar
				i++;
				progressBar.setValue(i);
				try {
					//On récupère le contenu du fichier
					String content = googleAPI.getGdoc(file.getId());
					Utils.print("Évaluation du fichier " + file.getName());
					Utils.print("\tId : " + file.getId());
					
					//On vérifie que c'est bien un fichier de script
					//et on en extrait les informations grâce à une regex
					pattern = Pattern.compile("@resetvoice route=(\\w+) day=(\\d+) scene=(\\d+)");
					matcher = pattern.matcher(content);
					
					//Un peu fragile ici
					//La boucle n'est censé faire qu'un tour
					//Il ne faut pas qu'il y ait de conflit dans la regex
					ArrayList<String> scriptInfos = new ArrayList<String>();
					while(matcher.find()) {
						scriptInfos.add(matcher.group(1));
						scriptInfos.add(matcher.group(2));
						scriptInfos.add(matcher.group(3));
					}
					
					//Génération du nom du fichier
					String filename = "";
					//D'abord le nom de la route
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
					//Le mot route
					filename += "ルート";
					//Le jour
					filename += Utils.numberToJapaneseString(Integer.parseInt(scriptInfos.get(1))) + "日目";
					//Et enfin la scène et l'extension .ks
					filename += "-" + String.format("%02d", Integer.parseInt(scriptInfos.get(2))) + ".ks";
					
					//On écrit le docx
					Utils.print("\tTéléchargement du fichier docx et conversion.");
					InputStream docxStream = googleAPI.getDocx(file.getId());
					//On convertit et enfin on écrit le fichier
					Utils.docxToKsFile(docxStream, outputFolder + "/" + filename);
					Utils.print("\tFichier " + filename +" écrit.");
					
				} catch (IOException e1) {
					Utils.print("Erreur lors de l'écriture.", Utils.ERROR);
				} catch (Exception e1) {
					Utils.print("Fichier invalide.", Utils.ERROR);
				}
			}
			Utils.print("Fini !");
		} else {
			Utils.print("Le répertoire de base n'a pas été trouvé.", Utils.ERROR);
		}
		generateButton.setEnabled(true);
	}
	
	
}
