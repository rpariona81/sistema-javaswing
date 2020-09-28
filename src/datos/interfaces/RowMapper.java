/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos.interfaces;

import java.sql.SQLException;
import java.sql.ResultSet;

/**
 *
 * @author JRonald
 * @param <T>
 */
public interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException;
}
