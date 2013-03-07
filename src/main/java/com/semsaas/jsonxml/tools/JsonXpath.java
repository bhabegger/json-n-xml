package com.semsaas.jsonxml.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.semsaas.jsonxml.XJSON;
import com.semsaas.jsonxml.XMLJsonGenerator;

public class JsonXpath {
	public static void main(String[] args) {
		/*
		 * Process options
		 */
		LinkedList<String> files = new LinkedList<String>();
		LinkedList<String> expr  = new LinkedList<String>();
		boolean help = false;
		String activeOption = null;
		String error = null;
		
		for(int i=0; i<args.length && error == null && !help; i++) {
			if(activeOption != null) {
				if(activeOption.equals("-e")) {
					expr.push(args[i]);
				} else if(activeOption.equals("-h")) {
					help = true;
				} else {
					error = "Unknown option "+activeOption;
				}
				activeOption = null;
			} else {
				if(args[i].startsWith("-")) {
					activeOption = args[i];
				} else {
					files.push(args[i]);
				}
			}
		}

		if(error != null) {
			System.err.println(error);
			showHelp();
		} else if(help) {
			showHelp();
		} else {
			try {
	    		TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    		Transformer transformer = transformerFactory.newTransformer();
	
				for(String f: files) {
					System.out.println("*** "+f+" ***");
					try {
						// Create a JSON XML reader
						XMLReader reader = XMLReaderFactory.createXMLReader("com.semsaas.jsonxml.JsonXMLReader");
				    	
						// Prepare a reader with the JSON file as input
				    	InputStreamReader stringReader = new InputStreamReader(new FileInputStream(f));
						SAXSource saxSource = new SAXSource(reader, new InputSource(stringReader));

						// Prepare a DOMResult which will hold the DOM of the xjson
						DOMResult domResult = new DOMResult();
						
						// Run SAX processing through a transformer
						// (This could be done more simply, but we have here the opportunity to pass our xjson through
						// an XSLT and get a legacy XML output ;) )
						transformer.transform(saxSource, domResult);
						Node dom = domResult.getNode();
						
						XPathFactory xpathFactory = XPathFactory.newInstance();
						for(String x: expr) {
							try {
								XPath xpath = xpathFactory.newXPath();
								xpath.setNamespaceContext(new NamespaceContext() {
									@Override
									public Iterator getPrefixes(String namespaceURI) {
										return null;
									}
									
									@Override
									public String getPrefix(String namespaceURI) {
										return null;
									}
									
									@Override
									public String getNamespaceURI(String prefix) {
										if(prefix==null) {
											return XJSON.XMLNS;
										} else if("j".equals(prefix)) {
											return XJSON.XMLNS;
										} else {
											return null;
										}
									}
								});
								NodeList nl = (NodeList)xpath.evaluate(x, dom, XPathConstants.NODESET);
								System.out.println("-- Found "+nl.getLength()+" nodes for xpath '"+x+"' in file '"+f+"'");
								for(int i=0; i<nl.getLength(); i++) {
									System.out.println(" +(" + i +")+ ");
									XMLJsonGenerator handler = new XMLJsonGenerator();
									StringWriter buffer= new StringWriter();
						    		handler.setOutputWriter(buffer);
	
						    		SAXResult result = new SAXResult(handler);
						    		transformer.transform(new DOMSource(nl.item(i)), result);

						    		System.out.println(buffer.toString());
								}
							} catch (XPathExpressionException e) {
								System.err.println("-- Error evaluating '"+x+"' on file '"+f+"'");
								e.printStackTrace();
							} catch (TransformerException e) {
								System.err.println("-- Error evaluating '"+x+"' on file '"+f+"'");
								e.printStackTrace();
							}
						}
					} catch (FileNotFoundException e) {
						System.err.println("File '"+f+"' was not found");
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (TransformerException e) {
						e.printStackTrace();
					}
				}
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			}
		}
	}

	private static void showHelp() {
		InputStream helpStream = ClassLoader.getSystemResourceAsStream("com/semsaas/jsonxml/tools/Xpath.help");
		try {
			IOUtils.copy(helpStream,System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
