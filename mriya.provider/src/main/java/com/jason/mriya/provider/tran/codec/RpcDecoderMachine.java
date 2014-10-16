package com.jason.mriya.provider.tran.codec;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.statemachine.DecodingState;
import org.apache.mina.filter.codec.statemachine.DecodingStateMachine;
import org.apache.mina.filter.codec.statemachine.FixedLengthDecodingState;
import org.apache.mina.filter.codec.statemachine.IntegerDecodingState;
import org.apache.mina.filter.codec.statemachine.SingleByteDecodingState;

import com.jason.mriya.client.conn.io.IoBufferInputStream;
import com.jason.mriya.client.contants.TranProtocol;
import com.jason.mriya.provider.tran.RpcRequest;

/**
 * 
 * <pre>
 * <p>文件名称: RpcDecoderMachine.java</p>
 * 
 * <p>文件功能: 解析状态机</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月16日 下午3:42:53</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class RpcDecoderMachine extends DecodingStateMachine {
	public static final String DEFAULT_CHARSET = "ASCII";

	private CharsetDecoder charsetDecoder = Charset.forName(DEFAULT_CHARSET)
			.newDecoder();

	@Override
	protected void destroy() throws Exception {
		charsetDecoder.reset();
	}

	@Override
	protected DecodingState finishDecode(List<Object> childProducts,
			ProtocolDecoderOutput out) throws Exception {
		if (childProducts.size() < 5) {
			return null;
		}
		TranProtocol protocol = (TranProtocol) childProducts.get(0);
		int childs = childProducts.size();
		for (int i = 1; i < childs; i = i + 4) {
			RpcRequest request = new RpcRequest();
			request.setSerializeProtocol(protocol);
			request.setBean((String) childProducts.get(i + 1));
			request.setSeq((Integer) childProducts.get(i + 2));
			request.setInputStream((InputStream) childProducts.get(i + 3));
			out.write(request);
			charsetDecoder.reset();
		}

		return null;
	}

	@Override
	protected DecodingState init() throws Exception {

		System.out.println("DecodingState-------------get this");

		return checkTransportState;
	}

	private DecodingState checkTransportState = new DecodingState() {

		public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out)
				throws Exception {
			if (in.hasRemaining()) {
				byte version = in.get();
				byte b = in.get();
				if (version == 1 && b == TranProtocol.HESSIAN.getCode()) {
					out.write(TranProtocol.HESSIAN);

					System.out
							.println("return beanNameLengthState-------------");
					return beanNameLengthState;
				} else {
					System.out.println("Protocol not support:" + b);
				}
			}
			return this;
		}

		public DecodingState finishDecode(ProtocolDecoderOutput out)
				throws Exception {
			System.out.println("finished-------------");
			return null;
		}

	};

	private DecodingState beanNameLengthState = new SingleByteDecodingState() {

		@Override
		protected DecodingState finishDecode(byte s, ProtocolDecoderOutput out)
				throws Exception {
			return new FixedLengthDecodingState(s) {

				@Override
				protected DecodingState finishDecode(IoBuffer product,
						ProtocolDecoderOutput out) throws Exception {
					out.write(product.getString(charsetDecoder));

					System.out.println("return sequenceState-------------");

					return sequenceState;
				}
			};
		}
	};

	private DecodingState sequenceState = new IntegerDecodingState() {

		@Override
		protected DecodingState finishDecode(int value,
				ProtocolDecoderOutput out) throws Exception {
			out.write(value);

			System.out.println("return bodyState-------------");
			return bodyState;
		}

	};

	private DecodingState bodyState = new IntegerDecodingState() {

		@Override
		protected DecodingState finishDecode(int value,
				ProtocolDecoderOutput out) throws Exception {
			return new FixedLengthDecodingState(value) {

				@Override
				protected DecodingState finishDecode(IoBuffer product,
						ProtocolDecoderOutput out) throws Exception {
					out.write(new IoBufferInputStream(product));
					System.out.println("return null-------------");
					return null;
				}
			};
		}
	};
}
