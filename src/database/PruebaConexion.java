/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 *
 * @author JRonald
 */
public class PruebaConexion {
    public static void main(String[] args) {
        //Conexion conn = new Conexion(); /*Antes del singleton*/
        Conexion conn=Conexion.getInstancia();
        conn.conectar();
        if (conn.cadena!=null) {
            //System.out.println("Conectado");
            System.out.println("Conectado a "+conn.getClass().getNestHost());
        }else{
            System.out.println("Desconectado");
        }
    }
    
}
