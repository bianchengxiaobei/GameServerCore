package com.chen.server.loader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.chen.server.config.MinaServerConfig;
/**
 * Mina服务器配置信息加载工具类
 * @author chen
 *
 */
public class MinaServerConfigXmlLoader 
{
	private Logger log = LogManager.getLogger(MinaServerConfigXmlLoader.class);
	public MinaServerConfig load(String file)
	{
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputStream in = new FileInputStream(file);
			Document doc = builder.parse(in);
			NodeList list = doc.getElementsByTagName("server");

			MinaServerConfig config = new MinaServerConfig();
			if (list.getLength() > 0) {
				Node node = list.item(0);
				NodeList childs = node.getChildNodes();

				for (int j = 0; j < childs.getLength(); j++) {
					if ("server-name".equals(childs.item(j).getNodeName()))
						config.setName(childs.item(j).getTextContent());
					else if ("server-id".equals(childs.item(j).getNodeName()))
						config.setId(Integer.parseInt(childs.item(j)
								.getTextContent()));
					else if ("server-web".equals(childs.item(j).getNodeName()))
						config.setWeb(childs.item(j).getTextContent());
					else if ("server-mina-port".equals(childs.item(j).getNodeName()))
					{
						config.setMina_port(Integer.parseInt(childs.item(j).getTextContent()));
					}
				}
			}
			in.close();
			return config;
		} catch (Exception e) {
			this.log.error(e.toString());
		}
		return null;
	}
}
