package com.jason.mriya.provider.tran;

import java.io.Serializable;

import com.jason.mriya.client.contants.TranProtocol;

/**
 * 
 * <pre>
 * <p>文件名称: BaseMessage.java</p>
 * 
 * <p>文件功能: 传输的基本信息</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月12日 上午10:03:55</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class BaseMessage implements Serializable {
	private static final long serialVersionUID = -3325574124346248561L;

	private byte version = 1;
	private String bean;
	private TranProtocol serializeProtocol;

	public TranProtocol getSerializeProtocol() {
		return serializeProtocol;
	}

	public void setSerializeProtocol(TranProtocol serializeProtocol) {
		this.serializeProtocol = serializeProtocol;
	}

	private int seq;

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public String getBean() {
		return bean;
	}

	public int getSeq() {
		return seq;
	}

	public void setBean(String bean) {
		this.bean = bean;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	@Override
	public String toString() {
		return "BaseMessage [version=" + version + ", bean=" + bean
				+ ", serializeProtocol=" + serializeProtocol + ", seq=" + seq
				+ "]";
	}

}
