package com.localidata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class Utils {


	private final static Logger log = Logger.getLogger(Utils.class);

	private static final int defaultReadTimeOut = 60000;

	private static final int defaultTimeOut = 10000;

	private static String comarcasAragon = null;

	private static String municipiosAragon = null;

	private static String provinciasAragon = null;

	private static String comunidadAragon = null;


	public static String weakClean(String chain) {
		return chain.replace(new String(Character.toChars(0)), "");
	}
	

	public static String urlify(String chain) {

		String chainToURI = chain.trim().toLowerCase();
		chainToURI = chainToURI.replace(" - ", "-");
		chainToURI = chainToURI.replace(" ", "-");
		chainToURI = chainToURI.replace("\\", "");
		chainToURI = chainToURI.replace("/", "");
		chainToURI = chainToURI.replaceAll("\\s+", "_");
		chainToURI = chainToURI.replaceAll("&", "");
		chainToURI = chainToURI.replaceAll("'", "");
		chainToURI = chainToURI.replaceAll("___", "_");
		chainToURI = chainToURI.replaceAll("__", "_");
		chainToURI = chainToURI.replace("(", "");
		chainToURI = chainToURI.replace(")", "");
		chainToURI = chainToURI.replace(";", "");
		chainToURI = chainToURI.replace(",", "");
		chainToURI = chainToURI.replace("\\", "");
		chainToURI = chainToURI.replace("_", "");
		chainToURI = chainToURI.replace(".", "");
		chainToURI = chainToURI.replace(":", "");
		chainToURI = chainToURI.replace("|", "");
		chainToURI = chainToURI.replace("=", "");
		chainToURI = chainToURI.replace(">", "");
		chainToURI = chainToURI.replace("<", "");
		chainToURI = chainToURI.replace("%", "");
		chainToURI = chainToURI.replace("+", "");
		chainToURI = chainToURI.replace("\"", "");

		chainToURI = chainToURI.replace("!", "");
		chainToURI = chainToURI.replace("¡", "");
		chainToURI = chainToURI.replace("?", "");
		chainToURI = chainToURI.replace("¿", "");
		chainToURI = chainToURI.replace("á", "a");
		chainToURI = chainToURI.replace("á", "a");
		chainToURI = chainToURI.replace("é", "e");
		chainToURI = chainToURI.replace("í", "i");
		chainToURI = chainToURI.replace("ó", "o");
		chainToURI = chainToURI.replace("ú", "u");
		chainToURI = chainToURI.replace("ñ", "n");

		chainToURI = chainToURI.replace("ü", "u");

		chainToURI = chainToURI.replace("º", "");
		chainToURI = chainToURI.replace("ª", "");
		chainToURI = chainToURI.replace(new String(Character.toChars(0)), "");
		chainToURI = chainToURI.replace(new String(Character.toChars(13)), "");

		try {
			chainToURI = URLEncoder.encode(chainToURI, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			chainToURI = null;
			e.printStackTrace();
		}

		return chainToURI;

	}
	
	public static String dimensionWeakClean(String chain) {
		
		String chainToURI = chain.trim().toLowerCase();
		chainToURI = chainToURI.replaceAll("[^\\p{ASCII}]", "");
		return chainToURI;
	}
	
	public static String dimensionStrongClean(String chain) {
		
		String chainToURI = chain.trim().toLowerCase();
		chainToURI = chainToURI.replace("\\", "");
		chainToURI = chainToURI.replace("/", "");
		chainToURI = chainToURI.replace(":", "");
		chainToURI = chainToURI.replace("*", "");
		chainToURI = chainToURI.replace("\"", "");
		chainToURI = chainToURI.replace("?", "");
		chainToURI = chainToURI.replace("¿", "");
		chainToURI = chainToURI.replace(">", "");
		chainToURI = chainToURI.replace("<", "");
		chainToURI = chainToURI.replace("|", "");
		chainToURI = chainToURI.replace("'", "");
		chainToURI = chainToURI.replace("º", "");
		
		chainToURI = chainToURI.replace("á", "a");
		chainToURI = chainToURI.replace("á", "a");
		chainToURI = chainToURI.replace("é", "e");
		chainToURI = chainToURI.replace("í", "i");
		chainToURI = chainToURI.replace("ó", "o");
		chainToURI = chainToURI.replace("ú", "u");
		chainToURI = chainToURI.replace("ñ", "n");

		chainToURI = chainToURI.replace("ü", "u");
		return chainToURI;
	}


	public static String genUUIDHash(String id) {

		String hash;
		UUID uuid = null;

		if (!Utils.validValue(id)) {
			log.error("Invalid ID generating a UUID");
			return null;
		}
		try {
			hash = DatatypeConverter.printHexBinary(MessageDigest.getInstance(
					"SHA-1").digest(id.getBytes("UTF-8")));
			uuid = UUID.nameUUIDFromBytes(hash.getBytes());
		} catch (NoSuchAlgorithmException e) {
			log.error("Error generating a UUID hash with id:" + id, e);
		} catch (UnsupportedEncodingException e) {
			log.error("Error generating a UUID hash with id:" + id, e);
		}

		return uuid.toString();
	}
	

	public static boolean validValue(String value) {
		if ((value != null) && (!value.equals("")))
			return true;
		return false;
	}
	

	public static String getUrlRefArea(String header, String cleanCell, String fileName) {

		String valueCell = cleanCell;
		valueCell = valueCell.replaceAll(" / ", "/");
		valueCell = valueCell.replaceAll(" ", "_");
		String cadidateUrl = "";
		String ttl = "";
		if (header.contains("comarca")) {
			cadidateUrl = "http://opendata.aragon.es/recurso/territorio/Comarca/";
			ttl = getComarcasAragon();
		} else if (header.contains("municipio")) {
			cadidateUrl = "http://opendata.aragon.es/recurso/territorio/Municipio/";
			ttl = getMunicipiosAragon();

		} else if (header.contains("provincia")) {
			cadidateUrl = "http://opendata.aragon.es/recurso/territorio/Provincia/";
			ttl = getProvinciasAragon();
		} else if (header.contains("comunidad") || header.contains("aragon")) {
			cadidateUrl = "http://opendata.aragon.es/recurso/territorio/ComunidadAutonoma/";
			ttl = getComunidadAragon();
		}

		if (valueCell.contains("Torla-Ordesa")) {
			TransformToRDF.insertError(fileName+". ERROR. Column "+header+". "+valueCell+" instead of Torla");
			cadidateUrl += "Torla";
		} else if (valueCell.contains("Beranuy")) {
			TransformToRDF.insertError(fileName+". ERROR. Column "+header+". "+valueCell+" instead of Veracruz");
			cadidateUrl += "Veracruz";
		} else if (valueCell.contains("Biel-Fuencalderas")) {
			TransformToRDF.insertError(fileName+". ERROR. Column "+header+". "+valueCell+" instead of Biel");
			cadidateUrl += "Biel";
		} else if (valueCell.contains("Sin_clasificar")) {
			TransformToRDF.insertError(fileName+". ERROR. Column "+header+". VALUE "+valueCell);
		} else {
			cadidateUrl += valueCell;
		}

		if (!ttl.contains(cadidateUrl)) {
			TransformToRDF.insertError(fileName+". ERROR. Column "+header+". VALUE "+valueCell+" NOT FOUND");
			log.error("URL no encontrada para: " + header + " | " + cleanCell);
			log.error(cadidateUrl);
			return "\""+valueCell+"\"";
		}
		
		return "<"+cadidateUrl+">";
	}
	

	private static String getComarcasAragon() {
		if (comarcasAragon == null) {
			String url = "http://opendata.aragon.es/recurso/territorio/Comarca";
			String response = "";
			comarcasAragon = Utils
					.processURLGet(
							url
									+ ".ttl?_sort=label&_page=0&_pageSize=100&api_key=04d08351bc1bf50c3c65beeb8f8f5b13",
							"", null);
			boolean finish = false;
			int page = 1;
			while (!finish) {
				response = Utils
						.processURLGet(
								url
										+ ".ttl?_sort=label&_page="
										+ page
										+ "&_pageSize=100&api_key=04d08351bc1bf50c3c65beeb8f8f5b13",
								"", null);
				page++;
				if (!response.contains("xhv:next")) {
					finish = true;
				}
				comarcasAragon += response;
			}
		}
		return comarcasAragon;
	}
	

	private static String getMunicipiosAragon() {
		if (municipiosAragon == null) {
			String url = "http://opendata.aragon.es/recurso/territorio/Municipio";
			String response = "";
			municipiosAragon = Utils
					.processURLGet(
							url
									+ ".ttl?_sort=label&_page=0&_pageSize=100&api_key=04d08351bc1bf50c3c65beeb8f8f5b13",
							"", null);
			boolean finish = false;
			int page = 1;
			while (!finish) {
				response = Utils
						.processURLGet(
								url
										+ ".ttl?_sort=label&_page="
										+ page
										+ "&_pageSize=100&api_key=04d08351bc1bf50c3c65beeb8f8f5b13",
								"", null);
				page++;
				if (!response.contains("xhv:next")) {
					finish = true;
				}
				municipiosAragon += response;
			}
		}
		return municipiosAragon;
	}
	

	private static String getProvinciasAragon() {
		if (provinciasAragon == null) {
			String url = "http://opendata.aragon.es/recurso/territorio/Provincia";
			String response = "";
			provinciasAragon = Utils
					.processURLGet(
							url
									+ ".ttl?_sort=label&_page=0&_pageSize=100&api_key=04d08351bc1bf50c3c65beeb8f8f5b13",
							"", null);
			boolean finish = false;
			int page = 1;
			while (!finish) {
				response = Utils
						.processURLGet(
								url
										+ ".ttl?_sort=label&_page="
										+ page
										+ "&_pageSize=100&api_key=04d08351bc1bf50c3c65beeb8f8f5b13",
								"", null);
				page++;
				if (!response.contains("xhv:next")) {
					finish = true;
				}
				provinciasAragon += response;
			}
		}
		return provinciasAragon;
	}
	

	private static String getComunidadAragon() {
		if (comunidadAragon == null) {
			String url = "http://opendata.aragon.es/recurso/territorio/ComunidadAutonoma";
			String response = "";
			comunidadAragon = Utils
					.processURLGet(
							url
									+ ".ttl?_sort=label&_page=0&_pageSize=100&api_key=04d08351bc1bf50c3c65beeb8f8f5b13",
							"", null);
			boolean finish = false;
			int page = 1;
			while (!finish) {
				response = Utils
						.processURLGet(
								url
										+ ".ttl?_sort=label&_page="
										+ page
										+ "&_pageSize=100&api_key=04d08351bc1bf50c3c65beeb8f8f5b13",
								"", null);
				page++;
				if (!response.contains("xhv:next")) {
					finish = true;
				}
				comunidadAragon += response;
			}
		}
		return comunidadAragon;
	}
	

	public static String processURLGet(String url, String urlParameters,
			Map<String, String> headers) {

		StringBuffer sb = new StringBuffer();
		HttpURLConnection httpConnection = null;
		try {
			URL targetUrl = null;
			if ((urlParameters == null) || (urlParameters.equals(""))) {
				targetUrl = new URL(url);
			} else {
				targetUrl = new URL(url + "?" + urlParameters);
			}

			httpConnection = (HttpURLConnection) targetUrl.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("GET");

			if (headers != null) {
				Iterator<Entry<String, String>> it = headers.entrySet()
						.iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> pairs = (Map.Entry<String, String>) it
							.next();
					httpConnection.setRequestProperty(pairs.getKey(),
							pairs.getValue());
				}
			}

			httpConnection.setConnectTimeout(defaultTimeOut);
			httpConnection.setReadTimeout(defaultReadTimeOut);

			BufferedReader responseBuffer = new BufferedReader(
					new InputStreamReader((httpConnection.getInputStream()), "UTF-8"));

			String output;

			while ((output = responseBuffer.readLine()) != null) {
				sb.append(output);
			}

			if (httpConnection.getResponseCode() != 200) {
				log.error("The URI does not return a 200 code");
				log.error(output);
				return "";
			}

		} catch (MalformedURLException e) {
			log.error("Error with the URI: " + url + "?" + urlParameters, e);
			sb.setLength(0);

		} catch (IOException e) {
			log.error("IOError: " + url + "?" + urlParameters, e);
			sb.setLength(0);

		} finally {
			httpConnection.disconnect();
		}

		return sb.toString();

	}
	

	public static void stringToFile(String string, File file) throws Exception {

		try {
			FileUtils.writeStringToFile(file, string, "UTF-8");
		} catch (IOException e) {
			log.error("Error writing file", e);
		}

	}
	
	public static void stringToFileAppend(String content, File file){
		
		try {
			FileUtils.writeStringToFile(file, content, "UTF-8", true);
		} catch (FileNotFoundException e) {
			log.error("Error writing file", e);
		} catch (UnsupportedEncodingException e) {
			log.error("Error writing file", e);
		} catch (IOException e) {
			log.error("Error writing file", e);
		}
		
	}

}
