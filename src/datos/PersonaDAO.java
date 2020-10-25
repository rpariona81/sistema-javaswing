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
import entidades.Persona;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author JRonald
 */
public class PersonaDAO implements CrudPaginadoInterface<Persona> {

    private final Conexion CONN;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public PersonaDAO() {
        CONN = Conexion.getInstancia();
    }

    @Override
    public List<Persona> listar(String texto, int totalPorPagina, int numPagina) {
        List<Persona> registros = new ArrayList();
        try {
            /*SQL Server 2019 y DB2*/
            ps = CONN.conectar().prepareStatement("SELECT p.id, p.tipo_persona,"
                    + " p.nombre, p.tipo_documento, p.num_documento, p.direccion, p.telefono, p.email, p.activo "
                    + " FROM persona p WHERE p.nombre LIKE ?"
                    + " ORDER BY p.id ASC "
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
                registros.add(new Persona(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getBoolean(9)));
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

    public List<Persona> listarTipo(String texto, int totalPorPagina, int numPagina, String tipoPersona) {
        List<Persona> registros = new ArrayList();
        try {
            /*SQL Server 2019 y DB2*/
            ps = CONN.conectar().prepareStatement("SELECT p.id, p.tipo_persona,"
                    + " p.nombre, p.tipo_documento, p.num_documento, p.direccion, p.telefono, p.email, p.activo "
                    + " FROM persona p WHERE p.nombre LIKE ? AND tipo_persona = ?"
                    + " ORDER BY p.id ASC "
                    + " OFFSET ? ROWS"
                    + " FETCH NEXT ? ROWS ONLY",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, tipoPersona);
            ps.setInt(3, (numPagina - 1) * totalPorPagina);
            ps.setInt(4, totalPorPagina);
            rs = ps.executeQuery();
            //rs.last();
            //if (rs.getRow() > 0) {
            //    CategoriaMapper mapper = new CategoriaMapper();
            while (rs.next()) {
                /*Categoria bean = mapper.mapRow(rs);
                    registros.add(bean);*/
                registros.add(new Persona(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getBoolean(9)));
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
    public boolean insertar(Persona obj) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("INSERT INTO persona(tipo_persona, nombre, tipo_documento, num_documento, direccion, telefono, email, activo) VALUES(?,?,?,?,?,?,?,1)");
            //ResultSet.TYPE_FORWARD_ONLY);
            System.out.println("Inicia inserción...");
            ps.setString(1, obj.getTipoPersona());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getTipoDocumento());
            ps.setString(4, obj.getNumDocumento());
            ps.setString(5, obj.getDireccion());
            ps.setString(6, obj.getTelefono());
            ps.setString(7, obj.getEmail());
            System.out.println("Insertando...");
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
    public boolean actualizar(Persona obj) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("UPDATE persona SET tipo_persona=?, nombre=?, tipo_documento=?, num_documento=?, direccion=?, telefono=?, email=? WHERE id=?");
            ps.setString(1, obj.getTipoPersona());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getTipoDocumento());
            ps.setString(4, obj.getNumDocumento());
            ps.setString(5, obj.getDireccion());
            ps.setString(6, obj.getTelefono());
            ps.setString(7, obj.getEmail());
            ps.setInt(8, obj.getId());
            System.out.println("Actualizacion en curso...");
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
            System.out.println("Actualizacion finalizada.");
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
            ps = CONN.conectar().prepareStatement("UPDATE persona SET activo=0 WHERE id=?");
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
            ps = CONN.conectar().prepareStatement("UPDATE persona SET activo=1 WHERE id=?");
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
            ps = CONN.conectar().prepareStatement("SELECT COUNT(id) as CONTADOR FROM persona");
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
            ps = CONN.conectar().prepareStatement("SELECT nombre FROM persona WHERE nombre = ?",
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
