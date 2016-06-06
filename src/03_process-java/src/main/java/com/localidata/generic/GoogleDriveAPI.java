package com.localidata.generic;

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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.User;
import com.localidata.util.Utils;

/**
 * 
 * @author Localidata
 *
 */
public class GoogleDriveAPI {

	private final static Logger log = Logger.getLogger(GoogleDriveAPI.class);
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static HttpTransport HTTP_TRANSPORT;
	private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY, DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA, DriveScopes.DRIVE_APPS_READONLY, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_METADATA, DriveScopes.DRIVE_READONLY,
			"https://www.googleapis.com/auth/drive.install");
	private Drive drive = null;

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
			boolean resultConf = Prop.loadConf();
			if (resultConf)
				this.drive = getDriveService();
			else
				log.error("Error in load configuration");
		} catch (Exception e) {
			log.error("Error init method", e);
		}
	}

	private static Drive getDriveService() throws IOException {
		Credential credential = null;
		try {
			credential = authorize();

		} catch (Exception e) {
			log.error("Error iniciando servicio google drive",e);
		}
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(Prop.APPLICATION_NAME).build();
	}

	private static GoogleCredential authorize() throws IOException, GeneralSecurityException {
		HttpTransport httpTransport = new NetHttpTransport();
		httpTransport = httpTransport.createRequestFactory().getTransport();
		HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {

			@Override
			public void initialize(HttpRequest httpRequest) throws IOException {

				httpRequest.setConnectTimeout(300 * 60000);
				httpRequest.setReadTimeout(300 * 60000);

			}
		};
		JacksonFactory jsonFactory = new JacksonFactory();
		GoogleCredential credential = new GoogleCredential.Builder().setRequestInitializer(httpRequestInitializer).setTransport(httpTransport).setJsonFactory(jsonFactory)
		.setServiceAccountId(Prop.acountId).setServiceAccountScopes(SCOPES)
		.setServiceAccountPrivateKeyFromP12File(new java.io.File(Prop.p12File)).build();
		return credential;
	}

	public boolean createFolder(String nameFolder, String emailUserOwner) {

		log.debug("init createFolder");
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
			file = drive.files().insert(body).execute();
			fileId = file.getId();
			log.info("File ID: " + file.getId());
			insertPermission(newPermission, fileId);
		} catch (Exception e) {
			log.error("Error create folder", e);
			resultado = false;
		}

		log.debug("end createFolder");
		return resultado;
	}

	public boolean createSpreadsheetFromFolder(String folderOrigin, String idParentFolder, String emailUserOwner, String extensionFile, String mimeType) {

		java.io.File folder = new java.io.File(folderOrigin);
		String[] extensions = new String[] { "csv", "txt" };
		Collection<java.io.File> list = FileUtils.listFiles(folder, extensions, true);
		int cont = 1;
		for (java.io.File file : list) {
			log.info("Upload file " + file.getName());
			createSpreadsheetFromFile(idParentFolder, emailUserOwner, extensionFile, file.getName().substring(0, file.getName().length() - 4), file, mimeType);
			if (cont++ % 30 == 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error("Error en el sleep", e);
				}
			}
		}
		return true;
	}

	public boolean createSpreadsheetFromFile(String idParentFolder, String emailUserOwner, String extensionFile, String nameFile, java.io.File fileOrigin, String mimeType) {

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
		body.setParents(Arrays.asList(new ParentReference().setId(idParentFolder)));
		FileContent mediaContent = new FileContent(mimeType, fileOrigin);
		String fileId = "";
		try {
			File file = drive.files().insert(body, mediaContent).execute();
			fileId = file.getId();
			insertPermission(newPermission, fileId);

		} catch (Exception e) {
			log.error("Error create spreadsheet from file ", e);
			result = false;
		}
		log.info("create Spreadsheet in google Drive from " + nameFile);
		return result;
	}

	public boolean updateFile(String name, java.io.File fileContent, String newMimeType) {

		File file = searchFile(name);
		FileContent mediaContent = new FileContent(newMimeType, fileContent);
		File updatedFile = null;
		try {
			updatedFile = drive.files().update(file.getId(), file, mediaContent).execute();
		} catch (Exception e) {
			log.error("Error actualizando fichero en google drive",e);
		}
		return (updatedFile != null);
	}

	public List<ChildReference> listFolderFiles(String folderId) {
		List<ChildReference> result = new ArrayList<ChildReference>();
		try {
			com.google.api.services.drive.Drive.Children.List request = drive.children().list(folderId);

			do {
				try {
					ChildList children = request.execute();

					result.addAll(children.getItems());
					request.setPageToken(children.getNextPageToken());
				} catch (Exception e) {
					System.out.println("An error occurred: " + e);
					request.setPageToken(null);
				}
			} while (request.getPageToken() != null && request.getPageToken().length() > 0);

		} catch (Exception e) {
			log.error("Error buscando ficheros en una carpeta en google drive",e);
		}

		return result;

	}

	public List<File> listOwnerFiles() {
		log.debug("init listOwnerFiles()");
		List<File> result = new ArrayList<File>();
		try {
			com.google.api.services.drive.Drive.Files.List request = drive.files().list().setQ("trashed = false");

			do {
				try {
					FileList files = request.execute();

					result.addAll(files.getItems());
					request.setPageToken(files.getNextPageToken());
				} catch (Exception e) {
					System.out.println("An error occurred: " + e);
					request.setPageToken(null);
				}
			} while (request.getPageToken() != null && request.getPageToken().length() > 0);

		} catch (Exception e) {
			log.error("Error list files", e);
		}
		log.debug("end listOwnerFiles()");
		return result;
	}

	public List<File> listOwnerFilesAfterDate(String stringDateLastChange) {
		SimpleDateFormat formatFullDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateLastChange = null;
		try {
			dateLastChange = formatFullDate.parse(stringDateLastChange);
		} catch (ParseException e1) {
			log.error("Error parse date in list", e1);
		}
		FileList result;
		List<File> files = null;
		try {
			result = drive.files().list().setMaxResults(1000).execute();

			files = result.getItems();
		} catch (Exception e) {
			log.error("Error list files", e);
		}
		if (files == null || files.size() == 0) {
			log.error("No files found");
		} else {
			for (File file : files) {
				if (file.getShared()) {
					DateTime dateTime = file.getModifiedDate();
					Date dateModifyFile = new Date(dateTime.getValue());
					if (dateModifyFile.after(dateLastChange))
						log.info("Title " + file.getTitle() + " id " + file.getId() + " DateTime " + formatFullDate.format(dateModifyFile));
				}
			}
		}
		return files;
	}

	public File searchFile(String name) {
		log.debug("init listOwnerFiles()");
		FileList result;
		List<File> files = new ArrayList<>();
		File file = null;
		com.google.api.services.drive.Drive.Files.List request = null;

		try {
			request = drive.files().list().setMaxResults(1000);
			result = request.execute();
			files.addAll(result.getItems());

		} catch (Exception e) {
			log.error("Error list files", e);
		}

		if (files == null || files.size() == 0) {
			log.error("No files found");
		} else {
			log.debug("Files:\n");
			for (File fileAux : files) {
				if (fileAux.getShared() == true && fileAux.getTitle().contains(name)) {
					file = fileAux;
					break;
				}
			}
		}

		log.debug("end listOwnerFiles()");
		return file;
	}

	public void downloadFilesAfterDate(String path, String stringDateLastChange) {

		List<File> files = listOwnerFilesAfterDate(stringDateLastChange);
		for (File file : files) {
			try {
				String downloadUrl = file.getExportLinks().get("text/csv");

				HttpResponse resp = drive.getRequestFactory().buildGetRequest(new GenericUrl(downloadUrl)).execute();
				InputStream input = resp.getContent();
				java.io.File f = new java.io.File(path + java.io.File.separator + file.getTitle() + ".csv");
				FileUtils.copyInputStreamToFile(input, f);

			} catch (Exception e) {
				log.error("Error decargando ficheros despues de una fecha en google drive",e);
			}
		}

	}

	public void downloadFolderFiles(String path, String folderId) throws IOException {

		List<ChildReference> listErrors = new ArrayList<>();
		List<ChildReference> list = listFolderFiles(folderId);
		FileUtils.deleteDirectory(new java.io.File(path));
		for (ChildReference child : list) {
			try {
				downloadChildFile(path, child);
			} catch (Exception e) {
				log.error("Error en downloadFolderFiles with " + child, e);
				listErrors.add(child);
			}
		}
		int index = 0;
		int numErrors = 0;
		while (listErrors.size() > 0) {
			ChildReference child = listErrors.get(index);
			try {
				downloadChildFile(path, child);
				listErrors.remove(child);
				numErrors = 0;
			} catch (Exception e) {
				log.error("Error en downloadFolderFiles with " + child, e);
				listErrors.add(child);
				if (numErrors != 0) {
					numErrors++;
				} else if (numErrors > 10) {
					listErrors.remove(child);
					numErrors = 0;
				}
			}
		}
	}
	
	private void downloadChildFile(String path, ChildReference child) throws IOException {
		File file = drive.files().get(child.getId()).execute();
		download(path, file);
	}

	private void download(String path, File file) throws IOException {
		if (file.getShared()) {
			String downloadUrl = file.getExportLinks().get("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			HttpResponse resp = drive.getRequestFactory().buildGetRequest(new GenericUrl(downloadUrl)).execute();
			InputStream input = resp.getContent();

			java.io.File f = new java.io.File(path + java.io.File.separator + file.getTitle() + "." + Prop.formatConfig);
			FileUtils.copyInputStreamToFile(input, f);
		}
	}

	public void downloadAllFiles(String path) throws IOException {
		log.debug("init downloadAllFiles()");
		List<File> files = listOwnerFiles();
		FileUtils.deleteDirectory(new java.io.File(path));
		for (File file : files) {
			download(path, file);

		}
		log.debug("end downloadAllFiles()");
	}

	public java.io.File downloadFile(String path, String name, String format) {
		File file = searchFile(name);
		return downloadFile(path,file,format);
	}

	public java.io.File downloadFile(String path, File file, String format) {
		if (file == null)
			return null;
		String mimetype = "";

		if (com.localidata.generic.Constants.CSV.equals(format))
			mimetype = "text/csv";
		else if (com.localidata.generic.Constants.XLSX.equals(format)) {
			mimetype = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		}

		try {
			String downloadUrl = file.getExportLinks().get(mimetype);

			HttpResponse resp = drive.getRequestFactory().buildGetRequest(new GenericUrl(downloadUrl)).execute();
			InputStream input = resp.getContent();

			java.io.File f = null;
			if (Utils.v(path)) {
				f = new java.io.File(path + java.io.File.separator + file.getTitle() + "." + format);
			} else {
				f = new java.io.File(file.getTitle() + "." + format);
			}
			FileUtils.copyInputStreamToFile(input, f);
			return f;
		} catch (Exception e) {
			log.error("Error decargando fichero en google drive",e);
		}
		return null;
	}

	public String getDownloadUrl(String fileId) {
		try {
			return drive.files().get(fileId).execute().getDownloadUrl();
		} catch (Exception e) {
			log.error("Error get download url", e);
		}
		return null;
	}

	private Permission createPermission() {

		Permission newPermission = new Permission();
		newPermission.setDomain(Prop.domainPermission);
		newPermission.setValue(Prop.domainPermission);
		newPermission.setType("domain");
		newPermission.setRole("writer");
		return newPermission;

	}

	private void insertPermission(Permission newPermission, String fileId) {
		try {
			drive.permissions().insert(fileId, newPermission).execute();
		} catch (Exception e) {
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
		String mimeType = "";
		String date = "";
		switch (modo) {
		case 1:
			pathFile = args[1];
			idParentFolder = args[2];
			emailUserOwner = args[3];
			extensionFile = args[4];
			nameFile = args[5];
			mimeType = args[6];
			java.io.File file = new java.io.File(pathFile);
			api.createSpreadsheetFromFile(idParentFolder, emailUserOwner, extensionFile, nameFile, file, mimeType);
			break;
		case 2:
			pathFolder = args[1];
			idParentFolder = args[2];
			emailUserOwner = args[3];
			extensionFile = args[4];
			mimeType = args[5];
			api.createSpreadsheetFromFolder(pathFolder, idParentFolder, emailUserOwner, extensionFile, mimeType);
			break;
		case 3:
			date = args[1];
			api.listOwnerFilesAfterDate(date);
			break;
		case 4:
			try {
				api.downloadAllFiles(args[1]);
			} catch (Exception e) {
				log.error("Error descargando todos los ficheros",e);
			}
			break;
		case 5:
			api.downloadFilesAfterDate(args[1], args[2]);
			break;
		default:
			break;
		}
	}
}
