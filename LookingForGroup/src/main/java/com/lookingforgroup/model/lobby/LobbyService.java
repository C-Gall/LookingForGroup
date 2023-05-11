package com.lookingforgroup.model.lobby;

public class LobbyService {
	private int id;
	private String serviceName;
	private String serviceUrl;
	
	public LobbyService() {
		
	}
	
	public LobbyService(String serviceName, String serviceUrl) {
		super();
		this.serviceName = serviceName;
		this.serviceUrl = serviceUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceBaseUrl) {
		this.serviceUrl = serviceBaseUrl;
	}
}
