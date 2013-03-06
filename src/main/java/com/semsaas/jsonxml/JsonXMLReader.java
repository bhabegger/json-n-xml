package com.semsaas.jsonxml;

import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class JsonXMLReader implements XMLReader {
	public final static String XJSON_URI = "http://www.objectml.org/ns/data/xjson";
	
	ContentHandler contentHandler;
	DTDHandler dtdHandler;
	EntityResolver entityResolver;
	ErrorHandler errorHandler;
	
	private String targetNamespace		= XJSON_URI;
	private String targetPrefix			= null;

	private String objectLocalName		= "object";
	private String arrayLocalName		= "array";
	private String valueLocalName		= "value";
	private String nameAttrLocalName	= "name";

	private String objectQName			= objectLocalName; 
	private String arrayQName			= arrayLocalName;	
	private String valueQName			= valueLocalName;

	private String nameAttrQName		= nameAttrLocalName;
	
	
	public JsonXMLReader() {
		targetNamespace = XJSON_URI;
		
		DefaultHandler handler = new DefaultHandler();
		contentHandler = handler;
		dtdHandler = handler;
		entityResolver =  handler;
		errorHandler = handler;
	}
	
	public String getTargetPrefix() {
		return targetPrefix;
	}
	
	public void setTargetPrefix(String prefix) {
		targetPrefix = prefix;
		if(targetPrefix != null) {
			objectQName = targetPrefix + ":" + objectLocalName;
			arrayQName = targetPrefix + ":" + arrayLocalName;
		} else {
			objectQName = objectLocalName;
			arrayQName = arrayLocalName;
		}
	}
	
	public ContentHandler getContentHandler() {
		return contentHandler;
	}

	public DTDHandler getDTDHandler() {
		return dtdHandler;
	}

	public EntityResolver getEntityResolver() {
		return entityResolver;
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setContentHandler(ContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	public void setDTDHandler(DTDHandler dtdHandler) {
		this.dtdHandler = dtdHandler;
	}

	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;		
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler  = errorHandler;	
	}
	
	public boolean getFeature(String feature) throws SAXNotRecognizedException, SAXNotSupportedException {
		return false;
	}

	public Object getProperty(String property) throws SAXNotRecognizedException, SAXNotSupportedException {
		return null;
	}

	public void setFeature(String feature, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {		
	}

	public void setProperty(String property, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
	}
	
	public void parse(InputSource inputSource) throws IOException, SAXException {
		JsonFactory f = new JsonFactory();
		JsonParser jp = f.createJsonParser(inputSource.getCharacterStream());
		parse(jp);
	}

	public void parse(String jsonData) throws IOException, SAXException {
		JsonFactory f = new JsonFactory();
		JsonParser jp = f.createJsonParser(jsonData);
		parse(jp);
	}
		
	private void parse(JsonParser jp) throws SAXException, IOException {
		try {
			JsonToken curTok = jp.nextToken();
			contentHandler.startDocument();
			AttributesImpl atts = new AttributesImpl();
			while (curTok != null) {
				if(curTok == JsonToken.START_OBJECT) {
					contentHandler.startElement(targetNamespace, objectLocalName, objectQName, atts);
					atts = new AttributesImpl();
				} else if(curTok == JsonToken.START_ARRAY) {
					contentHandler.startElement(targetNamespace, arrayLocalName, arrayQName, atts);
					atts = new AttributesImpl();
				} else if(curTok == JsonToken.END_OBJECT) {
					contentHandler.endElement(targetNamespace, objectLocalName, objectQName);
				} else if (curTok == JsonToken.END_ARRAY) {
					contentHandler.endElement(targetNamespace, arrayLocalName, arrayQName);
				} else if(curTok == JsonToken.FIELD_NAME) {
					atts = new AttributesImpl();
					atts.addAttribute(null, nameAttrLocalName, nameAttrQName, "CDATA", jp.getCurrentName());
				} else {
					String characters = null;
					if(curTok == JsonToken.VALUE_STRING) {
						characters = jp.getText();
					} else if(curTok == JsonToken.VALUE_NUMBER_INT) {
						characters = jp.getBigIntegerValue().toString();
					} else if(curTok == JsonToken.VALUE_NUMBER_FLOAT) {
						characters = ""+jp.getDoubleValue();
					} else if(curTok == JsonToken.VALUE_FALSE) {
						characters = "false";
					} else if(curTok == JsonToken.VALUE_TRUE) {
						characters = "true";
					}
				
					contentHandler.startElement(targetNamespace, valueLocalName, valueQName, atts);
					atts = new AttributesImpl();
					if(characters != null) {
						contentHandler.characters(characters.toCharArray(), 0, characters.length());
					}
					contentHandler.endElement(targetNamespace, valueLocalName, valueQName);
				}
				curTok = jp.nextToken();
				
			}
			jp.close(); // ensure resources get cleaned up timely and properly
			contentHandler.endDocument();
		} catch (JsonParseException e) {
			throw new SAXException(e);
		} finally {
			
		}
	}
}
