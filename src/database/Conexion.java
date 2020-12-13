/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author JRonald
 */
public final class Conexion {

    //SQL SERVER 2019
    private static String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static String URL = "jdbc:sqlserver://localhost:1263;databaseName=";
    private static String DATABASE = "BLUDB";
    private static String USER = "sa";
    private static String PASSWORD = "JPariona2020";
    
    //DB2
    /*private static String DRIVER = "com.ibm.db2.jcc.DB2Driver";
    private static String URL = "jdbc:db2://dashdb-txn-sbox-yp-dal09-14.services.dal.bluemix.net:50000/";
    private static String DATABASE = "BLUDB";
    private static String USER = "grd97511";
    private static String PASSWORD = "lqt8nczsc+dhp4p7";
    */
    //MYSQL
    /*private final String DRIVER="com.mysql.jdbc.Driver";
    private final String URL="jdbc:mysql://localhost:3306/";
    private final String DATABASE="dbsistema";
    private final String USER="root";
    private final String PASSWORD="";
     */
    //MARIADB
    /*private final String DRIVER="org.mariadb.jdbc.Driver";
    private final String URL="jdbc:mariadb://localhost:3306/";
    private final String DATABASE="dbsistema";
    private final String USER="root";
    private final String PASSWORD="jpariona2015";
     */
    public Connection cadena;
    public static Conexion instancia;

    private Conexion() {
        this.cadena = null;
    }

    public Connection conectar() {
        try {
            Class.forName(DRIVER);
            this.cadena = DriverManager.getConnection(URL + DATABASE, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error en conexion: " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(0);
        }
        return this.cadena;
    }

    public void desconectar() {
        try {
            this.cadena.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    //Agregamos la opcion sincronizado para ordenar las transacciones
    public synchronized static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }
}
