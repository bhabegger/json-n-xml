/*
 *    Copyright 2013 Benjamin Habegger
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package com.semsaas.jsonxml.examples;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.semsaas.jsonxml.XMLJsonGenerator;

public class Examples {
	public static class Json2Dom  {
		public static Node json2dom(InputStream jsonStream) throws SAXException, TransformerException {
			// Create a JSON XML reader
			XMLReader reader = XMLReaderFactory.createXMLReader("com.semsaas.jsonxml.JsonXMLReader");
	    	
			// Prepare a reader with the JSON file as input
	    	InputStreamReader stringReader = new InputStreamReader(jsonStream);
			SAXSource saxSource = new SAXSource(reader, new InputSource(stringReader));

			// Prepare a DOMResult which will hold the DOM of the xjson
			DOMResult domResult = new DOMResult();
			
			// Run SAX processing through a transformer
			// (This could be done more simply, but we have here the opportunity to pass our xjson through
			// an XSLT and get a legacy XML output ;) ) 
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.transform(saxSource, domResult);
			
			// Get our nice DOM object
			return domResult.getNode();
		}
		
		public static void main(String[] args) {
	    	try {
	    		InputStream is = args.length > 0 ? new FileInputStream(args[0]) : System.in;
	    		OutputStream os = args.length > 1 ? new FileOutputStream(args[1]) : System.out;
			
	    		Node node = json2dom(is);
				
	    		serialize(node, os);
	    	} catch(Exception e) {
	    		e.printStackTrace(System.err);
	    	}
		}
	}
	
	public static class XJson2json  {
		public static void xjson2json(InputStream is, Writer w) throws IOException, SAXException {
			XMLJsonGenerator handler = new XMLJsonGenerator();
    		handler.setOutputWriter(w);
    		
    		XMLReader reader = XMLReaderFactory.createXMLReader();
    		reader.setContentHandler(handler);
    		reader.parse(new InputSource(is));
		}
		
		public static void main(String[] args) {
	    	try {
	    		InputStream is = args.length > 0 ? new FileInputStream(args[0]) : System.in;
	    		OutputStream os = args.length > 1 ? new FileOutputStream(args[1]) : System.out;
			
	    		xjson2json(is,new OutputStreamWriter(os));
	    	} catch(Exception e) {
	    		e.printStackTrace(System.err);
	    	}
		}
	}

	/*
	 *  Just some utlility functions (err methods sorry ;))
	 */
	public static void serialize(Node node, OutputStream os) throws TransformerException {
		DOMSource source  = new DOMSource(node);
		StreamResult result = new StreamResult(os);
		transform(source,result);
	}

	public static void serialize(Node node, StringWriter sw) throws TransformerException {
		DOMSource source  = new DOMSource(node);
		StreamResult result = new StreamResult(sw);
		transform(source,result);		
	}
	
	private static void transform(DOMSource source, StreamResult result) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(source, result);
	}
	
	public static Document loadDocument(InputStream is) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory domBuilderFactory = DocumentBuilderFactory.newInstance();
		domBuilderFactory.setNamespaceAware(true);
		DocumentBuilder domBuilder = domBuilderFactory.newDocumentBuilder();
		return domBuilder.parse(is);
	}
}
