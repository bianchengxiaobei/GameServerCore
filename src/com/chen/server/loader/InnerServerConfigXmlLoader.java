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

import com.chen.server.config.InnerServerConfig;

public class InnerServerConfigXmlLoader 
{
	private Logger log = LogManager.getLogger(InnerServerConfigXmlLoader.class);
	public InnerServerConfig load(String file)
	{
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream in = new FileInputStream(file);
			Document doc = builder.parse(in);
			NodeList list = doc.getElementsByTagName("server");
			
			InnerServerConfig config = new InnerServerConfig();
			if (list.getLength() > 0)
			{
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
					else if ("server-port".equals(childs.item(j).getNodeName())) {
						config.setPort(Integer.parseInt(childs.item(j)
								.getTextContent()));
					}
				}
			}
			in.close();
			return config;
		} catch (Exception e) {
			this.log.error(e,e);
		}
		return null;
	}
}
