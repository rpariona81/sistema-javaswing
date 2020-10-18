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
import datos.interfaces.CrudPaginadoInterface;
import entidades.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author JRonald
 */
public class UsuarioDAO implements CrudPaginadoInterface<Usuario> {

    private final Conexion CONN;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public UsuarioDAO() {
        CONN = Conexion.getInstancia();
    }

    @Override
    public List<Usuario> listar(String texto, int totalPorPagina, int numPagina) {
        List<Usuario> registros = new ArrayList();
        try {
            /*SQL Server 2019 y DB2*/
            ps = CONN.conectar().prepareStatement("SELECT u.id, u.rol_id, r.nombre as rol_nombre,"
                    + " u.nombre, u.tipo_documento, u.num_documento, u.direccion, u.telefono, u.email, u.clave, u.activo "
                    + " FROM usuario u INNER JOIN rol r ON u.rol_id=r.id WHERE u.nombre LIKE ?"
                    + " ORDER BY u.id ASC "
                    + " OFFSET ? ROWS"
                    + " FETCH NEXT ? ROWS ONLY",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, "%" + texto + "%");
            ps.setInt(2, (numPagina - 1) * totalPorPagina);
            ps.setInt(3, totalPorPagina);
            rs = ps.executeQuery();
            //rs.last();
            //if (rs.getRow() > 0) {
            //    CategoriaMapper mapper = new CategoriaMapper();
            while (rs.next()) {
                /*Categoria bean = mapper.mapRow(rs);
                    registros.add(bean);*/
                registros.add(new Usuario(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getBoolean(11)));
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
    public boolean insertar(Usuario obj) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("INSERT INTO usuario(rol_id, nombre, tipo_documento, num_documento, direccion, telefono, email, clave, activo) VALUES(?,?,?,?,?,?,?,?,1)");
            //ResultSet.TYPE_FORWARD_ONLY);
            ps.setInt(1, obj.getRolId());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getTipo_documento());
            ps.setString(4, obj.getNum_documento());
            ps.setString(5, obj.getDireccion());
            ps.setString(6, obj.getTelefono());
            ps.setString(7, obj.getEmail());
            ps.setString(8, obj.getClave());

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
    public boolean actualizar(Usuario obj) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("UPDATE usuario SET rol_id=?, nombre=?, tipo_documento=?, num_documento=?, direccion=?, telefono=?, email=?, clave=? WHERE id=?");
            ps.setInt(1, obj.getRolId());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getTipo_documento());
            ps.setString(4, obj.getNum_documento());
            ps.setString(5, obj.getDireccion());
            ps.setString(6, obj.getTelefono());
            ps.setString(7, obj.getEmail());

            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.setInt(9, obj.getId());
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
            ps = CONN.conectar().prepareStatement("UPDATE usuario SET activo=0 WHERE id=?");
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
            ps = CONN.conectar().prepareStatement("UPDATE usuario SET activo=1 WHERE id=?");
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
            ps = CONN.conectar().prepareStatement("SELECT COUNT(id) as CONTADOR FROM usuario");
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
            ps = CONN.conectar().prepareStatement("SELECT email FROM usuario WHERE email = ?",
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
