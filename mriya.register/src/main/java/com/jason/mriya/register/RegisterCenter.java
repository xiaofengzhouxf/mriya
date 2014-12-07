package com.jason.mriya.register;

/**
 * 注册中心 根据groupId , service 注册ip ,port ,protocol 信息
 * 
 * @author jason
 *
 */
public interface RegisterCenter {

	public boolean register(RegisterInfo info);

	public RegisterInfo findRegisterService(String groupId, String service);

	public RegisterInfo removeRegisterService(String groupId, String service);
}
