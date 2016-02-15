package com.localidata;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import com.google.api.services.drive.model.User;


public class GoogleDriveAPI {

	private static String domainPermission = "";

	private static String p12File = "";

	private static String acountId = "";

	private final static Logger log = Logger.getLogger(GoogleDriveAPI.class);

	private static String APPLICATION_NAME = "";

	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	private static HttpTransport HTTP_TRANSPORT;

	private static final List<String> SCOPES = Arrays.asList(
			DriveScopes.DRIVE_METADATA_READONLY, DriveScopes.DRIVE,
			DriveScopes.DRIVE_APPDATA, DriveScopes.DRIVE_APPS_READONLY,
			DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_METADATA,
			DriveScopes.DRIVE_READONLY,
			"https://www.googleapis.com/auth/drive.install");

	private Drive service = null;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	public void init() {

		try {
			boolean resultConf = loadConf();
			if (resultConf)
				this.service = getDriveService();
			else
				log.error("Error in load configuration");
		} catch (IOException e) {
			log.error("Error init method", e);
		}
	}

	private static boolean loadConf() {

		boolean conf = false;

		Properties prop = new Properties();

		try {
			InputStream input = new FileInputStream("system.properties");
			prop.load(input);

			domainPermission = prop.getProperty("domainPermission");
			p12File = prop.getProperty("p12File");
			acountId = prop.getProperty("acountId");
			APPLICATION_NAME = prop.getProperty("APPLICATION_NAME");
			conf = true;
		} catch (IOException io) {
			log.error("Error loading configuration", io);
		}
		return conf;
	}


	private static Drive getDriveService() throws IOException {
		Credential credential = null;
		try {
			credential = authorize();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}


	private static GoogleCredential authorize() throws IOException,
			GeneralSecurityException {
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();
		GoogleCredential credential = new GoogleCredential.Builder()
				.setTransport(httpTransport).setJsonFactory(jsonFactory)
				.setServiceAccountId(acountId)
				.setServiceAccountScopes(SCOPES)
				.setServiceAccountPrivateKeyFromP12File(
						new java.io.File(p12File)).build();
		return credential;
	}


	public boolean createFolder(String nameFolder, String emailUserOwner) {

		log.info("init createFolder");
		boolean resultado = true;
		Permission newPermission = createPermission();

		File body = new File();
		body.setTitle(nameFolder);
		body.setMimeType("application/vnd.google-apps.folder");
		body.setEditable(true);
		body.setShared(true);
		List<Permission> listPermisions = new ArrayList<Permission>();
		listPermisions.add(newPermission);
		body.setPermissions(listPermisions);
		body.setUserPermission(newPermission);
		body.setWritersCanShare(true);
		User user = new User();
		user.setEmailAddress(emailUserOwner);
		user.setIsAuthenticatedUser(true);
		List<User> list = new ArrayList<User>();
		list.add(user);
		body.setOwners(list);
		File file;
		String fileId = null;
		try {
			file = service.files().insert(body).execute();
			fileId = file.getId();
			log.info("File ID: " + file.getId());
			insertPermission(newPermission, fileId);
		} catch (IOException e) {
			log.error("Error create folder", e);
			resultado = false;
		}

		log.info("end createFolder");
		return resultado;
	}
	
	/**
	 * Method to create a google spreadsheet from a folder
	 * 
	 * @param idParentFolder
	 *            String to identify the parent folder
	 * @param emailUserOwner
	 *            String to identify email user owner
	 * @param extensionFile
	 *            String with extension file
	 * @param fileOrigin
	 *            Path's folder
	 * @param mimeType
	 *            String with mime type file origin
	 * 
	 * @return True if create is ok, False otherwise
	 */
	public boolean createSpreadsheetFromFolder(String folderOrigin, String idParentFolder,
			String emailUserOwner, String extensionFile, String mimeType) {
		
		java.io.File folder = new java.io.File(folderOrigin);
		String[] extensions = new String[] { "csv", "txt" };
		Collection<java.io.File> list = FileUtils.listFiles(folder,extensions, true);
		int cont=1;
		for (java.io.File file : list) {
			log.info("Upload file "+file.getName());
			createSpreadsheetFromFile(idParentFolder, emailUserOwner, extensionFile, file.getName().substring(0, file.getName().length()-4), file, mimeType);
			if(cont++%30==0){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error("Error en el sleep", e);
				}
			}
		}
		return true;
	}
	
	/**
	 * Method to create a google spreadsheet from a file
	 * 
	 * @param idParentFolder
	 *            String to identify the parent folder
	 * @param emailUserOwner
	 *            String to identify email user owner
	 * @param extensionFile
	 *            String with extension file
	 * @param nameFile
	 *            String with name google spreadsheet
	 * @param fileOrigin
	 *            File with content file origin
	 * @param mimeType
	 *            String with mime type file origin
	 * 
	 * @return True if create is ok, False otherwise
	 */
	public boolean createSpreadsheetFromFile(String idParentFolder,
			String emailUserOwner, String extensionFile, String nameFile,
			java.io.File fileOrigin, String mimeType) {

		boolean result = true;
		Permission newPermission = createPermission();
		File body = new File();
		body.setTitle(nameFile);
		body.setMimeType("application/vnd.google-apps.spreadsheet");
		body.setEditable(true);
		body.setShared(true);
		body.setPermissions(Arrays.asList(newPermission));
		body.setUserPermission(newPermission);
		body.setWritersCanShare(true);
		body.setFileExtension(extensionFile);
		User user = new User();
		user.setEmailAddress(emailUserOwner);
		user.setIsAuthenticatedUser(true);
		List<User> list = new ArrayList<User>();
		list.add(user);
		body.setOwners(list);
		body.setParents(Arrays.asList(new ParentReference()
				.setId(idParentFolder)));
				
		FileContent mediaContent = new FileContent(mimeType, fileOrigin);
		String fileId = "";
		try {
			File file = service.files().insert(body, mediaContent).execute();
			fileId = file.getId();
			log.info("File ID: " + file.getId());
			insertPermission(newPermission, fileId);

		} catch (IOException e) {
			log.error("Error create spreadsheet from file ", e);
			result = false;
		}
		return result;
	}


	public void listOwnerFiles() {
		FileList result;
		List<File> files = null;
		try {
			result = service.files().list().setMaxResults(10).execute();
			files = result.getItems();
		} catch (IOException e) {
			log.error("Error list files", e);
		}
		if (files == null || files.size() == 0) {
			log.error("No files found");
		} else {
			log.info("Files:\n");
			for (File file : files) {
				log.info("Title " + file.getTitle() + " id " + file.getId());
				PermissionList result2 = null;
				try {
					result2 = service.permissions().list(file.getId())
							.execute();
				} catch (IOException e) {
					log.error("Error list permissions files", e);
				}
				List<Permission> permissions = result2.getItems();
				for (Permission permission : permissions) {
					log.info("\tid " + permission.getId() + " domain "
							+ permission.getDomain() + " value "
							+ permission.getValue() + " type "
							+ permission.getType() + "\n");
				}
			}
		}
	}

	public void listOwnerFilesAfterDate(String stringDateLastChange) {
		SimpleDateFormat formatFullDate = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date dateLastChange = null;
		try {
			dateLastChange = formatFullDate.parse(stringDateLastChange);
		} catch (ParseException e1) {
			log.error("Error parse date in list", e1);
		}
		FileList result;
		List<File> files = null;
		try {
			result = service.files().list().setMaxResults(10).execute();
			files = result.getItems();
		} catch (IOException e) {
			log.error("Error list files", e);
		}
		if (files == null || files.size() == 0) {
			log.error("No files found");
		} else {
			log.info("Files:\n");
			for (File file : files) {
				DateTime dateTime = file.getModifiedDate();
				Date dateModifyFile = new Date(dateTime.getValue());
				if (dateModifyFile.after(dateLastChange))
					log.info("Title " + file.getTitle() + " id " + file.getId()
							+ " DateTime "
							+ formatFullDate.format(dateModifyFile));
			}
		}
	}
	
	public String getDownloadUrl(String fileId){
		try {
			return service.files().get(fileId).execute().getDownloadUrl() ;
		} catch (IOException e) {
			log.error("Error get download url", e);
		}
		return null;
	}
	

	private Permission createPermission() {

		Permission newPermission = new Permission();
		newPermission.setDomain(domainPermission);
		newPermission.setValue(domainPermission);
		newPermission.setType("domain");
		newPermission.setRole("writer");
		return newPermission;

	}

	private void insertPermission(Permission newPermission, String fileId) {
		try {
			Permission permission = service.permissions()
					.insert(fileId, newPermission).execute();
			log.info("permission ID: " + permission.getId());
		} catch (IOException e) {
			log.error("Error insert permission ", e);
		}
	}


	public static void main(String[] args) {
		if ((log == null) || (log.getLevel() == null))
			PropertyConfigurator.configure("log4j.properties");

		int modo = new Integer(args[0]);
		GoogleDriveAPI api = new GoogleDriveAPI();
		api.init();
		String pathFile = "";
		String pathFolder = "";
		String idParentFolder = "";
		String emailUserOwner = "";
		String extensionFile = "";
		String nameFile = "";
		String mimeType  = "";
		String date  = "";
		switch (modo) {
		case 1:
			pathFile = args[1];
			idParentFolder = args[2];
			emailUserOwner = args[3];
			extensionFile = args[4];
			nameFile = args[5];
			mimeType  = args[6];
			java.io.File file = new java.io.File(pathFile);
			api.createSpreadsheetFromFile(idParentFolder,
					 emailUserOwner, extensionFile, nameFile, file,
					 mimeType);
			break;
		case 2:
			pathFolder = args[1];
			idParentFolder = args[2];
			emailUserOwner = args[3];
			extensionFile = args[4];
			mimeType  = args[5];
			api.createSpreadsheetFromFolder(pathFolder, idParentFolder, emailUserOwner, extensionFile, mimeType);
			break;
		case 3:
			date  = args[1];
			api.listOwnerFilesAfterDate(date);
			break;
		default:
			break;
		}

	}
}
