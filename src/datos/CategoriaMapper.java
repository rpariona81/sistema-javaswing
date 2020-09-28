/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import datos.interfaces.RowMapper;
import entidades.Categoria;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author JRonald
 */
public class CategoriaMapper implements RowMapper<Categoria>{

    @Override
    public Categoria mapRow(ResultSet rs) throws SQLException {
        Categoria bean = new Categoria();
        bean.setId(rs.getInt("id"));
        bean.setNombre(rs.getString("nombre"));
        bean.setDescripcion(rs.getString("descripcion"));
        bean.setActivo(rs.getBoolean("activo"));
        return bean;
    }
    
}
