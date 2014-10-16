package com.jason.mriya.provider.spring;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mina.core.buffer.IoBuffer;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.jason.mriya.client.conn.io.IoBufferInputStream;
import com.jason.mriya.client.conn.io.IoBufferOutputStream;
import com.jason.mriya.client.contants.Constants;
import com.jason.mriya.client.exception.MriyaRuntimeException;
import com.jason.mriya.client.utils.CommonUtils;
import com.jason.mriya.provider.export.MriyaRpcExporter;
import com.jason.mriya.provider.export.RemoteExporter;
import com.jason.mriya.provider.invoker.RemoteInvoker;
import com.jason.mriya.provider.invoker.RemoteInvokerFactory;
import com.jason.mriya.provider.tran.RpcRequest;
import com.jason.mriya.provider.tran.RpcResponse;
import com.jason.mriya.provider.tran.codec.MriyaCodec;

/**
 * 
 * <pre>
 *  文件功能:spring 代码暴露 
 *   
 *  配置：
 *   {@code
 *   org.springframework.web.servlet.DispatcherServlet
 *  
 *      <servlet>
 *  		<servlet-name>service</servlet-name>
 *  		<servlet-class>
 *              org.springframework.web.servlet.DispatcherServlet
 *      	</servlet-class >
 *      	<load-on-startup >1</load-on-startup>
 *      </servlet>
 *      <servlet-mapping >
 *          <servlet-name>service</servlet-name>
 *          <url-pattern>/service/*</url-pattern>
 *      </servlet-mapping >
 *    }
 *   
 *  然后可以使用 {@code http://${serviceName}:${port}/${contextPath}/service/httpService} 访问
 *  
 *  编程者: xiaofeng.zhou
 *  初作时间: 2014年9月22日 上午10:58:08
 *  
 * 版本: version 1.0
 * </pre>
 */
public class MriyaSpringExporter extends MriyaRpcExporter implements
		HttpRequestHandler, RemoteExporter, BeanNameAware {

	private String serviceInterface;

	private MriyaCodec codec = new MriyaCodec();

	public MriyaSpringExporter() {
		super();
	}

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (!"POST".equals(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(
					request.getMethod(), new String[] { "POST" },
					"Mriya Exporter invocation only supports POST requests");
		}

		RpcResponse resp = new RpcResponse();
		ServletInputStream inputStream = request.getInputStream();
		ServletOutputStream outputStream = response.getOutputStream();
		RpcRequest rpcRequest = null;

		try {
			// decode
			int bodyLength = request.getContentLength();
			byte[] bodystream = new byte[bodyLength];
			int haveReadBytes = 0;
			int totalReadBytes = 0;
			for (; totalReadBytes < bodyLength; totalReadBytes += haveReadBytes) {
				haveReadBytes = inputStream.read(bodystream, totalReadBytes,
						bodyLength - totalReadBytes);
			}
			ByteBuffer byteBuffer = ByteBuffer.wrap(bodystream);
			IoBuffer bf = IoBuffer.wrap(byteBuffer);

			rpcRequest = (RpcRequest) codec.decode(new IoBufferInputStream(bf));
			resp.setSerializeProtocol(rpcRequest.getSerializeProtocol());

			// invoke
			IoBufferOutputStream os = new IoBufferOutputStream();
			RemoteInvoker invoker = RemoteInvokerFactory.create(this);

			String methodName = invoker.handlerRequest(
					rpcRequest.getInputStream(), os);

			resp.setOutputBuffer(os.flip());
			resp.setBean(rpcRequest.getBean());
			resp.setMethod(methodName);
			resp.setSeq(rpcRequest.getSeq());
			IoBufferOutputStream encode = codec.encode(resp);
			outputStream.write(encode.flip().array());
			// encode

		} catch (Throwable e) {
			// throw new NestedServletException(
			// "Mriya Exporter invocation failed", e);

			byte[] serialize = CommonUtils.serialize(new MriyaRuntimeException(
					"Remote exception.", e));
			IoBuffer iobuffer = IoBuffer.wrap(serialize);
			resp.setOutputBuffer(iobuffer);
			resp.setBean(rpcRequest != null ? rpcRequest.getBean() : "error");
			resp.setMethod(Constants.EXCEPTION_METHOD);
			resp.setSeq(rpcRequest != null ? rpcRequest.getSeq() : 0);
			IoBufferOutputStream encode = codec.encode(resp);
			outputStream.write(encode.flip().array());
		}
	}

	public void setServiceInterface(String serviceInterface) {
		this.serviceInterface = serviceInterface;
		try {
			super.setServiceInterface(Class.forName(this.serviceInterface));
		} catch (ClassNotFoundException e) {
			throw new MriyaRuntimeException(e);
		}
	}

	@Override
	public void setBeanName(String name) {
		super.setName(name);
	}
}
