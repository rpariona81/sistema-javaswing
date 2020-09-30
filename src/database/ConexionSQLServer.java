/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.management.RuntimeErrorException;
import javax.swing.JOptionPane;

/**
 *
 * @author JRonald
 */
public final class ConexionSQLServer {

    private static String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static String URL = "jdbc:sqlserver://localhost:1263;databaseName=";
    private static String DATABASE = "BLUDB";
    private static String USER = "sa";
    private static String PASSWORD = "JPariona2020";
    public Connection cadena;
    public static ConexionSQLServer instancia;

    private ConexionSQLServer() {
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
    public synchronized static ConexionSQLServer getInstancia() {
        if (null == instancia) {
            instancia = new ConexionSQLServer();
        }
        return instancia;
    }
}
