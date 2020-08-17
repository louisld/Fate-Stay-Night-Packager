package fr.bloomenetwork.fatestaynight.packager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GoogleAPI {
	
	private static final String APPLICATION_NAME = "Fate Stay Night Packager";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    
    private Drive service;
    
    public GoogleAPI() throws GeneralSecurityException, IOException {
    	initGoogleService();
    }
	
    //Fonction donnée par la documentation de l'API
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = Main.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    
	//Initialisation du service
    private void initGoogleService() throws GeneralSecurityException, IOException {
    	// Build a new authorized API client service.
    	final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        service = new Drive.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    //Donne la liste des sous-dossiers du dossier passé en paramètres
    //L'option filter peut être utilisé pour passer d'autres paramètres
    public List<File> getSubFiles(String id, String filter) throws IOException {
    	FileList list = service.files().list()
    			.setFields("nextPageToken, files(id, name)")
    			.setQ("'" + id + "' in parents " + filter)
    			.execute();
    	return list.getFiles();
    }
    
    //Permet d'obtenir l'id d'un dossier à partir de son nom
    //Le nom doit être unique, sinon on peut avoir un autre dossier
    public String getFolderIdByName(String name) throws Exception {
    	if(service == null) throw new Exception();
    	String pageToken = null;
    	do {
		  FileList result = service.files().list()
		      .setQ("name = '" + name + "'")
		      .setSpaces("drive")
		      .setFields("nextPageToken, files(id, name)")
		      .setPageToken(pageToken)
		      .execute();
		  for (File file : result.getFiles()) {
		    System.out.println("Répertoire " + name + " trouvé.\n");
		    return file.getId();
		  }
		  pageToken = result.getNextPageToken();
		} while (pageToken != null);
    	
    	return null;
    }
    
    //Retourne le contenu d'un Google Doc sous la forme d'un String
    //Utilisation de l'InputStream peut être un peu astucieuse
    //Il vaut peut être mieux utiliser l'API Google Docs
    public String getGdoc(String id) throws Exception {
        if(service == null) throw new Exception();
        InputStream inputStream = service.files().export(id, "text/plain")
            .executeMediaAsInputStream();
        
        inputStream.read();
        inputStream.read();
        inputStream.read();
        
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

}
