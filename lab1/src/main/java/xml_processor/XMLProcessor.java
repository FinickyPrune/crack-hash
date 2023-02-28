package xml_processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.MapUtil;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import java.util.*;

public class XMLProcessor {

    private static final Logger log = LoggerFactory.getLogger(XMLProcessor.class);

    public static void process(XMLEventReader reader) throws XMLStreamException {
        Map<String, Integer> editors = new HashMap<>();
        Map<String, Integer> uniqueTags = new HashMap<>();

        log.info("Started file processing...");

        parse(reader, editors, uniqueTags);
        saveData(editors, uniqueTags);

        log.info("Ended file processing.");
    }

    private static void parse(XMLEventReader reader,
                              Map<String, Integer> editors,
                              Map<String, Integer> uniqueTags)  throws XMLStreamException {
        while (reader.hasNext()) {
            var event = reader.nextEvent();
            if (event.isStartElement()) {
                var startElement = event.asStartElement();
                var localPart = startElement.getName().getLocalPart();
                if (localPart.equals(XMLKey.NODE.rawValue)) parseNode(startElement, editors);
                if (localPart.equals(XMLKey.TAG.rawValue)) parseTag(startElement, uniqueTags);
            }
        }
    }

    private static void parseTag(StartElement startElement, Map<String, Integer> uniqueTags) {
        var key = startElement.getAttributeByName(new QName(XMLKey.KEY_PART.rawValue));
        var value = startElement.getAttributeByName(new QName(XMLKey.VALUE_PART.rawValue));
        if (key != null && key.getValue().equals(XMLKey.NAME.rawValue) && value != null) {
            uniqueTags.merge(value.getValue(), 1, Integer::sum);
        }
    }

    private static void parseNode(StartElement startElement, Map<String, Integer> editors) {
        var userAttr = startElement.getAttributeByName(new QName(XMLKey.USER.rawValue));
        if (userAttr != null) editors.merge(userAttr.getValue(), 1, Integer::sum);
    }

    private static void saveData(Map<String, Integer> editors, Map<String, Integer> tags) {
        MapUtil.saveToFile(MapUtil.sortByValue(editors, false), "editors.txt");
        MapUtil.saveToFile(MapUtil.sortByKey(tags, true), "tags.txt");
    }

}
