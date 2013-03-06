package com.semsaas.jsonxml.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.semsaas.jsonxml.examples.Examples;

public class Tests {
	
	@Test
	public void test() {
		InputStream is = ClassLoader.getSystemResourceAsStream("com/semsaas/jsonxml/tests/test001.json");
		try {
			// The real test
			Node node = Examples.Json2Dom.json2dom(is);
			
			// Check the results
			StringWriter result = new StringWriter();
			Examples.serialize(node, result);
			
			StringWriter expected = new StringWriter();
			InputStream xmlIs = ClassLoader.getSystemResourceAsStream("com/semsaas/jsonxml/tests/test001.xml");
			IOUtils.copy(xmlIs, expected);
			
			assertEquals(expected.toString(),result.toString());
			
		} catch (SAXException e) {
			fail(e.getMessage());
		} catch (TransformerException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}
