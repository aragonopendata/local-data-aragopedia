package com.localidata.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.localidata.generic.Prop;
import com.localidata.process.TransformToRDF;

/**
 * 
 * @author Localidata
 *
 */
public class Utils {

	private final static Logger log = Logger.getLogger(Utils.class);
	private static final int defaultReadTimeOut = 10000;
	private static final int defaultTimeOut = 5000;
	private static String comarcasAragon = null;
	private static String municipiosAragon = null;
	private static String provinciasAragon = null;
	private static String comunidadAragon = null;

	public static String prefLabelClean(String chain) {
		if (Utils.v(chain) && chain.length() >= 2)
			if (chain.charAt(chain.length() - 2) == ',' && chain.charAt(chain.length() - 1) == '¿') {
				chain = chain.substring(0, chain.length() - 2);
			}
		return chain;
	}

	public static String nameDataBeanClean(String chain) {
		String chainToURI = chain.replace("á", "a");
		chainToURI = chainToURI.replace("á", "a");
		chainToURI = chainToURI.replace("é", "e");
		chainToURI = chainToURI.replace("í", "i");
		chainToURI = chainToURI.replace("ó", "o");
		chainToURI = chainToURI.replace("ú", "u");
		chainToURI = chainToURI.replace("ñ", "n");

		chainToURI = chainToURI.replace("ü", "u");
		return chainToURI;
	}

	public static String weakClean(String chain) {
		chain = chain.replace(new String(Character.toChars(0)), "");
		chain = chain.replace("", "");
		chain = chain.replace("\"", "");
		return chain;
	}

	public static String cleanSpaceAndCaracters(String chain) {
		String result = "";
		result = cleanSpace(chain);
		return cleanCaracters(result);
	}

	public static String cleanSpace(String chain) {
		String result = "";
		String[] split = chain.split(" ");
		for (int h = 0; h < split.length; h++) {
			String s = split[h];
			if (!s.equals("")) {
				result = result + s;
				if ((h + 1) < split.length) {
					result = result + " ";
				}
			}
		}

		return result;
	}

	public static String cleanCaracters(String chain) {

		String result = "";
		char[] charaters = { '\t', '~', 'á', 'Á', 'é', 'É', 'í', 'Í', 'ó', 'Ó', 'ú', 'Ú', '(', ')', '%', '>', '<', '-', '.', '\n', '\r' };
		ArrayList listChars = new ArrayList(Arrays.asList(charaters));
		for (int h = 0; h < chain.length(); h++) {
			char c = chain.charAt(h);
			if (listChars.contains(c) || Character.isDigit(c) || Character.isLetter(c) || c == ' ') {
				result = result + c;
			}
		}
		return result;
	}

	public static String urlify(String chain) {

		String chainToURI = chain.trim().toLowerCase();

		chainToURI = chainToURI.replace(" - ", "-");
		chainToURI = chainToURI.replace("\\", "");
		chainToURI = chainToURI.replace("/", "");
		chainToURI = chainToURI.replace("_", "");
		chainToURI = chainToURI.replace(" ", "-");
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
		chainToURI = chainToURI.replace(".", "");
		chainToURI = chainToURI.replace(":", "");
		chainToURI = chainToURI.replace("|", "");
		chainToURI = chainToURI.replace("=", "");
		chainToURI = chainToURI.replace(">", "");
		chainToURI = chainToURI.replace("<", "");
		chainToURI = chainToURI.replace("+", "");
		chainToURI = chainToURI.replace("\"", "");
		chainToURI = chainToURI.replace("%", "");
		chainToURI = chainToURI.replace("*", "");
		chainToURI = chainToURI.replace("", "");
		if (chainToURI.length() > 0) {
			while (chainToURI.charAt(0) == '-')
				chainToURI = chainToURI.substring(1, chainToURI.length());
		}

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
			log.error("Error en el método urlify", e);
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
		chainToURI = chainToURI.replace(",", "");

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

		if (!Utils.v(id)) {
			log.error("Invalid ID generating a UUID");
			return null;
		}
		try {
			hash = DatatypeConverter.printHexBinary(MessageDigest.getInstance("SHA-1").digest(id.getBytes("UTF-8")));
			uuid = UUID.nameUUIDFromBytes(hash.getBytes());
		} catch (NoSuchAlgorithmException e) {
			log.error("Error generating a UUID hash with id:" + id, e);
		} catch (UnsupportedEncodingException e) {
			log.error("Error generating a UUID hash with id:" + id, e);
		}

		return uuid.toString();
	}
	
	public static boolean v(Object c) {

		if (c == null)
			return false;

		if (c instanceof StringBuffer) {
			if (((StringBuffer) c).length() <= 0)
				return false;
			return true;
		}

		if (c instanceof List) {
			if (((List<?>) c).size() == 0)
				return false;
			return true;
		}

		if (c instanceof String) {
			if (c.equals(""))
				return false;
			return true;
		}

		if (Float.class.isInstance(c)) {
			if ((float) c == -1)
				return false;
			return true;
		}

		if (Integer.class.isInstance(c)) {
			if ((int) c == -1)
				return false;
			return true;
		}

		return true;
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
		} else if (header.contains("comunidad") || header.contains("aragon") || header.contains("ccaa")) {
			cadidateUrl = "http://opendata.aragon.es/recurso/territorio/ComunidadAutonoma/";
			ttl = getComunidadAragon();
		}
		if (valueCell.contains("Torla-Ordesa")) {
			TransformToRDF.insertError(fileName + ". ERROR. Column " + header + ". " + valueCell + " instead of Torla");
			cadidateUrl += "Torla";
		} else if (valueCell.contains("Beranuy")) {
			TransformToRDF.insertError(fileName + ". ERROR. Column " + header + ". " + valueCell + " instead of Veracruz");
			cadidateUrl += "Veracruz";
		} else if (valueCell.contains("Biel-Fuencalderas")) {
			TransformToRDF.insertError(fileName + ". ERROR. Column " + header + ". " + valueCell + " instead of Biel");
			cadidateUrl += "Biel";
		} else if (valueCell.contains("Sin_clasificar")) {
			TransformToRDF.insertError(fileName + ". ERROR. Column " + header + ". VALUE " + valueCell);
		} else {
			cadidateUrl += valueCell;
		}

		if (!ttl.contains(cadidateUrl)) {
			TransformToRDF.insertError(fileName + ". ERROR. Column " + header + ". VALUE " + valueCell + " NOT FOUND");
			log.error("URL no encontrada para: " + header + " | " + cleanCell);
			log.error(cadidateUrl);
			log.debug("Error en getUrlRefArea cadidateUrl " + cadidateUrl + " response " + ttl);
			return "\"" + valueCell + "\"";
		}

		return "<" + cadidateUrl + ">";
	}

	private static String getComarcasAragon() {
		if (comarcasAragon == null) {
			String url = "http://opendata.aragon.es/recurso/territorio/Comarca";
			String response = "";
			comarcasAragon = Utils.processURLGet(url + ".ttl?_sort=label&_page=0&_pageSize=100&api_key=" + Prop.apiKeyAragopedia, "", null);
			boolean finish = false;
			int page = 1;
			while (!finish) {
				response = Utils.processURLGet(url + ".ttl?_sort=label&_page=" + page + "&_pageSize=100&api_key=" + Prop.apiKeyAragopedia, "", null);
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
			municipiosAragon = Utils.processURLGet(url + ".ttl?_sort=label&_page=0&_pageSize=100&api_key=" + Prop.apiKeyAragopedia, "", null);
			boolean finish = false;
			int page = 1;
			while (!finish) {
				response = Utils.processURLGet(url + ".ttl?_sort=label&_page=" + page + "&_pageSize=100&api_key=" + Prop.apiKeyAragopedia, "", null);
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
			provinciasAragon = Utils.processURLGet(url + ".ttl?_sort=label&_page=0&_pageSize=100&api_key=" + Prop.apiKeyAragopedia, "", null);
			boolean finish = false;
			int page = 1;
			while (!finish) {
				response = Utils.processURLGet(url + ".ttl?_sort=label&_page=" + page + "&_pageSize=100&api_key=" + Prop.apiKeyAragopedia, "", null);
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
			comunidadAragon = Utils.processURLGet(url + ".ttl?_sort=label&_page=0&_pageSize=100&api_key=" + Prop.apiKeyAragopedia, "", null);
			boolean finish = false;
			int page = 1;
			while (!finish) {
				response = Utils.processURLGet(url + ".ttl?_sort=label&_page=" + page + "&_pageSize=100&api_key=" + Prop.apiKeyAragopedia, "", null);
				page++;
				if (!response.contains("xhv:next")) {
					finish = true;
				}
				comunidadAragon += response;
			}
		}
		return comunidadAragon;
	}

	public static void processURLGetApache() {

		CookieStore cookieStore = new BasicCookieStore();

		RequestConfig defaultRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).setExpectContinueEnabled(true).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST)).setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
				.build();

		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

		try {
			HttpGet httpget = new HttpGet("http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/03/030018TP&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico");
			httpget.addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
			httpget.addHeader(
					"Cookie",
					"sawU=granpublico; ORA_BIPS_LBINFO=153c4c924b8; ORA_BIPS_NQID=k8vgekohfuquhdg71on5hjvqbcorcupbmh4h3lu25iepaq5izOr07UFe9WiFvM3; __utma=263932892.849551431.1443517596.1457200753.1458759706.17; __utmc=263932892; __utmz=263932892.1456825145.15.6.utmcsr=alzir.dia.fi.upm.es|utmccn=(referral)|utmcmd=referral|utmcct=/kos/iaest/clase-vivienda-agregado");
			httpget.addHeader("content-type", "text/csv; charset=utf-8");

			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).setConnectTimeout(5000).setConnectionRequestTimeout(5000).setCookieSpec(CookieSpecs.DEFAULT).build();
			httpget.setConfig(requestConfig);

			HttpClientContext context = HttpClientContext.create();
			context.setCookieStore(cookieStore);

			System.out.println("executing request " + httpget.getURI());
			CloseableHttpResponse response = httpclient.execute(httpget, context);
			try {
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				System.out.println(EntityUtils.toString(response.getEntity()));
				System.out.println("----------------------------------------");


				context.getRequest();
				context.getHttpRoute();
				context.getTargetAuthState();
				context.getTargetAuthState();
				context.getCookieOrigin();
				context.getCookieSpec();
				context.getUserToken();

			} finally {
				response.close();
			}
			response = httpclient.execute(httpget, context);
			try {
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				System.out.println(EntityUtils.toString(response.getEntity()));
				System.out.println("----------------------------------------");


				context.getRequest();
				context.getHttpRoute();
				context.getTargetAuthState();
				context.getTargetAuthState();
				context.getCookieOrigin();
				context.getCookieSpec();
				context.getUserToken();

			} finally {
				response.close();
			}
			httpclient.close();
		} catch (ClientProtocolException e) {
			log.error("Error en el método processURLGetApache", e);
		} catch (IOException e) {
			log.error("Error en el método processURLGetApache", e);
		}
	}

	public static String ISO88591toUTF8(String content) {
		String utf8content = "";

		try {
			Charset iso88591charset = Charset.forName("ISO-8859-1");
			Charset utf8charset = Charset.forName("UTF-8");

			CharBuffer data = iso88591charset.decode(ByteBuffer.wrap(content.getBytes("ISO-8859-1")));

			ByteBuffer outputBuffer = utf8charset.encode(data);
			byte[] outputData = outputBuffer.array();

			String contenido8 = new String(outputData);

			utf8content = contenido8.trim();
		} catch (UnsupportedEncodingException e) {
			log.error("Encoding not suported", e);
		}
		return utf8content;

	}
	
	public static String UTF8BOMtoUTF8(String content) {
		byte[] bytes = content.getBytes();
		log.debug("UTF8BOMtoUTF8 bytes "+bytes[0]+" "+bytes[1]+" "+bytes[3]+" "+bytes[4]);
		byte[] bytesResult = new byte[bytes.length-Prop.bom];
		for(int h=Prop.bom;h<bytes.length;h++){
			bytesResult[h-Prop.bom]=bytes[h];
		}
		log.debug("UTF8BOMtoUTF8 bytesResult "+bytesResult[0]+" "+bytesResult[1]+" "+bytesResult[3]+" "+bytesResult[4]);
		String result = new String(bytes);
		return result;
	}

	public static String processURLGet(String url, String urlParameters, Map<String, String> headers, Cookies cookies, String encoding) throws SocketTimeoutException {

		String output = null;
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
				Iterator<Entry<String, String>> it = headers.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
					httpConnection.setRequestProperty(pairs.getKey(), pairs.getValue());
				}
			}
			cookies.setCookies(httpConnection);
			httpConnection.setConnectTimeout(defaultTimeOut);
			httpConnection.setReadTimeout(defaultReadTimeOut);

			InputStream is = (InputStream) httpConnection.getContent();

			output = IOUtils.toString(is, encoding);

			cookies.storeCookies(httpConnection);

			if (httpConnection.getResponseCode() != 200) {
				log.error("The URI does not return a 200 code");
				log.error(output);
				return "";
			}

		} catch (MalformedURLException e) {
			log.error("Error with the URI: " + url + "?" + urlParameters, e);

		} catch (IOException e) {
			log.error("IOError: " + url + "?" + urlParameters, e);

		} finally {
			httpConnection.disconnect();
		}

		return output;

	}

	public static String processURLGet(String url, String urlParameters, Map<String, String> headers) {

		log.debug("processURLGet url " + url + " urlParameters " + urlParameters + " headers " + headers);
		StringBuffer sb = new StringBuffer();
		HttpURLConnection httpConnection = null;
		try {
			URL targetUrl = null;
			if ((urlParameters == null) || (urlParameters.equals(""))) {
				targetUrl = new URL(url);
			} else {
				targetUrl = new URL(url + "?" + urlParameters);
			}
			log.debug("targetUrl " + targetUrl.toString());
			httpConnection = (HttpURLConnection) targetUrl.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("GET");

			if (headers != null) {
				Iterator<Entry<String, String>> it = headers.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
					httpConnection.setRequestProperty(pairs.getKey(), pairs.getValue());
				}
			}

			httpConnection.setConnectTimeout(defaultTimeOut);
			httpConnection.setReadTimeout(defaultReadTimeOut);

			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader((httpConnection.getInputStream()), "UTF-8"));

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
	
	public static String processURLGet(String URI) throws IOException {

		log.info("processURLGet: " + URI);

		StringBuilder content = new StringBuilder();

		try {
			URL url = new URL(URI);

			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setConnectTimeout(10000);

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				content.append(line + "\n");
			}
			bufferedReader.close();
		} catch (Exception e) {
			log.error("Error in processURLGet", e);
			return "";
		}
		return content.toString();

	}
	
	public static String processURLPost(String url, String urlParameters, Map<String, String> headers, String body) throws IOException {
		return processURLPost(url,urlParameters,headers,body,"UTF-8");
	}
	
	public static String processURLPost(String url, String urlParameters, Map<String, String> headers, String body, String encoding) throws IOException {

		HttpURLConnection httpConnection = null;
		try {

			StringBuffer sb = new StringBuffer();
			String u = "";
			if (!Utils.v(urlParameters)) {
				u = url;
			} else {
				u = url + "?" + urlParameters;
			}
			URL targetUrl = new URL(u);

			httpConnection = (HttpURLConnection) targetUrl.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("POST");
			httpConnection.setConnectTimeout(defaultTimeOut);
			httpConnection.setReadTimeout(defaultReadTimeOut);

			if (headers != null) {
				Iterator<Entry<String, String>> it = headers.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> pairs = it.next();
					httpConnection.setRequestProperty(pairs.getKey(), pairs.getValue());
				}
			}

			OutputStream outputStream = httpConnection.getOutputStream();
			outputStream.write(body.getBytes());
			outputStream.flush();

			BufferedReader responseBuffer;

			responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), encoding));

			String output;

			while ((output = responseBuffer.readLine()) != null) {
				sb.append(output);
			}

			if ((httpConnection.getResponseCode() > 299) && (httpConnection.getResponseCode() < 200)) {
				log.error("The URI does not return a 2XX code");
				log.error(httpConnection.getResponseCode());
				httpConnection.disconnect();
				return "";
			}
			httpConnection.disconnect();
			return sb.toString();

		} catch (IOException e) {
			log.error("Error procesing URL by Post");
			if (httpConnection != null)
				httpConnection.disconnect();
			throw e;
		}

	}

	public static void stringToFile(String string, File file) throws Exception {

		try {
			FileUtils.writeStringToFile(file, string, "UTF-8");
		} catch (IOException e) {
			log.error("Error writing file", e);
		}

	}
	
	public static void stringToFileAppend(String content, File file) {

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
	
	public static boolean isInteger(String cell) {
		boolean resultado = false;

		String pattern = "^-?[0-9]+$";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(cell);
		if (m.find()) {
			resultado = true;
		}

		return resultado;
	}
	
	public static boolean isDouble(String cell) {
		boolean resultado = false;

		String pattern = "^-?[0-9]+[,|.]+[0-9]*$";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(cell);
		if (m.find()) {
			resultado = true;
		}

		return resultado;
	}
	
	public static boolean isString(String cell) {
		boolean resultado = false;

		String pattern = "(([a-z]|[A-Z]| )+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(cell);
		if (m.find()) {
			resultado = true;
		}

		return resultado;
	}

	public static boolean isDate(String string) {
		return (isInteger(string) && string.length() == 4) || (isInteger(string) && string.length() == 2);
	}
	
	public static String[] split(String s, String exp) {
		List<String> resultado = new ArrayList<String>();
		String partial = "";
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (!exp.equals(c + "")) {
				partial = partial + c;
			} else {
				resultado.add(partial);
				partial = "";
			}
		}
		resultado.add(partial);
		Object[] o = resultado.toArray();
		String[] result = new String[o.length];
		for (int h = 0; h < o.length; h++) {
			result[h] = o[h] + "";
		}
		return result;
	}
	
	public static String generateHash(String original) {

		StringBuffer sb = new StringBuffer();
		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			log.error("Error with hash algorith", e);
		}

		if (md != null) {
			md.update(original.getBytes());
			byte[] digest = md.digest();

			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}
		}
		return sb.toString();
	}
	
	public static List<String> removeChar(List<String> list, String string) {

		List<String> result = new ArrayList<>();

		for (String line : list) {
			line = line.replaceAll(string, "");
			result.add(line);
		}

		return result;
	}
	
	public static List<String> removeFisrtChar(List<String> list, char c) {

		List<String> result = new ArrayList<>();

		for (String line : list) {
			if (line.charAt(0) == c)
				line = line.substring(1, line.length());
			result.add(line);
		}

		return result;
	}
	
	public static List<String> removeLastChar(List<String> list, char c) {

		List<String> result = new ArrayList<>();

		for (String line : list) {
			if (line.charAt(line.length() - 1) == c)
				line = line.substring(0, line.length() - 2);
			result.add(line);
		}

		return result;
	}
	
	public static File zipFolders(String folderPath, boolean removeOriginalFiles) throws IOException {
		byte[] buffer = new byte[1024];
		File zipfile = null;

		File folders = new File(folderPath);
		File folder = new File(folders.getAbsolutePath());

		if (folders.isDirectory()) {

			File[] dir = folder.listFiles();

			String zipFileName = folder.getName() + ".zip";
			zipFileName = StringUtils.stripAccents(zipFileName).replaceAll(" ", "");
			String zipFileAbsoluteName = folder.getAbsolutePath() + File.separator + zipFileName;
			zipfile = new File(zipFileAbsoluteName);

			FileOutputStream fos = new FileOutputStream(zipFileAbsoluteName);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : dir) {
				if ((file.isFile()) && (!file.getName().equals(zipFileName))) {
					ZipEntry ze = new ZipEntry(file.getName());
					zos.putNextEntry(ze);
					FileInputStream in = new FileInputStream(file.getAbsolutePath());
					int len;
					while ((len = in.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
					in.close();
					zos.closeEntry();

					if (removeOriginalFiles) {
						file.delete();

					}

				}
			}

			zos.close();
		}

		return zipfile;
	}
	
	public static String getDate() {
		SimpleDateFormat formatFullDate = new SimpleDateFormat("yyyyMMdd");
		return formatFullDate.format(new Date());
	}
	
	public static String getDate(String format) {
		SimpleDateFormat formatFullDate = new SimpleDateFormat(format);
		return formatFullDate.format(new Date());
	}

	public static void main(String[] args) {

		if ((log == null) || (log.getLevel() == null))
			PropertyConfigurator.configure("log4j.properties");

		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", "token XXX");
		headers.put("Content-Type", "application/json");

		String body = "{" +
				"\"title\": \"Prueba de creacción de issue desde api 3\"," +
				"\"body\": \"Esto es una prueba de creaccion de una isssue desde la api de github realizada por hlafuente\"," +
				"\"assignee\": \"hlafuente\", " +
				"\"labels\": [\"bug\"] " +
				"}";
		try {
			String response = Utils.processURLPost("https://api.github.com/repos/aragonopendata/local-data-aragopedia/issues", "", headers, body);
			log.info("response " + response);
		} catch (IOException e) {
			log.error("Error en main", e);
		}

	}

}
