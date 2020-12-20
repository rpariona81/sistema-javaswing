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
import entidades.Articulo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author JRonald
 */
public class ArticuloDAO implements CrudPaginadoInterface<Articulo> {

    private final Conexion CONN;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public ArticuloDAO() {
        CONN = Conexion.getInstancia();
    }

    @Override
    public List<Articulo> listar(String texto, int totalPorPagina, int numPagina) {
        List<Articulo> registros = new ArrayList();
        try {
            /*SQL Server 2019 y DB2*/
            ps = CONN.conectar().prepareStatement("SELECT a.id, a.categoria_id, c.nombre as categoria_nombre,"
                    + " a.codigo, a.nombre, a.precio_venta, a.stock, a.descripcion, a.imagen, a.activo"
                    + " FROM articulo a INNER JOIN categoria c ON a.categoria_id=c.id WHERE a.nombre LIKE ?"
                    + " ORDER BY a.id ASC "
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
                registros.add(new Articulo(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getDouble(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getBoolean(10)));
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
    
    public List<Articulo> listarArticuloVenta(String texto, int totalPorPagina, int numPagina) {
        List<Articulo> registros = new ArrayList();
        try {
            /*SQL Server 2019 y DB2*/
            ps = CONN.conectar().prepareStatement("SELECT a.id, a.categoria_id, c.nombre as categoria_nombre,"
                    + " a.codigo, a.nombre, a.precio_venta, a.stock, a.descripcion, a.imagen, a.activo"
                    + " FROM articulo a INNER JOIN categoria c ON a.categoria_id=c.id WHERE a.nombre LIKE ?"
                    + " AND a.stock > 0 AND a.activo = 1"
                    + " ORDER BY a.id ASC "
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
                registros.add(new Articulo(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getDouble(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getBoolean(10)));
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

    public Articulo obtenerArticuloCodigoIngreso(String codigo){
        Articulo art = new Articulo();
        try {
            /*SQL Server 2019 y DB2*/
            ps = CONN.conectar().prepareStatement("SELECT a.id, "
                    + " a.codigo, a.nombre, a.precio_venta, a.stock"
                    + " FROM articulo a WHERE a.codigo = ?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, codigo);
            rs = ps.executeQuery();
            
            if (rs.first()) {
                art = new Articulo(rs.getInt(1), rs.getString(2),rs.getString(3),rs.getDouble(4),rs.getInt(5));
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        } finally {
            ps = null;
            rs = null;
            CONN.desconectar();
        }
        return art;
    }
    
    public Articulo obtenerArticuloCodigoVenta(String codigo){
        Articulo art = new Articulo();
        try {
            /*SQL Server 2019 y DB2*/
            ps = CONN.conectar().prepareStatement("SELECT a.id, "
                    + " a.codigo, a.nombre, a.precio_venta, a.stock"
                    + " FROM articulo a WHERE a.codigo = ? AND a.stock > 0",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, codigo);
            rs = ps.executeQuery();
            
            if (rs.first()) {
                art = new Articulo(rs.getInt(1), rs.getString(2),rs.getString(3),rs.getDouble(4),rs.getInt(5));
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        } finally {
            ps = null;
            rs = null;
            CONN.desconectar();
        }
        return art;
    }
    
    @Override
    public boolean insertar(Articulo obj) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("INSERT INTO articulo(categoria_id, codigo, nombre, precio_venta, stock, descripcion, imagen, activo) VALUES(?,?,?,?,?,?,?,1)");
            //ResultSet.TYPE_FORWARD_ONLY);
            ps.setInt(1, obj.getCategoriaId());
            ps.setString(2, obj.getCodigo());
            ps.setString(3, obj.getNombre());
            ps.setDouble(4, obj.getPrecioVenta());
            ps.setInt(5, obj.getStock());
            ps.setString(6, obj.getDescripcion());
            ps.setString(7, obj.getImagen());
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
    public boolean actualizar(Articulo obj) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("UPDATE articulo SET categoria_id=?, codigo=?, nombre=?, precio_venta=?, stock=?, descripcion=?, imagen=? WHERE id=?");
            ps.setInt(1, obj.getCategoriaId());
            ps.setString(2, obj.getCodigo());
            ps.setString(3, obj.getNombre());
            ps.setDouble(4, obj.getPrecioVenta());
            ps.setInt(5, obj.getStock());
            ps.setString(6, obj.getDescripcion());
            ps.setString(7, obj.getImagen());
            ps.setInt(8, obj.getId());
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
            ps = CONN.conectar().prepareStatement("UPDATE articulo SET activo=0 WHERE id=?");
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
            ps = CONN.conectar().prepareStatement("UPDATE articulo SET activo=1 WHERE id=?");
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
            ps = CONN.conectar().prepareStatement("SELECT COUNT(id) as CONTADOR FROM articulo");
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
            ps = CONN.conectar().prepareStatement("SELECT nombre FROM articulo WHERE nombre = ?",
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
