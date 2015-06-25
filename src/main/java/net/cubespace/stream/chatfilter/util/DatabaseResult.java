package net.cubespace.stream.chatfilter.util;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabian on 25.06.15.
 */
public class DatabaseResult {
    @Getter private List<DatabaseRow> rows = new ArrayList<>();

    public void addRow( DatabaseRow row ) {
        rows.add( row );
    }
}
