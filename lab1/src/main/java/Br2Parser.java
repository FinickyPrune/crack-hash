import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xml_processor.XMLProcessor;


public class Br2Parser {

    private static final Logger log = LoggerFactory.getLogger(Br2Parser.class);

    public void parse(String fileName) {
        var inputStream = Main.class.getClassLoader().getResourceAsStream(fileName);
        try {

            var reader = Br2Reader.getBufferedReaderForInputStream(inputStream);
            var xmlReader = new StaxStreamProcessor(reader).getReader();
            XMLProcessor.process(xmlReader);
            reader.close();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
