/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student.Connecting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {
    private static final String username = "sa";
    private static final String password = "123";
    private static final String database = "sn200255";
    private static final int port = 1433;
    private static final String serverName = "DESKTOP-3UGPUUA";
    
    //jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
    private static final String connectionString="jdbc:sqlserver://"
            + serverName + ":" + port + ";"
            + "databaseName=" + database + ";"
            +"user=" + username 
            + ";password=" + password;
     //       + ";encrypt=false;trustServerCertificate=true;";
    
    private Connection connection;  
    
    private DB(){
        try {
            connection=DriverManager.getConnection(connectionString);
        } catch (SQLException ex) {
            connection = null;
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Connection getConnection() {
        return connection;
    }
     
    private static DB db=null;
    
    public static DB getInstance()
    {
        if(db == null)
            db = new DB();
        return db;
    } 
}

