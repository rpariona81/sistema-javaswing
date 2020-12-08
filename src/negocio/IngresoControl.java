/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

import datos.ArticuloDAO;
import datos.IngresoDAO;
import entidades.Articulo;
import entidades.DetalleIngreso;
import entidades.Ingreso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author JRonald
 */
public class IngresoControl {

    private final IngresoDAO DATOS;
    private final ArticuloDAO DATOSART;
    private Ingreso obj;
    private DefaultTableModel modeloTabla;
    public int registrosMostrados;

    public IngresoControl() {
        this.DATOS = new IngresoDAO();
        this.DATOSART = new ArticuloDAO();
        this.obj = new Ingreso();
        this.registrosMostrados = 0;
    }

    public DefaultTableModel listar(String texto, int totalPorPagina, int numPagina) {
        List<Ingreso> lista = new ArrayList();
        lista.addAll(DATOS.listar(texto, totalPorPagina, numPagina));

        String[] titulos = {"id", "Usuario ID", "Usuario", "Proveedor ID", "Proveedor", "Tipo Comprobante", "Serie", "Número", "Fecha", "Impuesto","Total","Estado"};
        this.modeloTabla = new DefaultTableModel(null, titulos);

        //String[] registro = new String[10];
        Object[] registro = new Object[12];
        
        //Formato de fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        this.registrosMostrados = 0;
        for (Ingreso item : lista) {
            //registro[0] = Integer.toString(item.getId());
            registro[0] = item.getId();
            registro[1] = item.getUsuarioId();
            registro[2] = item.getUsuarioNombre();
            registro[3] = item.getPersonaId();
            registro[4] = item.getPersonaNombre();
            registro[5] = item.getTipoComprobante();
            registro[6] = item.getSerieComprobante();
            registro[7] = item.getNumComprobante();
            registro[8] = sdf.format(item.getFecha());
            registro[9]=item.getImpuesto();
            registro[10]=item.getTotal();
            registro[11] = item.getEstado();
            this.modeloTabla.addRow(registro);
            this.registrosMostrados = this.registrosMostrados + 1;
        }
        return this.modeloTabla;
    }

    public Articulo obteneArticuloCodigoIngreso(String codigo){
        Articulo art = DATOSART.obtenerArticuloCodigoIngreso(codigo);
        return art;
    }
    
    public String insertar(int personaId, 
            String tipoComprobante, 
            String serieComprobante, 
            String numComprobante, 
            double impuesto, 
            double total, 
            DefaultTableModel modeloDetalles) {
        String result = "";
        if (DATOS.existe(serieComprobante,numComprobante)) {
            System.out.println("El registro ya existe");
            //return "El registro ya existe";
            result = "El registro ya existe";
        } else {
            obj.setUsuarioId(Variables.usuarioId);
            obj.setPersonaId(personaId);
            obj.setTipoComprobante(tipoComprobante);
            obj.setSerieComprobante(serieComprobante);
            obj.setNumComprobante(numComprobante);
            obj.setImpuesto(impuesto);
            obj.setTotal(total);
            
            List<DetalleIngreso> detalles = new ArrayList<>();
            int articuloId;
            int cantidad;
            double precio;
            
            for (int i = 0; i < modeloDetalles.getRowCount(); i++) {
                articuloId=Integer.parseInt(String.valueOf(modeloDetalles.getValueAt(i, 0)));
                cantidad=Integer.parseInt(String.valueOf(modeloDetalles.getValueAt(i, 3)));
                precio=Double.parseDouble(String.valueOf(modeloDetalles.getValueAt(i, 4)));
                detalles.add(new DetalleIngreso(articuloId, cantidad, precio));
            }
            
            obj.setDetalles(detalles);
            
            if (DATOS.insertar(obj)) {
                return "OK";
            } else {
                return "Error en el registro.";
            }
        }
        
        return result;
    }

    public String anular(int id) {
        if (DATOS.anular(id)) {
            return "OK";
        } else {
            return "No se puede anular el registro";
        }
    }

    public int total() {
        return DATOS.total();
    }

    public int totalMostrados() {
        return this.registrosMostrados;
    }
}
