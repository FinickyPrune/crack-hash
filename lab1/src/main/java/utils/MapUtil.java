package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.util.*;
import java.util.Map.Entry;

public class MapUtil {

    private static final Logger log = LoggerFactory.getLogger(MapUtil.class);

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map,
                                                                             Boolean ascending) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        if (!ascending) Collections.reverse(list);
        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByKey(Map<K, V> map,
                                                                           Boolean ascending) {
        return new TreeMap<>(map);
    }

    public static <K, V extends Comparable<? super V>> void saveToFile(Map<K, V> map,
                                                                       String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            for (K key : map.keySet()) writer.write(key + " - " + map.get(key) + "\n");
            writer.close();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
