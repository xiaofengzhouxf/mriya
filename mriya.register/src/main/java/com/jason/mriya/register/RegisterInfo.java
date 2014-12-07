package com.jason.mriya.register;

import java.io.Serializable;

/**
 * 
 * @author jason
 *
 */
public class RegisterInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String groupId = "DEFAULT_GROUP";

	private String service;

	private String ip;

	private int port;

	private int protocol;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

}
