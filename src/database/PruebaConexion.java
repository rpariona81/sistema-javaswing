/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.ResultSet;
import datos.CategoriaDAO;
import entidades.Categoria;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JRonald
 */
public class PruebaConexion {

    public static void main(String[] args) throws SQLException {
        //Conexion conn = new Conexion(); /*Antes del singleton*/
        Conexion conn = Conexion.getInstancia();
        conn.conectar();
        if (conn.cadena != null) {
            //System.out.println("Conectado");
            System.out.println("Conectado a " + conn.getClass().getNestHost());
        } else {
            System.out.println("Desconectado");
        }
        String cadena = "Abarrotes";
        PreparedStatement pps = conn.conectar().prepareStatement("SELECT nombre FROM categoria WHERE nombre = ?",
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        pps.setString(1, cadena);
        //pps.execute();
        ResultSet res = pps.executeQuery();
        res.last();
        /*while (res.next()) {            
            System.out.println("d:"+res.getString("nombre"));
        }*/
        if (res.getRow() > 0) {
            System.out.println("d:" + res.getString("nombre"));
        }
        /*List<Categoria> records = new ArrayList();
        CategoriaDAO DAO = new CategoriaDAO();
        records.addAll(DAO.listar(""));
        //records.addAll((Collection<? extends Categoria>) TEST.listar(""));
        System.err.println("records: " + records);*/
    }

}
