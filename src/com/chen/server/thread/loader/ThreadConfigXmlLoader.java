package com.chen.server.thread.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.chen.server.thread.config.ThreadConfig;

public class ThreadConfigXmlLoader {
	private Logger log = LogManager.getLogger(ThreadConfigXmlLoader.class);

	public List<ThreadConfig> load(String file) {
		try {
			List configs = new ArrayList();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputStream in = ClassLoader.getSystemResourceAsStream(file);
			Document doc = builder.parse(in);
			NodeList list = doc.getElementsByTagName("thread");
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				NodeList childs = node.getChildNodes();

				ThreadConfig config = new ThreadConfig();
				for (int j = 0; j < childs.getLength(); j++) {
					if ("thread-name".equals(childs.item(j).getNodeName()))
						config.setThreadName(childs.item(j).getTextContent());
					else if ("thread-heart"
							.equals(childs.item(j).getNodeName())) {
						config.setHeart(Integer.parseInt(childs.item(j)
								.getTextContent()));
					}
				}
				configs.add(config);
			}
			in.close();

			return configs;
		} catch (Exception e) {
			this.log.error(e);
		}
		return null;
	}
}
