package xml_processor;

public enum XMLKey {

    NODE("node"),
    TAG("tag"),
    USER("user"),
    KEY_PART("k"),
    VALUE_PART("v"),
    NAME("name");

    final String rawValue;

    XMLKey(String rawValue) {
        this.rawValue = rawValue;
    }
}
