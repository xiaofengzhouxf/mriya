package com.jason.mriya.provider.spring.schema;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.jason.mriya.provider.spring.MriyaSpringConsumer;

/**
 * 
 * <mriya:client id="demo" connTimeout="5" readTimeout="5" api="com.x.y.Demo"
 * 
 * url="hessian://127.0.0.1:9090/hello" />
 * 
 * @author xiaofeng.zhou
 * 
 *
 */
public class MriyaSpringClientBeanDefinitionParser extends
		AbstractSingleBeanDefinitionParser {

	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		String connTimeout = element.getAttribute("connTimeout");
		String readTimeout = element.getAttribute("readTimeout");
		String api = element.getAttribute("api");
		String url = element.getAttribute("url");

		if (StringUtils.hasText(connTimeout)) {
			bean.addPropertyValue("connTimeout", Integer.valueOf(connTimeout));
		}
		if (StringUtils.hasText(readTimeout)) {
			bean.addPropertyValue("readTimeout", Integer.valueOf(readTimeout));
		}
		if (StringUtils.hasText(api)) {
			bean.addPropertyValue("api", api);
		}
		if (StringUtils.hasText(url)) {
			bean.addPropertyValue("url", url);
		}

		bean.getRawBeanDefinition().setBeanClass(MriyaSpringConsumer.class);

		// try {
		// bean.getRawBeanDefinition().setBeanClass(Class.forName(api));
		// } catch (ClassNotFoundException e) {
		// throw new RuntimeException("[mriya] parse bean error.");
		// }
	}
}