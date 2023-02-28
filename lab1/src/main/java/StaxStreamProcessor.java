import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.InputStreamReader;

public class StaxStreamProcessor implements AutoCloseable {

    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();
    private final XMLEventReader reader;

    public StaxStreamProcessor(InputStreamReader is) throws XMLStreamException {
        reader = FACTORY.createXMLEventReader(is);
    }

    public XMLEventReader getReader() {
        return reader;
    }

    @Override
    public void close() throws Exception {
        if (reader == null) return;
        reader.close();
    }
}