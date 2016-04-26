package com.localidata.util;



public class Cookies {
        
    private Map store;

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE_VALUE_DELIMITER = ";";
    private static final String PATH = "path";
    private static final String EXPIRES = "expires";
    private static final String DATE_FORMAT = "EEE, dd-MMM-yyyy hh:mm:ss z";
    private static final String SET_COOKIE_SEPARATOR="; ";
    private static final String COOKIE = "Cookie";

    private static final char NAME_VALUE_SEPARATOR = '=';
    private static final char DOT = '.';
    
    private DateFormat dateFormat;

    public Cookies() {

	store = new HashMap();
	dateFormat = new SimpleDateFormat(DATE_FORMAT);
    }
    

    public void storeCookies(URLConnection conn) throws IOException {
	
	String domain = getDomainFromHost(conn.getURL().getHost());
	
	
	
	if (store.containsKey(domain)) {
	    domainStore = (Map)store.get(domain);
	} else {
	    domainStore = new HashMap();
	    store.put(domain, domainStore);    
	}
	
	
	
	
	
	String headerName=null;
	for (int i=1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
	    if (headerName.equalsIgnoreCase(SET_COOKIE)) {
		Map cookie = new HashMap();
		StringTokenizer st = new StringTokenizer(conn.getHeaderField(i), COOKIE_VALUE_DELIMITER);
		
		
		if (st.hasMoreTokens()) {
		    String token  = st.nextToken();
		    String name = token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR));
		    String value = token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length());
		    domainStore.put(name, cookie);
		    cookie.put(name, value);
		}
    
		    String token  = st.nextToken();
		    cookie.put(token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR)).toLowerCase(),
		     token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length()));
	    }
	}
    }
 

    public void setCookies(URLConnection conn) throws IOException {
	
	URL url = conn.getURL();
	String domain = getDomainFromHost(url.getHost());
	String path = url.getPath();
	
	Map domainStore = (Map)store.get(domain);
	if (domainStore == null) return;
	StringBuffer cookieStringBuffer = new StringBuffer();
	
	Iterator cookieNames = domainStore.keySet().iterator();
	while(cookieNames.hasNext()) {
	    String cookieName = (String)cookieNames.next();
	    Map cookie = (Map)domainStore.get(cookieName);
	    if (comparePaths((String)cookie.get(PATH), path) && isNotExpired((String)cookie.get(EXPIRES))) {
		cookieStringBuffer.append(cookieName);
		cookieStringBuffer.append("=");
		cookieStringBuffer.append((String)cookie.get(cookieName));
		if (cookieNames.hasNext()) cookieStringBuffer.append(SET_COOKIE_SEPARATOR);
	    }
	}
	try {
	    conn.setRequestProperty(COOKIE, cookieStringBuffer.toString());
	} catch (java.lang.IllegalStateException ise) {
	    IOException ioe = new IOException("Illegal State! Cookies cannot be set on a URLConnection that is already connected. " 
	    + "Only call setCookies(java.net.URLConnection) AFTER calling java.net.URLConnection.connect().");
	    throw ioe;
	}
    }

    private String getDomainFromHost(String host) {
	if (host.indexOf(DOT) != host.lastIndexOf(DOT)) {
	    return host.substring(host.indexOf(DOT) + 1);
	} else {
	    return host;
	}
    }

    private boolean isNotExpired(String cookieExpires) {
	if (cookieExpires == null) return true;
	Date now = new Date();
	try {
	    return (now.compareTo(dateFormat.parse(cookieExpires))) <= 0;
	} catch (java.text.ParseException pe) {
	    pe.printStackTrace();
	    return false;
	}
    }

    private boolean comparePaths(String cookiePath, String targetPath) {
	if (cookiePath == null) {
	    return true;
	} else if (cookiePath.equals("/")) {
	    return true;
	} else if (targetPath.regionMatches(0, cookiePath, 0, cookiePath.length())) {
	    return true;
	} else {
	    return false;
	}
	
    }
    

    public String toString() {
	return store.toString();
    }
    

    
}
