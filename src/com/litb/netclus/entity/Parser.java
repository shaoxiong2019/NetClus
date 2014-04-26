package com.litb.netclus.entity;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Parser extends DefaultHandler {

	private Store s;
	private int attrType = 0;
	private String text;
	private String itemId;

	public Parser(String file, Store s) {
		this.s = s;
		SAXParserFactory spf = SAXParserFactory.newInstance();

		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(file, this);
		} catch (SAXException ex) {
			ex.printStackTrace();
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
		if (qName.equalsIgnoreCase("LINK")) {
			String id = attrs.getValue(0);s.e.put(id, new Edge(id, attrs.getValue(1), attrs.getValue(2),Double.parseDouble(attrs.getValue(3))));
		} else if (qName.equalsIgnoreCase("ATTRIBUTE")) {
			String name = attrs.getValue("NAME");
			if (name.equals("object-type")) {
				attrType = 1;
			} else if (name.equalsIgnoreCase("item")) {
				attrType = 2;
			} else if (name.equalsIgnoreCase("customer")) {
				attrType = 3;
			} else if (name.equalsIgnoreCase("merchant")) {
				attrType = 4;
			} else if (name.equalsIgnoreCase("category")) {
				attrType = 5;
			}
		} else if (qName.equalsIgnoreCase("ATTR-VALUE")) {
			itemId = attrs.getValue("ITEM-ID");
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase("ATTRIBUTE")) {
			attrType = 0;
		} else if (qName.equalsIgnoreCase("ATTR-VALUE")) {
			itemId = null;
		} else {
			switch (attrType) {
			case 1:
				if (text.equals("order")) {
					s.order(itemId);
				}
				break;
			case 2:
				s.item(itemId, text);
				break;
			case 3:
				s.customer(itemId, text);
				break;
			case 4:
				s.merchant(itemId, text);
				break;
			case 5:
				s.category(itemId, text);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		text = new String(ch, start, length);
	}

}
