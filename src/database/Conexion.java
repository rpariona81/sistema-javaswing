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
public final class Conexion {
    private static String DRIVER="com.ibm.db2.jcc.DB2Driver";
    private static String URL="jdbc:db2://dashdb-txn-sbox-yp-dal09-14.services.dal.bluemix.net:50000/";
    private static String DATABASE="BLUDB";
    private static String USER="grd97511";
    private static String PASSWORD="lqt8nczsc+dhp4p7";
    public Connection cadena;
    public static Conexion instancia;
    
    private Conexion(){
        this.cadena=null;
    }
    
    public Connection conectar(){
        try {
            Class.forName(DRIVER);
            this.cadena = DriverManager.getConnection(URL+DATABASE, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            //e.getMessage();
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(0);
        }
        return this.cadena;
    }
    
    public void desconectar()
    {
        try {
            this.cadena.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    //Agregamos la opcion sincronizado para ordenar las transacciones
    public synchronized static Conexion getInstancia(){
        if (instancia==null) {
            instancia=new Conexion();
        }
        return instancia;
    }
}