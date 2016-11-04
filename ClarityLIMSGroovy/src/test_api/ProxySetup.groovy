package test_api

class ProxySetup {

	public static void main(args) {
		setProxy();
	}

	public static void setProxy() {
		setDevProxy();
	}

	/**
	 * Proxy settings for local dev environment
	 */
	private static void setDevProxy() {
		Properties systemSettings = System.getProperties();
		systemSettings.put("proxySet", "true");
		systemSettings.put("http.proxyHost", "srvddproxy02");
		systemSettings.put("http.proxyPort", "3128");
		systemSettings.put("https.proxyHost", "srvddproxy02");
		systemSettings.put("https.proxyPort", "3128");
	}

	/**
	 * Proxy settings for test installation
	 */
	private static void setTestProxy() {
		Properties systemSettings = System.getProperties();
		systemSettings.put("proxySet", "false");
	}

	/**
	 * Proxy settings for prod installation
	 */
	private static void setProdProxy() {
		Properties systemSettings = System.getProperties();
		systemSettings.put("proxySet", "false");
	}
}
