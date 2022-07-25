package com.mcreater.amcl.util.xml;

import com.mcreater.amcl.nativeInterface.ResourceGetter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Vector;

public class DepenciesXMLHandler {
    public static Vector<DepencyItem> load() throws ParserConfigurationException, SAXException, IOException {
        InputStream is = new ResourceGetter().get("assets/depencies.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parse = factory.newSAXParser();
        XMLReader reader=parse.getXMLReader();
        PHandler handler = new PHandler();
        reader.setContentHandler(handler);
        reader.parse(new InputSource(is));
        return handler.depencies;
    }
    static class PHandler extends DefaultHandler {
        String maven;
        Vector<DepencyItem> depencies = new Vector<>();
        public void startDocument() {}
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (Objects.equals(qName, "maven")){
                maven = "";
            }
        }
        public void characters(char[] ch, int start, int length) {
            String contents = new String(ch, start, length).trim();
            if (Objects.equals(maven, "")){
                maven = contents;
            }
            else {
                if (!contents.equals("")) {
                    depencies.add(new DepencyItem(contents, this.maven));
                }
            }
        }
        public void endElement(String uri, String localName, String qName) {}
        public void endDocument() {}
    }
}
