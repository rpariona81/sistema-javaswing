/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

import datos.RolDAO;
import datos.UsuarioDAO;
import entidades.Rol;
import entidades.Usuario;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author JRonald
 */
public class UsuarioControl {

    private final UsuarioDAO DATOS;
    private final RolDAO DATOSROL;
    private Usuario obj;
    private DefaultTableModel modeloTabla;
    public int registrosMostrados;

    public UsuarioControl() {
        this.DATOS = new UsuarioDAO();
        this.DATOSROL = new RolDAO();
        this.obj = new Usuario();
        this.registrosMostrados = 0;
    }

    public DefaultTableModel listar(String texto, int totalPorPagina, int numPagina) {
        List<Usuario> lista = new ArrayList();
        lista.addAll(DATOS.listar(texto, totalPorPagina, numPagina));

        String[] titulos = {"id", "Rol ID", "Rol", "Usuario", "Documento", "# Documento", "Dirección", "Teléfono", "Email", "Clave", "Estado"};
        this.modeloTabla = new DefaultTableModel(null, titulos);

        String estado;
        //String[] registro = new String[10];
        Object[] registro = new Object[11];

        this.registrosMostrados = 0;
        for (Usuario item : lista) {
            if (item.isActivo()) {
                estado = "Activo";
            } else {
                estado = "Inactivo";
            }
            //registro[0] = Integer.toString(item.getId());
            registro[0] = item.getId();
            registro[1] = item.getRolId();
            registro[2] = item.getRolNombre();
            registro[3] = item.getNombre();
            registro[4] = item.getTipo_documento();
            registro[5] = item.getNum_documento();
            registro[6] = item.getDireccion();
            registro[7] = item.getTelefono();
            registro[8] = item.getEmail();
            registro[9] = item.getClave();
            registro[10] = estado;
            this.modeloTabla.addRow(registro);
            this.registrosMostrados = this.registrosMostrados + 1;
        }
        return this.modeloTabla;
    }

    public String login(String email, String clave) {
        String resp = "0";
        Usuario usuario = this.DATOS.login(email, encriptar(clave));
        if (usuario != null) {
            if (usuario.isActivo()) {
                Variables.usuarioId = usuario.getId();
                Variables.rolId = usuario.getRolId();
                Variables.rolNombre = usuario.getRolNombre();
                Variables.usuarioNombre = usuario.getNombre();
                Variables.usuarioEmail = usuario.getEmail();
                resp = "1";
            } else {
                resp = "2";
            }
        }
        return resp;
    }

    public DefaultComboBoxModel seleccionar() {
        DefaultComboBoxModel items = new DefaultComboBoxModel();
        List<Rol> listaRol = new ArrayList<>();
        listaRol = DATOSROL.seleccionar();
        for (Rol item : listaRol) {
            items.addElement(new Rol(item.getId(), item.getNombre()));
        }
        return items;
    }

    private static String encriptar(String valor) {
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        byte[] hash = md.digest(valor.getBytes());
        StringBuilder sb = new StringBuilder();

        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();

        //return BCrypt.hashpw(valor, BCrypt.gensalt());
    }

    public String insertar(int rolId, String nombre, String tipoDocumento, String numDocumento, String direccion, String telefono, String email, String clave) {
        String result = "";
        if (DATOS.existe(email)) {
            System.out.println("El registro ya existe");
            //return "El registro ya existe";
            result = "El registro ya existe";
        } else {
            obj.setRolId(rolId);
            obj.setNombre(nombre);
            obj.setTipo_documento(tipoDocumento);
            obj.setNum_documento(numDocumento);
            obj.setDireccion(direccion);
            obj.setTelefono(telefono);
            obj.setEmail(email);
            obj.setClave(this.encriptar(clave));
            if (DATOS.insertar(obj)) {
                return "OK";
            } else {
                return "Error en el registro.";
            }
        }
        /*
        obj.setNombre(nombre);
        obj.setDescripcion(descripcion);
        DATOS.insertar(obj);
        return "OK"; */
        return result;
    }

    public String actualizar(int id, int rolId, String nombre, String tipoDocumento, String numDocumento, String direccion, String telefono, String email, String emailAnt, String clave) {
        if (email.equals(emailAnt)) {
            obj.setId(id);
            obj.setRolId(rolId);
            obj.setNombre(nombre);
            obj.setTipo_documento(tipoDocumento);
            obj.setNum_documento(numDocumento);
            obj.setDireccion(direccion);
            obj.setTelefono(telefono);
            obj.setEmail(email);

            String encriptado;
            if (clave.length() == 64) {
                encriptado = clave;
            } else {
                encriptado = this.encriptar(clave);
            }
            obj.setClave(encriptado);

            if (DATOS.actualizar(obj)) {
                return "OK";
            } else {
                return "Error en la actualización";
            }
        } else {
            if (DATOS.existe(email)) {
                return "El registro ya existe";
            } else {
                obj.setId(id);
                obj.setRolId(rolId);
                obj.setNombre(nombre);
                obj.setTipo_documento(tipoDocumento);
                obj.setNum_documento(numDocumento);
                obj.setDireccion(direccion);
                obj.setTelefono(telefono);
                obj.setEmail(email);

                String encriptado;
                if (clave.length() == 64) {
                    encriptado = clave;
                } else {
                    encriptado = this.encriptar(clave);
                }
                obj.setClave(encriptado);

                if (DATOS.actualizar(obj)) {
                    return "OK";
                } else {
                    return "Error en la actualización";
                }
            }
        }
    }

    public String desactivar(int id) {
        if (DATOS.desactivar(id)) {
            return "OK";
        } else {
            return "No se puede desactivar el registro";
        }
    }

    public String activar(int id) {
        if (DATOS.activar(id)) {
            return "OK";
        } else {
            return "No se puede activar el registro";
        }
    }

    public int total() {
        return DATOS.total();
    }

    public int totalMostrados() {
        return this.registrosMostrados;
    }
}
