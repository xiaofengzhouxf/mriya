package com.jason.mriya.provider.invoker.hessian;

import java.io.InputStream;
import java.io.OutputStream;

import com.caucho.hessian.io.SerializerFactory;
import com.jason.mriya.client.contants.TranProtocol;
import com.jason.mriya.provider.invoker.RemoteInvoker;

/**
 * 
 * <pre>
 * <p>文件名称: RemoteServiceExport.java</p>
 * 
 * <p>文件功能: hessian服务暴露</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月11日 下午4:54:24</p>
 * 
 * <p>版本: version 1.0 </p>
 * 
 * <p>输入说明: </p>
 * 
 * <p>输出说明: </p>
 * 
 * <p>程序流程: </p>
 * 
 * <p>============================================</p>
 * <p>修改序号:</p>
 * <p>时间:	 </p>
 * <p>修改者:  </p>
 * <p>修改内容:  </p>
 * <p>============================================</p>
 * </pre>
 */
public class HessianInvoker implements RemoteInvoker {
	private SerializerFactory serializerFactory = new SerializerFactory();
	private HessianSkeletonProxy proxy;

	public HessianInvoker(final Object bean, final Class<?> beanClass) {
		proxy = new HessianSkeletonProxy(bean, beanClass);
	}

	@Override
	public String handlerRequest(InputStream in, OutputStream out)
			throws Exception {
		
		return proxy.invoke(in, out, serializerFactory);

	}

	@Override
	public TranProtocol supportSerializeProtocol() {
		return TranProtocol.HESSIAN;
	}

}
