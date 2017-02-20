package com.chen.mina.code;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
/**
 * 服务器编解码工厂类
 * @author chen
 *
 */
public class ServerProtocolCodecFactory implements ProtocolCodecFactory{

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return new ServerProtocolDecoder();
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return new ServerProtocolEncoder();
	}

}
