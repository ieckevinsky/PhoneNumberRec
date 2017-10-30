package com.hylanda.htmlparser;


import java.util.ArrayList;
import java.util.List;

import org.htmlparser.util.Translate;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


public class HLContentHandler implements ContentHandler {
	
	public StringBuffer m_sbText = new StringBuffer();
	public List<String> m_listLink = new ArrayList<String>();
	public List<String> m_listImg = new ArrayList<String>();
	private boolean m_bIsInScript = false;
	private boolean m_bAddUnkownTag = false;

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(m_bAddUnkownTag){
			m_sbText.append(">");
			m_bAddUnkownTag = false;
		}
		if(!m_bIsInScript){
			//m_sbText.append(Translate.decode(new String(ch, start, length)));
			m_sbText.append(ch, start, length);
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if(localName != null){
			if(localName.equals("SCRIPT") || localName.equals("STYLE")){
				m_bIsInScript = true;
			}
			else if(localName.equals("A")){
				String strHref = atts.getValue("href");
				m_listLink.add(strHref);
			}
			else if(localName.equals("IMG")){
				String strHref = atts.getValue("src");
				m_listImg.add(strHref);
			}else if(localName.equals("DIV")){
				m_sbText.append("\n");
			}
		}
		else if(uri == null && atts != null) {
			org.htmlparser.sax.Attributes hpAtts = (org.htmlparser.sax.Attributes)atts;
			org.htmlparser.Tag tag = hpAtts.getTag();
			if(tag != null){
				m_sbText.append("<").append(tag.getText());
				m_bAddUnkownTag = true;
				/*int size = tag.getAttributesEx().size();
				for (int i = 0; i < size; i++) {
					org.htmlparser.Attribute atr = 
						(org.htmlparser.Attribute)tag.getAttributesEx().get(i);
					if(atr.getName() != null)
						m_sbText.append(atr.getName());
					if(atr.getValue() != null)
						m_sbText.append(atr.getValue());
				}*/
			}
		}
	}
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(localName != null){
			if(localName.equals("SCRIPT") || localName.equals("STYLE")){
				m_bIsInScript = false;
			}
		}
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)throws SAXException {}
	@Override
	public void endPrefixMapping(String prefix) throws SAXException {}
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)throws SAXException {}
	@Override
	public void processingInstruction(String target, String data)throws SAXException {}
	@Override
	public void setDocumentLocator(Locator locator) {}
	@Override
	public void skippedEntity(String name) throws SAXException {}
	@Override
	public void startDocument() throws SAXException {}
	@Override
	public void endDocument() throws SAXException {}
}
