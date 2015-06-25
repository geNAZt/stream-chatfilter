package net.cubespace.stream.chatfilter.config;

import lombok.Getter;
import net.cubespace.Yamler.Config.Config;
import net.cubespace.stream.chatfilter.Chatfilter;

import java.io.File;

/**
 * Created by Fabian on 24.06.15.
 */
public class MySQLConfig extends Config {
    @Getter private String host;
    @Getter private String user;
    @Getter private String password;
    @Getter private String database;
    @Getter private int poolSize;

    public MySQLConfig() {
        CONFIG_FILE = new File( Chatfilter.getInstance().getDataFolder(), "config.yml" );
    }
}
