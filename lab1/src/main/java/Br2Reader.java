import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.*;

public class Br2Reader {

    public static InputStreamReader getBufferedReaderForInputStream(InputStream inputStream) throws FileNotFoundException, CompressorException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
        return new InputStreamReader(input);
    }
}
