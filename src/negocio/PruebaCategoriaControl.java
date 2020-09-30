/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

import datos.CategoriaDAO;
import entidades.Categoria;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JRonald
 */
public class PruebaCategoriaControl {

    public static void main(String[] args) throws SQLException {
        CategoriaControl TEST = new CategoriaControl();
        TEST.insertar("Ojoxs","Abarrotes en general: azúcar, arroz, aceite, etc.");
        List<Categoria> records = new ArrayList();
        CategoriaDAO DAO = new CategoriaDAO();
        records.addAll(DAO.listar(""));
        //records.addAll((Collection<? extends Categoria>) TEST.listar(""));
        System.err.println("records: " + records);
    }
}
