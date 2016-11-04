package test_api

class TestApi {

	public static main(args) {
		ProxySetup.setProxy();
		testGetContainerById()
	}

	private static testGetContainerById() {
		// Determine the containers URIs and retrieve them
		String containerId = "27-53";
//		String	uri = "https://${GlobalProperties.API_HOST}/api/v2/containers/${containerId}";
		String exception = "";

		Node container = getContainerById(containerId);

//		println container //.name();
//				println container.message.text();

		if (container.name().toString().endsWith("exception")) {
			println "Exception occurred!!!"
			exception = container.message.text();
			println exception;
		}
		else {
			println "No exception occurred!!!"
		}


		// For each container, print its limsid
		//		containers.'container'.each { println it.@limsid }

		String xml = GLSRestApiUtils.nodeToXmlString(container);
		println xml;
	}

	private static getContainerById(id) {
		String containerId = id
		String	uri = "https://${GlobalProperties.API_HOST}/api/v2/containers/${containerId}";
		Node container = GLSRestApiUtils.httpGET(uri, GlobalProperties.API_USER, GlobalProperties.API_PASSWORD);
		return container
	}


}
