package com.jason.mriya.provider.invoker;

import java.io.InputStream;
import java.io.OutputStream;

import com.jason.mriya.client.contants.TranProtocol;

/**
 * 
 * <pre>
 * <p>文件名称: RemoteInvoker.java</p>
 * 
 * <p>文件功能: 远程服务执行器</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月18日 上午11:03:04</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public interface RemoteInvoker {

	/**
	 * 
	 * <pre>
	 * Description：处理请求
	 * @param in
	 * @param out
	 * @throws Exception
	 * @return void
	 * @author name：xiaofeng.zhou
	 * </pre>
	 *
	 */
	public String handlerRequest(InputStream in, OutputStream out)
			throws Exception;

	public TranProtocol supportSerializeProtocol();
}
