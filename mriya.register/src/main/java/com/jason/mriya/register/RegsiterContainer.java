package com.jason.mriya.register;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.jason.mriya.register.exception.MriyaRegisterException;

/**
 * 注册中心容器
 * 
 * @author jason
 *
 */
public class RegsiterContainer {

	private static RegsiterContainer contianer = new RegsiterContainer();

	private static RegisterCenter center = null;
	static {

		ServiceLoader<RegisterCenter> load = ServiceLoader
				.<RegisterCenter> load(RegisterCenter.class,
						RegsiterContainer.class.getClassLoader());

		if (load != null) {
			Iterator<RegisterCenter> registerCenter = load.iterator();

			if (registerCenter != null) {
				center = registerCenter.next();
			}
		}
	}

	public static RegsiterContainer getInstance() {
		return contianer;
	}

	public boolean registerService(RegisterInfo info) {
		if (center == null) {
			throw new MriyaRegisterException("register center is null.");
		}
		return center.register(info);
	}

	public RegisterInfo findRegisterService(String groupId, String service) {
		if (center == null) {
			throw new MriyaRegisterException("register center is null.");
		}

		return center.findRegisterService(groupId, service);
	}

	public RegisterInfo removeRegisterService(String groupId, String service) {
		if (center == null) {
			throw new MriyaRegisterException("register center is null.");
		}

		return center.removeRegisterService(groupId, service);
	}

	public boolean hasCenter() {
		return center != null;
	}
}
