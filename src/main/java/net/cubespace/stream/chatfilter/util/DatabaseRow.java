package net.cubespace.stream.chatfilter.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fabian on 25.06.15.
 */
public class DatabaseRow {
    private Map<String, Object> fields = new HashMap<>();

    public void addField( String fieldName, Object value ) {
        fields.put( fieldName, value );
    }

    public <T> T get( String fieldName ) {
        return (T) fields.get( fieldName );
    }
}
