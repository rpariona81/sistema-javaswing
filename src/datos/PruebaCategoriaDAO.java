/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import entidades.Categoria;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JRonald
 */
public class PruebaCategoriaDAO {

    public static void main(String[] args) throws SQLException {
        CategoriaDAO TEST = new CategoriaDAO();
        String result="";
        Categoria obj = new Categoria();
        if(TEST.existe("Polos")) result= "Error";
        System.out.println("result: "+result);
        obj.setNombre("Polos");
        obj.setDescripcion("Abarrotes en general: azúcar, arroz, aceite, etc.");
        TEST.insertar(obj);
        List<Categoria> records = new ArrayList();
        records.addAll(TEST.listar(""));
        System.err.println("records: " + records);
        
    }
}
