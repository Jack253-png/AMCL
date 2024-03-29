package com.mcreater.amcl.util.parsers;

import com.google.gson.internal.LinkedTreeMap;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class ForgeVersionXMLHandler {
    public static Map<String, Vector<String>> result;
    public static Map<String, Vector<String>> load(String s) throws ParserConfigurationException, SAXException, IOException {
        result = new LinkedTreeMap<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parse = factory.newSAXParser();
        XMLReader reader=parse.getXMLReader();
        reader.setContentHandler(new PHandler());
        reader.parse(new InputSource(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8))));
        return result;
    }
    static class PHandler extends DefaultHandler {
        public String re;
        public void startDocument() {}
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            re = qName;
        }
        public void characters(char[] ch, int start, int length) {
            String contents = new String(ch, start, length).trim();
            if (contents.length() > 0) {
                if (Objects.equals(re, "version")) {
                    String mv = Arrays.asList(contents.split("-")).get(0).replace("_", "-");
                    String fv = Arrays.asList(contents.split("-")).get(1);
                    String o;
                    try{
                        o = Arrays.asList(contents.split("-")).get(2);
                        fv += "-"+o;
                    }
                    catch (Exception ignored){
                    }
                    ForgeVersionXMLHandler.result.computeIfAbsent(mv, k -> new Vector<>());
                    ForgeVersionXMLHandler.result.get(mv).add(fv);
                }
            }
        }
        public void endElement(String uri, String localName, String qName) {}
        public void endDocument() {}
    }
}
