package com.jason.mriya.provider.spring.schema;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.jason.mriya.provider.spring.MriyaSpringProvider;

/**
 * <pre>
 * 
 * <mriya:server id="demo" workSize="10" port="9090" 
 * 
 * 					service="beanname" serviceInterface="com.x.y.Demo" />
 * 
 * 下面参数可选:
 * 
 * port
 * workSize
 * serviceInterface
 * 
 * </pre>
 * 
 * @author xiaofeng.zhou
 *
 */
public class MriyaSpringServerBeanDefinitionParser extends
		AbstractSingleBeanDefinitionParser {
	protected Class<MriyaSpringProvider> getBeanClass(Element element) {
		return MriyaSpringProvider.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder bean) {

		String id = element.getAttribute("id");
		String port = element.getAttribute("port");
		String workSize = element.getAttribute("workSize");
		String service = element.getAttribute("service");
		String serviceInterface = element.getAttribute("serviceInterface");

		if (StringUtils.hasText(workSize)) {
			bean.addPropertyValue("workSize", Integer.valueOf(workSize));
		}
		if (StringUtils.hasText(port)) {
			bean.addPropertyValue("port", Integer.valueOf(port));
		}
		if (StringUtils.hasText(service)) {
			bean.addPropertyReference("service", service);
		}
		if (StringUtils.hasText(serviceInterface)) {
			bean.addPropertyValue("serviceInterface", serviceInterface);
		}
		if (StringUtils.hasText(id)) {
			bean.addPropertyValue("name", id);
		}

		bean.setInitMethodName("init");
	}
}