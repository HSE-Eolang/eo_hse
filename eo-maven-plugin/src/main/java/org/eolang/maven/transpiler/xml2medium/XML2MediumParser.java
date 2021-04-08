package org.eolang.maven.transpiler.xml2medium;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.eolang.maven.transpiler.mediumcodemodel.EOAbstraction;
import org.eolang.maven.transpiler.mediumcodemodel.EOSourceEntity;
import org.eolang.maven.transpiler.mediumcodemodel.EOSourceFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class XML2MediumParser {

    private final File file;
    private XPath xPath;
    private Document doc;

    public XML2MediumParser(File file) {
        this.file = file;
    }

    public EOSourceEntity parse() throws XML2MediumParserException {
        loadXmlDocument();

        EOSourceFile sourceFile = FileMetadataParsingUtils.parseSourceFile(this.file, this.doc, this.xPath);

        ArrayList<EOAbstraction> objects = ObjectsParsingUtils.parseObjects(this.file, this.doc, this.xPath, sourceFile);
        sourceFile.addObjects(objects.toArray(EOAbstraction[]::new));
        return sourceFile;
    }

    private void loadXmlDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            this.doc = dBuilder.parse(file);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.xPath = XPathFactory.newInstance().newXPath();
    }

    public static class XML2MediumParserException extends Exception {

        public XML2MediumParserException(String message) {
            super(message);
        }
    }
}
