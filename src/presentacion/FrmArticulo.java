/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentacion;

import entidades.Categoria;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import negocio.ArticuloControl;

/**
 *
 * @author JRonald
 */
public class FrmArticulo extends javax.swing.JInternalFrame {

    private final ArticuloControl CONTROL;
    private String accion;
    private String nombreAnt;

    private String rutaOrigen;
    private String rutaDestino;
    private final String DIRECTORIO = "src/files/articulos/";
    private String imagen = "";
    private String imagenAnt;

    private int totalPorPagina = 10;
    private int numPagina = 10;
    private boolean primeraCarga = true;
    private int totalRegistros;

    /**
     * Creates new form FrmArticulo
     */
    public FrmArticulo() {
        initComponents();
        this.CONTROL = new ArticuloControl();
        this.paginar();
        this.listar("", false);
        this.primeraCarga = false;
        tabGeneral.setEnabledAt(1, false);
        this.accion = "guardar";
        this.txtId.setVisible(false);
        this.cargarCategorias();
    }

    private void ocultarColumnas() {
        tablaListado.getColumnModel().getColumn(1).setMaxWidth(0);
        tablaListado.getColumnModel().getColumn(1).setMinWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(1).setMinWidth(0);
    }

    private void paginar() {
        int totalPaginas;

        this.totalRegistros = this.CONTROL.total();
        this.totalPorPagina = Integer.parseInt((String) cboTotalPorPagina.getSelectedItem());

        //Calculamos el número de páginas
        totalPaginas = (int) (Math.ceil((double) this.totalRegistros / this.totalPorPagina));
        //En caso no haya registros debe mostrar al menos una página
        if (totalPaginas == 0) {
            totalPaginas = 1;
        }
        System.out.println("totalPaginas: " + totalPaginas);
        //Limpiamos el combo de números de página
        cboNumPagina.removeAllItems();

        for (int i = 1; i <= totalPaginas; i++) {
            cboNumPagina.addItem(Integer.toString(i));
        }

        cboNumPagina.setSelectedIndex(0);

    }

    private void listar(String texto, boolean paginar) {
        this.totalPorPagina = Integer.parseInt((String) cboTotalPorPagina.getSelectedItem());
        if ((String) cboNumPagina.getSelectedItem() != null) {
            this.numPagina = Integer.parseInt((String) cboNumPagina.getSelectedItem());
        }

        if (paginar == true) {
            tablaListado.setModel(this.CONTROL.listar(texto, this.totalPorPagina, this.numPagina));
        } else {
            tablaListado.setModel(this.CONTROL.listar(texto, this.totalPorPagina, 1));
        }

        //tablaListado.setModel(this.CONTROL.listar(texto, 3, 1));
        TableRowSorter orden = new TableRowSorter(tablaListado.getModel());
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaListado.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
        tablaListado.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        tablaListado.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        TableColumnModel tableColumns = tablaListado.getColumnModel();
        //id
        tableColumns.getColumn(0).setPreferredWidth(100);
        //categoriaId
        tableColumns.getColumn(1).setPreferredWidth(0);
        //categoriaNombre
        tableColumns.getColumn(2).setPreferredWidth(300);
        //codigo
        tableColumns.getColumn(3).setPreferredWidth(300);
        //nombre
        tableColumns.getColumn(4).setPreferredWidth(200);
        //precioVenta
        tableColumns.getColumn(5).setPreferredWidth(200);
        //stock
        tableColumns.getColumn(6).setPreferredWidth(200);
        //descripcion
        tableColumns.getColumn(7).setPreferredWidth(200);
        //imagen
        tableColumns.getColumn(8).setPreferredWidth(200);
        //activo
        tableColumns.getColumn(9).setPreferredWidth(200);
        
        TableCellRenderer renderer= tablaListado.getCellRenderer(0, 0);
        Component comp = renderer.getTableCellRendererComponent(tablaListado, "123", false, false, 0, 0);
        tablaListado.setRowHeight(new Double(comp.getPreferredSize().getHeight()).intValue() + 5);
        System.out.println("Tipo:" + tableColumns.getColumn(0).getWidth());
        orden.setComparator(0, (Object o1, Object o2) -> {
            Integer int1 = (Integer) o1;
            Integer int2 = (Integer) o2;
            return int1 - int2;
        });
        orden.setComparator(5, (Object p1, Object p2) -> {
            double pv1 = Double.valueOf(p1.toString());
            double pv2 = Double.valueOf(p2.toString());
            if (pv1 < pv2) {
                return -1;
            }
            if (pv1 > pv2) {
                return 1;
            }
            return 0;
        });
        orden.setComparator(6, (Object n1, Object n2) -> {
            Integer nt1 = (Integer) n1;
            Integer nt2 = (Integer) n2;
            return nt1 - nt2;
        });
        System.out.println("Tipo:" + tablaListado.getColumnClass(0));

        tablaListado.setRowSorter(orden);
        this.ocultarColumnas();
        lblTotalRegistros.setText("Mostrando " + this.CONTROL.totalMostrados() + " de un total de " + this.CONTROL.total() + " registros.");
    }

    private void cargarCategorias() {
        DefaultComboBoxModel items = this.CONTROL.seleccionar();
        cboCategoria.setModel(items);
    }

    private void subirImagenes() {
        File origen = new File(this.rutaOrigen);
        File destino = new File(this.rutaDestino);
        try {
            InputStream in = new FileInputStream(origen);
            OutputStream out = new FileOutputStream(destino);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtId.setText("");
        txtPrecioVenta.setText("");
        txtStock.setText("");
        this.imagen = "";
        this.imagenAnt = "";
        lblImagen.setIcon(null);
        this.rutaDestino = "";
        this.rutaOrigen = "";
        this.accion = "guardar";
    }

    private void mensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Sistema", JOptionPane.ERROR_MESSAGE);
    }

    private void mensajeOk(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Sistema", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabGeneral = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaListado = new javax.swing.JTable();
        lblTotalRegistros = new javax.swing.JLabel();
        btnNuevo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnDesactivar = new javax.swing.JButton();
        btnActivar = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        cboNumPagina = new javax.swing.JComboBox<>();
        cboTotalPorPagina = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        reporteArticulos = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        cboCategoria = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        btnAgregarImagen = new javax.swing.JToggleButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblImagen = new javax.swing.JLabel();
        txtPrecioVenta = new javax.swing.JFormattedTextField();
        txtStock = new javax.swing.JFormattedTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Articulos");

        tabGeneral.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Nombre");

        txtBuscar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        btnBuscar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        tablaListado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tablaListado);

        lblTotalRegistros.setText("Registros");

        btnNuevo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnEditar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnDesactivar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnDesactivar.setText("Desactivar");
        btnDesactivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesactivarActionPerformed(evt);
            }
        });

        btnActivar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnActivar.setText("Activar");
        btnActivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActivarActionPerformed(evt);
            }
        });

        jLabel10.setText("# Página");

        cboNumPagina.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cboNumPagina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboNumPaginaActionPerformed(evt);
            }
        });

        cboTotalPorPagina.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cboTotalPorPagina.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "10", "20", "50", "100" }));
        cboTotalPorPagina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTotalPorPaginaActionPerformed(evt);
            }
        });

        jLabel11.setText("Total de registros por página");

        reporteArticulos.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        reporteArticulos.setText("Reporte Artículos");
        reporteArticulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reporteArticulosActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("Generar Barcode");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnDesactivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnActivar, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboNumPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboTotalPorPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblTotalRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reporteArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnEditar)
                    .addComponent(btnNuevo)
                    .addComponent(reporteArticulos)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalRegistros)
                    .addComponent(jLabel10)
                    .addComponent(cboNumPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTotalPorPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(btnDesactivar)
                    .addComponent(btnActivar))
                .addContainerGap(120, Short.MAX_VALUE))
        );

        tabGeneral.addTab("Listado", jPanel1);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Nombre (*)");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Descripción");

        txtDescripcion.setColumns(20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setRows(5);
        jScrollPane2.setViewportView(txtDescripcion);

        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jLabel4.setText("(*) Indica que es campo obligatorio");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Categoria (*)");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Código (*)");

        btnAgregarImagen.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnAgregarImagen.setText("Agregar");
        btnAgregarImagen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarImagenActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Precio venta (*)");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Stock (*)");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Imagen");

        lblImagen.setBackground(new java.awt.Color(255, 255, 204));
        lblImagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagen.setText("Imagen");
        lblImagen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtPrecioVenta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtPrecioVenta.setMinimumSize(new java.awt.Dimension(100, 20));

        txtStock.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtStock.setMinimumSize(new java.awt.Dimension(100, 20));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(72, 72, 72)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnAgregarImagen)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                                    .addGap(120, 120, 120)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel2)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7))
                        .addGap(25, 25, 25)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtNombre)
                                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboCategoria, 0, 296, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(376, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtPrecioVenta, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                    .addComponent(txtStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30))))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)))
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(13, 13, 13)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(btnAgregarImagen)
                    .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnGuardar))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        tabGeneral.addTab("Mantenimiento", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabGeneral)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        this.listar(txtBuscar.getText().trim(), false);
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        tabGeneral.setEnabledAt(1, true);
        tabGeneral.setEnabledAt(0, false);
        tabGeneral.setSelectedIndex(1);
        this.accion = "guardar";
        btnGuardar.setText("Guardar");
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        tabGeneral.setEnabledAt(0, true);
        tabGeneral.setEnabledAt(1, false);
        tabGeneral.setSelectedIndex(0);
        this.limpiar();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        if (txtNombre.getText().trim().length() == 0 || txtNombre.getText().length() > 100) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un nombre y no debe ser mayor a 100 caracteres,\n es obligatorio", "Sistema", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        if (txtPrecioVenta.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un precio de venta,\n es obligatorio", "Sistema", JOptionPane.WARNING_MESSAGE);
            txtPrecioVenta.requestFocus();
            return;
        }
        if (txtStock.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el stock del artículo,\n es obligatorio", "Sistema", JOptionPane.WARNING_MESSAGE);
            txtStock.requestFocus();
            return;
        }
        if (txtDescripcion.getText().length() > 255) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una descripción y no debe ser mayor a 255 caracteres", "Sistema", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        String resp;
        if (this.accion.equals("editar")) {
            //Editar
            String imagenActual = "";
            if (this.imagen.equals("")) {
                imagenActual = this.imagenAnt;
            } else {
                imagenActual = this.imagen;
            }
            Categoria seleccionado = (Categoria) cboCategoria.getSelectedItem();
            resp = this.CONTROL.actualizar(Integer.parseInt(txtId.getText()), seleccionado.getId(), txtCodigo.getText().trim(), txtNombre.getText().trim(), this.nombreAnt, Double.parseDouble(txtPrecioVenta.getText()), Integer.parseInt(txtStock.getText()), txtDescripcion.getText().trim(), imagenActual);
            if (resp.equals("OK")) {
                if (!this.imagen.equals("")) {
                    this.subirImagenes();
                }
                this.mensajeOk("Actualizado correctamente");
                this.limpiar();
                this.listar("", false);
                tabGeneral.setEnabledAt(1, false);
                tabGeneral.setEnabledAt(0, true);
                tabGeneral.setSelectedIndex(0);
            } else {
                this.mensajeError(resp);
            }
        } else {
            //guardar
            //resp = "OK";
            Categoria seleccionado = (Categoria) cboCategoria.getSelectedItem();
            resp = this.CONTROL.insertar(seleccionado.getId(), txtCodigo.getText().trim(), txtNombre.getText().trim(), Double.parseDouble(txtPrecioVenta.getText()), Integer.parseInt(txtStock.getText()), txtDescripcion.getText().trim(), this.imagen);
            if (resp.equals("OK")) {
                if (!this.imagen.equals("")) {
                    this.subirImagenes();
                }
                this.mensajeOk("Regisrado correctamente");
                this.limpiar();
                this.listar("", false);
            } else {
                this.mensajeError(resp);
            }
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        // TODO add your handling code here:
        if (tablaListado.getSelectedRowCount() == 1) {
            String id = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 0));
            int categoriaId = Integer.parseInt(String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 1)));
            String categoriaNombre = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 2));
            String codigo = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 3));
            String nombre = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 4));
            this.nombreAnt = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 4));
            String precioVenta = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 5));
            String stock = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 6));
            String descripcion = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 7));
            this.imagenAnt = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 8));

            txtId.setText(id);
            Categoria seleccionado = new Categoria(categoriaId, categoriaNombre);
            cboCategoria.setSelectedItem(seleccionado);
            txtCodigo.setText(codigo);
            txtNombre.setText(nombre);
            txtPrecioVenta.setText(precioVenta);
            txtStock.setText(stock);
            txtDescripcion.setText(descripcion);

            ImageIcon im = new ImageIcon(this.DIRECTORIO + this.imagenAnt);
            Icon icono = new ImageIcon(im.getImage().getScaledInstance(lblImagen.getWidth(), lblImagen.getHeight(), Image.SCALE_DEFAULT));
            lblImagen.setIcon(icono);
            lblImagen.repaint();

            tabGeneral.setEnabledAt(0, false);
            tabGeneral.setEnabledAt(1, true);
            tabGeneral.setSelectedIndex(1);
            this.accion = "editar";
            btnGuardar.setText("Editar");
        } else {
            this.mensajeError("Debe seleccionar un item de la lista");
        }
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnActivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActivarActionPerformed
        if (tablaListado.getSelectedRowCount() == 1) {
            String id = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 0));
            String nombre = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 4));
            if (JOptionPane.showConfirmDialog(this, "¿Deseas activar el registro: " + nombre + "?", "Desactivar", JOptionPane.YES_NO_OPTION) == 0) {
                String resp = this.CONTROL.activar(Integer.parseInt(id));
                if (resp.equals("OK")) {
                    this.mensajeOk("Registro activado");
                    this.listar("", false);
                } else {
                    this.mensajeError(resp);
                }
            }
        } else {
            this.mensajeError("Seleccione 1 registro a activar");
        }        // TODO add your handling code here:
    }//GEN-LAST:event_btnActivarActionPerformed

    private void btnDesactivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesactivarActionPerformed
        if (tablaListado.getSelectedRowCount() == 1) {
            String id = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 0));
            String nombre = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 4));
            if (JOptionPane.showConfirmDialog(this, "¿Deseas desactivar el registro: " + nombre + "?", "Desactivar", JOptionPane.YES_NO_OPTION) == 0) {
                String resp = this.CONTROL.desactivar(Integer.parseInt(id));
                if (resp.equals("OK")) {
                    this.mensajeOk("Registro desactivado");
                    this.listar("", false);
                } else {
                    this.mensajeError(resp);
                }
            }
        } else {
            this.mensajeError("Seleccione 1 registro a desactivar");
        }
    }//GEN-LAST:event_btnDesactivarActionPerformed

    private void btnAgregarImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarImagenActionPerformed
        JFileChooser file = new JFileChooser();
        int estado = file.showOpenDialog(this);
        if (estado == JFileChooser.APPROVE_OPTION) {
            this.imagen = file.getSelectedFile().getName();
            this.rutaOrigen = file.getSelectedFile().getAbsolutePath();
            this.rutaDestino = this.DIRECTORIO + this.imagen;

            ImageIcon im = new ImageIcon(this.rutaOrigen);
            Icon icono = new ImageIcon(im.getImage().getScaledInstance(lblImagen.getWidth(), lblImagen.getHeight(), Image.SCALE_DEFAULT));
            lblImagen.setIcon(icono);
            lblImagen.repaint();
        }

    }//GEN-LAST:event_btnAgregarImagenActionPerformed

    private void cboNumPaginaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboNumPaginaActionPerformed
        if (this.primeraCarga == false) {
            this.listar("", true);
        }
    }//GEN-LAST:event_cboNumPaginaActionPerformed

    private void cboTotalPorPaginaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTotalPorPaginaActionPerformed
        this.paginar();
    }//GEN-LAST:event_cboTotalPorPaginaActionPerformed

    private void reporteArticulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reporteArticulosActionPerformed
        this.CONTROL.reporteArticulos();
    }//GEN-LAST:event_reporteArticulosActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.CONTROL.reporteArticulosBarras();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActivar;
    private javax.swing.JToggleButton btnAgregarImagen;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDesactivar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JComboBox<String> cboCategoria;
    private javax.swing.JComboBox<String> cboNumPagina;
    private javax.swing.JComboBox<String> cboTotalPorPagina;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JLabel lblTotalRegistros;
    private javax.swing.JButton reporteArticulos;
    private javax.swing.JTabbedPane tabGeneral;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JFormattedTextField txtPrecioVenta;
    private javax.swing.JFormattedTextField txtStock;
    // End of variables declaration//GEN-END:variables
}
