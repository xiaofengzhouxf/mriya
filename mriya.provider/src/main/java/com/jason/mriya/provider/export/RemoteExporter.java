package com.jason.mriya.provider.export;

/**
 * 
 * <pre>
 * <p>文件名称: RemoteExport.java</p>
 * 
 * <p>文件功能: 服务暴露 </p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月11日 下午4:57:59</p>
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
public interface RemoteExporter {

	/**
	 * 
	 * <pre>
	 * Description：暴露的名称
	 * @param bean
	 * @return void
	 * @author name：xiaofeng.zhou
	 * </pre>
	 *
	 */
	public void setName(String name);

	/**
	 * 
	 * <pre>
	 * Description：协议
	 * @param protocol
	 * @return void
	 * @author name：xiaofeng.zhou
	 * </pre>
	 *
	 */
	public void setProtocol(String protocol);

	/**
	 * 
	 * <pre>
	 * Description： 设置服务实现
	 * @param service
	 * @return void
	 * @author name：xiaofeng.zhou
	 * </pre>
	 *
	 */
	public void setService(Object service);

	/**
	 * 
	 * <pre>
	 * Description：服务接口
	 * @param interface
	 * @return void
	 * @author name：xiaofeng.zhou
	 * </pre>
	 *
	 */
	public void setServiceInterface(Class<?> inter);

	public Object getService();

	public Class<?> getServiceInterface();

	public String getProtocol();

	public String getName();
}
