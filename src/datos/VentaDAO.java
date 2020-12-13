/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import database.Conexion;
import datos.interfaces.CrudVentaInterface;
import entidades.DetalleVenta;
import entidades.Venta;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author JRonald
 */
public class VentaDAO implements CrudVentaInterface<Venta, DetalleVenta> {

    private final Conexion CONN;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public VentaDAO() {
        this.CONN = Conexion.getInstancia();
    }

    @Override
    public List<Venta> listar(String texto, int totalPorPagina, int numPagina) {
        List<Venta> registros = new ArrayList();
        try {
            /*SQL Server 2019 y DB2*/
            ps = CONN.conectar().prepareStatement("SELECT v.id,"
                    + " v.usuario_id, u.nombre as usuario_nombre, v.persona_id,"
                    + " p.nombre as persona_nombre,"
                    + " v.tipo_comprobante, v.serie_comprobante, v.num_comprobante,"
                    + " v.fecha, v.impuesto, v.total, v.estado"
                    + " FROM venta v INNER JOIN usuario u ON v.usuario_id=u.id"
                    + "	INNER JOIN persona p ON v.persona_id=p.id"
                    + "	WHERE p.nombre LIKE ?"
                    + " ORDER BY v.id ASC"
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
                registros.add(new Venta(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getDate(9), rs.getDouble(10), rs.getDouble(11), rs.getString(12)));
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
        return registros;
    }

    @Override
    public List<DetalleVenta> listarDetalle(int id) {
        List<DetalleVenta> registros=new ArrayList();
        try {
            ps=CONN.conectar().prepareStatement("SELECT a.id,a.codigo,a.nombre,a.stock,d.cantidad,d.precio,d.descuento,((d.cantidad*precio) - d.descuento) as sub_total FROM detalle_venta d INNER JOIN articulo a ON d.articulo_id=a.id WHERE d.venta_id=?");
            ps.setInt(1,id);
            rs=ps.executeQuery();
            while(rs.next()){
                registros.add(new DetalleVenta(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getInt(5),rs.getDouble(6),rs.getDouble(7),rs.getDouble(8)));
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally{
            ps=null;
            rs=null;
            CONN.desconectar();
        }
        return registros;
    }

    @Override
    public boolean insertar(Venta obj) {
        resp = false;
        // Una instancia de conexion para usar transacciones
        Connection connTx = null;
        try {
            connTx = CONN.conectar();
            connTx.setAutoCommit(false);
            //MySQL
            //String sqlInsertVenta="INSERT INTO venta(persona_id,usuario_id,fecha,tipo_comprobante,serie_comprobante,num_comprobante,impuesto,total,estado) VALUES (?,?,now(),?,?,?,?,?,?)";
            //SQL Server
            String sqlInsertVenta = "INSERT INTO venta(persona_id, usuario_id, fecha, tipo_comprobante, serie_comprobante, num_comprobante, impuesto, total, estado) VALUES(?,?,GETDATE(),?,?,?,?,?,?)";
            //DB2
            //String sqlInsertVenta = "INSERT INTO venta(persona_id, usuario_id, fecha, tipo_comprobante, serie_comprobante, num_comprobante, impuesto, total, estado) VALUES(?,?,(SELECT CURRENT TIMESTAMP - 1 DAY - 5 HOUR FROM SYSIBM.SYSDUMMY1),?,?,?,?,?,?)";
            ps = CONN.conectar().prepareStatement(sqlInsertVenta, PreparedStatement.RETURN_GENERATED_KEYS);
            //ResultSet.TYPE_FORWARD_ONLY);
            ps.setInt(1, obj.getPersonaId());
            ps.setInt(2, obj.getUsuarioId());
            ps.setString(3, obj.getTipoComprobante());
            ps.setString(4, obj.getSerieComprobante());
            ps.setString(5, obj.getNumComprobante());
            ps.setDouble(6, obj.getImpuesto());
            ps.setDouble(7, obj.getTotal());
            ps.setString(8, "Aceptado");
            int filasAfectadas = ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            int idGenerado = 0;
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }

            if (filasAfectadas == 1) {
                String sqlInsertDetalle = "INSERT INTO detalle_venta(venta_id,articulo_id,cantidad,precio,descuento) VALUES(?,?,?,?,?)";
                ps = connTx.prepareStatement(sqlInsertDetalle);
                for (DetalleVenta item : obj.getDetalles()) {
                    ps.setInt(1, idGenerado);
                    ps.setInt(2, item.getArticuloId());
                    ps.setInt(3, item.getCantidad());
                    ps.setDouble(4, item.getPrecio());
                    ps.setDouble(5, item.getDescuento());
                    resp = ps.executeUpdate() > 0;
                }
                connTx.commit();
            } else {
                connTx.rollback();
            }
        } catch (SQLException e) {
            try {
                if (connTx != null) {
                    connTx.rollback();
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            } catch (SQLException ex) {
                Logger.getLogger(VentaDAO.class.getName()).log(Level.SEVERE, null, ex.getMessage());
            }
            System.err.println(e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (connTx != null) {
                    connTx.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(VentaDAO.class.getName()).log(Level.SEVERE, null, ex.getMessage());
            }

        }
        return resp;
    }

    @Override
    public boolean anular(int id) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("UPDATE venta SET estado='Anulado' WHERE id=?");
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
            ps = CONN.conectar().prepareStatement("SELECT COUNT(id) as CONTADOR FROM venta");
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
    public boolean existe(String texto1, String texto2) {
        resp = false;
        try {
            ps = CONN.conectar().prepareStatement("SELECT id FROM venta WHERE serie_comprobante = ? AND num_comprobante=?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            //ps = CONN.conectar().prepareStatement("SELECT nombre FROM categoria WHERE nombre = ?", ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, texto1);
            ps.setString(2, texto2);
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
