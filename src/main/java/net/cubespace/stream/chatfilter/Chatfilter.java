package net.cubespace.stream.chatfilter;

import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.stream.chatfilter.config.MySQLConfig;
import net.cubespace.stream.chatfilter.listener.ChatListener;
import net.cubespace.stream.chatfilter.util.MySQL;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Created by Fabian on 25.06.15.
 */
public class Chatfilter extends Plugin {
    @Getter private static Chatfilter instance;

    @Getter private MySQL mySQL;

    @Override
    public void onEnable() {
        instance = this;

        // Load config
        MySQLConfig mySQLConfig = new MySQLConfig();
        try {
            mySQLConfig.init();
        } catch ( InvalidConfigurationException e ) {
            e.printStackTrace();
        }

        // Startup MySQL
        mySQL = new MySQL( mySQLConfig.getHost(), mySQLConfig.getUser(), mySQLConfig.getPassword(), mySQLConfig.getDatabase(), mySQLConfig.getPoolSize() );
        if ( !mySQL.setup() ) {
            getProxy().stop();
        }

        // Listener
        getProxy().getPluginManager().registerListener( this, new ChatListener() );
    }
}
