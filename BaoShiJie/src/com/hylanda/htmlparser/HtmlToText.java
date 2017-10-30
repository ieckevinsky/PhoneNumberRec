package com.hylanda.htmlparser;

import java.io.IOException;

import org.htmlparser.scanners.ScriptScanner;
import org.xml.sax.SAXException;

public class HtmlToText {
	public static String getFormatText_SAX(String strHtml) {
		if(strHtml == null || strHtml.length() == 0)
			return "";
		//strHtml = strHtml.replace('[', '<');
		//strHtml = strHtml.replace(']', '>');

		org.xml.sax.ContentHandler content = new HLContentHandler ();		
		try {
			org.xml.sax.XMLReader reader = null;
			reader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader (
					"org.htmlparser.sax.XMLReader");
			reader.setContentHandler (content);
			org.xml.sax.ErrorHandler errors = new HLErrorHandler ();
			reader.setErrorHandler (errors);
		
			org.htmlparser.sax.XMLReader htmReader = (org.htmlparser.sax.XMLReader)reader;
			ScriptScanner.STRICT = true;
			htmReader.parse(strHtml,"UTF-8");
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ((HLContentHandler)content).m_sbText.toString();
	}
}
