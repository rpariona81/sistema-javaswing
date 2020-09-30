/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.Categoria;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author JRonald
 */
public class CategoriaDAO implements CrudSimpleInterface<Categoria> {

    private final Conexion CONN;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public CategoriaDAO() {
        CONN = Conexion.getInstancia();
    }

    @Override
    public List<Categoria> listar(String texto) {
        List<Categoria> registros = new ArrayList();
        try {
            ps = CONN.conectar().prepareStatement("SELECT * FROM categoria WHERE nombre LIKE ?", 
                    ResultSet.TYPE_SCROLL_INSENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            //rs.last();
            //if (rs.getRow() > 0) {
            //    CategoriaMapper mapper = new CategoriaMapper();
            while (rs.next()) {
                /*Categoria bean = mapper.mapRow(rs);
                    registros.add(bean);*/
                registros.add(new Categoria(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBoolean(4)));
            }
            //} else {
            //    JOptionPane.showMessageDialog(null, "No hay registros que mostrar");
            //}
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        } finally {
            ps = null;
            rs = null;
            CONN.desconectar();
        }
        return registros;
    }

    @Override
    public boolean insertar(Categoria obj) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("INSERT INTO categoria(nombre,descripcion,activo) VALUES(?,?,1)"); 
                    //ResultSet.TYPE_FORWARD_ONLY);
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getDescripcion());
            //ps.executeUpdate();
            /*rs=ps.getGeneratedKeys();
            System.err.println("Row:"+rs.getInt(1));
            if (rs.getInt(1) > 0) {*/
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.err.println(e.getMessage());
        } finally {
            ps = null;
            CONN.desconectar();
        }
        return resp;
    }

    @Override
    public boolean actualizar(Categoria obj) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("UPDATE categoria SET nombre=?, descripcion=? WHERE id=?");
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getDescripcion());
            ps.setInt(3, obj.getId());
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            CONN.desconectar();
        }
        return resp;
    }

    @Override
    public boolean desactivar(int id) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("UPDATE categoria SET activo=0 WHERE id=?");
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            CONN.desconectar();
        }
        return resp;
    }

    @Override
    public boolean activar(int id) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("UPDATE categoria SET activo=1 WHERE id=?");
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            CONN.desconectar();
        }
        return resp;
    }

    @Override
    public int total() {
        int totalRegistros = 0;
        try {
            ps = CONN.conectar().prepareStatement("SELECT COUNT(id) as CONTADOR FROM categoria");
            rs = ps.executeQuery();
            while (rs.next()) {
                totalRegistros = rs.getInt("CONTADOR");
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CONN.desconectar();
        }
        return totalRegistros;
    }

    @Override
    public boolean existe(String texto) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("SELECT nombre FROM categoria WHERE nombre = ?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            //ps = CONN.conectar().prepareStatement("SELECT nombre FROM categoria WHERE nombre = ?", ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, texto);
            rs = ps.executeQuery();
            rs.last();
            if (rs.getRow() > 0) {
                resp = true;
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CONN.desconectar();
        }
        return resp;
    }

}
