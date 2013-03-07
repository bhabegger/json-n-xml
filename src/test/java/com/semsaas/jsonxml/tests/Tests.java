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
	public void test001_json2xjson() {
		json2xjson("com/semsaas/jsonxml/tests/test001.json","com/semsaas/jsonxml/tests/test001.xml");
	}
	
	@Test 
	public void test001_xjson2json() {
		xjson2json("com/semsaas/jsonxml/tests/test001.xml", "com/semsaas/jsonxml/tests/test001.json");
	}
	
	@Test
	public void test002_json2xjson() {
		json2xjson("com/semsaas/jsonxml/tests/test002.json","com/semsaas/jsonxml/tests/test002.xml");
	}
	
	@Test 
	public void test002_xjson2json() {
		xjson2json("com/semsaas/jsonxml/tests/test002.xml", "com/semsaas/jsonxml/tests/test002.json");
	}	
	
	public void json2xjson(String jsonResource, String xmlResource) {
		InputStream is = ClassLoader.getSystemResourceAsStream(jsonResource);
		try {
			// The real test
			Node node = Examples.Json2Dom.json2dom(is);
			
			// Check the results
			StringWriter result = new StringWriter();
			Examples.serialize(node, result);
			
			StringWriter expected = new StringWriter();
			InputStream xmlIs = ClassLoader.getSystemResourceAsStream(xmlResource);
			IOUtils.copy(xmlIs, expected);
			
			assertEquals(expected.toString().replaceAll("[\\n\\s]+",""),result.toString().replaceAll("[\\n\\s]+",""));
			
		} catch (SAXException e) {
			fail(e.getMessage());
		} catch (TransformerException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	public void xjson2json(String xmlResource, String jsonResource) {
		InputStream is = ClassLoader.getSystemResourceAsStream(xmlResource);
		try {
			// The real test
			StringWriter result = new StringWriter();
			Examples.XJson2json.xjson2json(is, result);
			
			// Check the results			
			StringWriter expected = new StringWriter();
			InputStream xmlIs = ClassLoader.getSystemResourceAsStream(jsonResource);
			IOUtils.copy(xmlIs, expected);
			
			assertEquals(expected.toString().replaceAll("[\\n\\s]+", ""),result.toString().replaceAll("[\\n\\s]+", ""));
			
		} catch (SAXException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}
