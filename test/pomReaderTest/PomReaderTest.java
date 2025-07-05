package test.pomReaderTest;

import loader.utilities.UrlAccessor;
import loader.utilities.linkGenerator.LinkGenerator;
import loader.utilities.pomReader.handlers.XMLHandler;
import loader.utilities.pomReader.handlers.XMLHandlerFactory;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.project.Project;
import loader.utilities.version.versionHandler.VersionHandler;
import loader.utilities.version.versionHandler.VersionParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

public class PomReaderTest {

    public static XMLHandler read() {
        try {
            UrlAccessor.init();
            UrlAccessor urlAccessor = UrlAccessor.getInstance();
            LinkGenerator.init();
            LinkGenerator linkGenerator = LinkGenerator.getInstance();
            VersionHandler.init(urlAccessor, linkGenerator);
            VersionHandler versionHandler = VersionHandler.getInstance();
            VersionParser.init(versionHandler);
            VersionParser versionParser = VersionParser.getInstance();

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();

            XMLHandlerFactory.init(versionParser);
            XMLHandlerFactory xmlHandlerFactory = XMLHandlerFactory.getInstance();

            XMLHandler xmlHandler = xmlHandlerFactory.create();

            saxParser.parse("./test/pomReaderTest/pom.xml", xmlHandler);

            return xmlHandler;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(read().getXmlParsed().<Project>getAs());
    }
}
