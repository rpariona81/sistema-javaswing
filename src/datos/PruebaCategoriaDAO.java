/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import entidades.Categoria;
import java.sql.SQLException;

/**
 *
 * @author JRonald
 */
public class PruebaCategoriaDAO {
    public static void main(String[] args) throws SQLException{
        CategoriaDAO TEST = new CategoriaDAO();
        TEST.listar("").toString();
        Categoria obj=new Categoria();
        obj.setNombre("Abarrotes");
        obj.setDescripcion("Abarrotes en general: azúcar, arroz, aceite, etc.");
        TEST.insertar(obj);
    }
}
