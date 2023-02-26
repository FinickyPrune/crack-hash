import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Br2Parser implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(Br2Parser.class);
    private InputStreamReader reader;

    public void parse(String fileName) {
        var inputStream = Main.class.getClassLoader().getResourceAsStream(fileName);
        try {

            reader = Br2Reader.getBufferedReaderForInputStream(inputStream);
            var xmlReader = new StaxStreamProcessor(reader).getReader();
            XMLProcessor.process(xmlReader);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        if (reader == null) return;
        reader.close();
    }
}
