package net.cubespace.stream.chatfilter.util;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabian on 25.06.15.
 */
public class MySQL {
    private final String host;
    private final String user;
    private final String password;
    private final String database;
    private final int poolSize;

    private BasicDataSource mysqlConnection;

    public MySQL(String host, String user, String password, String database, int poolSize) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
        this.poolSize = poolSize;
    }

    public DatabaseResult select( String query, Object ... params ) {
        Connection connection = null;

        try {
            connection = mysqlConnection.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement( query );

            // Set the objects for the Query
            int count = 1;
            for ( Object param : params ) {
                preparedStatement.setObject( count, param );
                count++;
            }

            // Execute the query and return the resultset
            if ( preparedStatement.execute() ) {
                ResultSet resultSet = preparedStatement.getResultSet();
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                List<String> fieldNames = new ArrayList<>();
                for ( int i = 0; i < resultSetMetaData.getColumnCount(); i++ ) {
                    fieldNames.add( resultSetMetaData.getColumnName( i + 1 ) );
                }

                DatabaseResult databaseResult = new DatabaseResult();
                while ( resultSet.next() ) {
                    DatabaseRow row = new DatabaseRow();
                    for ( String fieldName : fieldNames ) {
                        row.addField( fieldName, resultSet.getObject( fieldName ) );
                    }

                    databaseResult.addRow( row );
                }

                resultSet.close();
                return databaseResult;
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        } finally {
            if ( connection != null ) {
                try {
                    connection.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public boolean setup() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( MySQL.class.getClassLoader() );

        mysqlConnection = new BasicDataSource();

        // Init basic Driver settings
        mysqlConnection.setDriverClassName( "com.mysql.jdbc.Driver" );
        mysqlConnection.setUrl( "jdbc:mysql://" + host + ":3306/" + database );
        mysqlConnection.setUsername( user );
        mysqlConnection.setPassword( password );

        // Setup connection pooling
        mysqlConnection.setMaxIdle( poolSize );
        mysqlConnection.setMinIdle( 1 );
        mysqlConnection.setDriverClassLoader( MySQL.class.getClassLoader() );

        try {
            Connection connection = mysqlConnection.getConnection();
            connection.close();
            return true;
        } catch ( SQLException e ) {
            e.printStackTrace();
            return false;
        } finally {
            Thread.currentThread().setContextClassLoader( classLoader );
        }
    }
}
