package net.cubespace.stream.chatfilter.listener;

import net.cubespace.stream.chatfilter.Chatfilter;
import net.cubespace.stream.chatfilter.util.DatabaseResult;
import net.cubespace.stream.chatfilter.util.DatabaseRow;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Fabian on 25.06.15.
 */
public class ChatListener implements Listener {
    private Map<String, String> replacements = new HashMap<String, String>(){{
        put( "3", "e" );
        put( "4", "a" );
        put( "2", "z" );
        put( "0", "o" );
        put( "7", "t" );
        put( "1", "i" );
        put( " ", "" );
        put( "  ", "" );
    }};

    private ReentrantLock lock = new ReentrantLock( true );
    private Set<String> forbiddenWords = new HashSet<>();

    public ChatListener() {
        Chatfilter.getInstance().getProxy().getScheduler().schedule( Chatfilter.getInstance(), new Runnable() {
            @Override
            public void run() {
                DatabaseResult dbResult = Chatfilter.getInstance().getMySQL().select( "SELECT `word` FROM `word_blacklist`;" );
                if ( dbResult != null && dbResult.getRows().size() > 0 ) {
                    lock.lock();

                    try {
                        forbiddenWords.clear();
                        for ( DatabaseRow databaseRow : dbResult.getRows() ) {
                            forbiddenWords.add( (String) databaseRow.get( "word" ) );
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }, 0, 30, TimeUnit.SECONDS );
    }

    @EventHandler
    public void onChat( ChatEvent event ) {
        // Check for bypass
        if ( event.getSender() instanceof ProxiedPlayer ) {
            ProxiedPlayer senderPlayer = (ProxiedPlayer) event.getSender();
            if ( senderPlayer.hasPermission( "stream.chatfilter.bypass" ) ) {
                return;
            }
        }

        String chatCopy = event.getMessage();

        // Normalize Chatmessage
        for ( Map.Entry<String, String> stringStringEntry : replacements.entrySet() ) {
            chatCopy = chatCopy.replaceAll( stringStringEntry.getKey(), stringStringEntry.getValue() );
        }

        // Check if normalize did change the whole message
        boolean valid = false;
        for ( int i = 0; i < chatCopy.length(); i++ ) {
            if ( chatCopy.charAt( i ) == event.getMessage().charAt( i ) ) {
                valid = true;
                break;
            }
        }

        // 32e -> eze; 32 -> ez
        if ( !valid ) {
           return;
        }

        // toLower the chatmessage
        chatCopy = chatCopy.toLowerCase();

        // Check against blacklist
        lock.lock();

        try {
            for ( String forbiddenWord : forbiddenWords ) {
                if ( chatCopy.contains( forbiddenWord ) ) {
                    event.setCancelled( true );

                    if ( event.getSender() instanceof ProxiedPlayer ) {
                        ProxiedPlayer senderPlayer = (ProxiedPlayer) event.getSender();
                        senderPlayer.sendMessage( "Diese Nachricht enthält verbotene Inhalte und wurde deswegen nicht versendet" );
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
