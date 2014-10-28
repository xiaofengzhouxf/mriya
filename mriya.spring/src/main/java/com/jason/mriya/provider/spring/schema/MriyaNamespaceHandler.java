package com.jason.mriya.provider.spring.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * <mriya:client >
 * 
 * </mriya:client >
 * 
 * <mriya:server directStart="true" workSize="10" port="9090">
 * 
 * 
 * </mriya:server>
 * 
 * 
 * @author xiaofeng.zhou
 *
 */
public class MriyaNamespaceHandler extends NamespaceHandlerSupport {
	private static final String CLIENT = "client";
	private static final String SERVER = "server";

	public void init() {
		registerBeanDefinitionParser(SERVER,
				new MriyaSpringServerBeanDefinitionParser());

		registerBeanDefinitionParser(CLIENT,
				new MriyaSpringClientBeanDefinitionParser());
	}
}