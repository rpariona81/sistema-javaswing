/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentacion;

import entidades.Articulo;
import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import negocio.IngresoControl;

/**
 *
 * @author JRonald
 */
public class FrmIngreso extends javax.swing.JInternalFrame {
    
    private final IngresoControl CONTROL;
    private String accion;
    private int totalPorPagina = 10;
    private int numPagina = 10;
    private boolean primeraCarga = true;
    private int totalRegistros;
    
    public DefaultTableModel modeloDetalles;
    
    public JFrame contenedor;

    /**
     * Creates new form FrmArticulo
     */
    public FrmIngreso(JFrame frmP) {
        initComponents();
        this.contenedor = frmP;
        this.CONTROL = new IngresoControl();
        this.paginar();
        this.listar("", false);
        this.primeraCarga = false;
        tabGeneral.setEnabledAt(1, false);
        this.accion = "guardar";
        this.txtIdProveedor.setVisible(false);
        this.crearDetalles();
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
    
    private void ocultarColumnas() {
        tablaListado.getColumnModel().getColumn(1).setMaxWidth(0);
        tablaListado.getColumnModel().getColumn(1).setMinWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(1).setMinWidth(0);
        tablaListado.getColumnModel().getColumn(3).setMaxWidth(0);
        tablaListado.getColumnModel().getColumn(3).setMinWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(3).setMinWidth(0);
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
        TableColumnModel tableColumns = tablaListado.getColumnModel();
        //id
        tableColumns.getColumn(0).setPreferredWidth(100);
        //usuario_id
        tableColumns.getColumn(1).setPreferredWidth(0);
        //usuario_nombre
        tableColumns.getColumn(2).setPreferredWidth(300);
        //persona_id
        tableColumns.getColumn(3).setPreferredWidth(0);
        //persona_nombre
        tableColumns.getColumn(4).setPreferredWidth(200);
        //tipo_comprobante
        tableColumns.getColumn(5).setPreferredWidth(200);
        //serie_comprobante
        tableColumns.getColumn(6).setPreferredWidth(200);
        //num_comprobante
        tableColumns.getColumn(7).setPreferredWidth(200);
        //fecha
        tableColumns.getColumn(8).setPreferredWidth(200);
        //impuesto
        tableColumns.getColumn(9).setPreferredWidth(200);
        //total
        tableColumns.getColumn(10).setPreferredWidth(200);
        //estado
        tableColumns.getColumn(11).setPreferredWidth(200);
        
        TableCellRenderer renderer = tablaListado.getCellRenderer(0, 0);
        Component comp = renderer.getTableCellRendererComponent(tablaListado, "123", false, false, 0, 0);
        tablaListado.setRowHeight(new Double(comp.getPreferredSize().getHeight()).intValue() + 5);
        System.out.println("Tipo:" + tableColumns.getColumn(0).getWidth());
        orden.setComparator(0, (Object o1, Object o2) -> {
            Integer int1 = (Integer) o1;
            Integer int2 = (Integer) o2;
            return int1 - int2;
        });
        System.out.println("Tipo:" + tablaListado.getColumnClass(0));
        this.ocultarColumnas();
        tablaListado.setRowSorter(orden);
        lblTotalRegistros.setText("Mostrando " + this.CONTROL.totalMostrados() + " de un total de " + this.CONTROL.total() + " registros.");
    }
    
    private void crearDetalles() {
        modeloDetalles = new DefaultTableModel() {
            @Override
            public boolean isCellEditable​(int row, int column) {
                if (column == 3) {
                    return column == 3;
                }
                if (column == 4) {
                    return column == 4;
                }
                return column == 3;
            }
            
            @Override
            public Object getValueAt​(int row, int column) {
                if (column == 5) {
                    Double cantD;
                    try {
                        cantD = Double.parseDouble((String) getValueAt(row, 3));
                    } catch (Exception e) {
                        cantD = 1.0;
                    }
                    Double precioD = Double.parseDouble((String) getValueAt(row, 4));
                    if (cantD != null && precioD != null) {
                        return String.format("%.2f", cantD * precioD);
                    }
                }
                return super.getValueAt(row, column);
            }
            
            @Override
            public void setValueAt​(Object aValue, int row, int column) {
                super.setValueAt(aValue, row, column);
                calcularTotales();
                fireTableDataChanged();
            }
        };
        modeloDetalles.setColumnIdentifiers(new Object[]{"Id", "Código", "Artículo", "Cantidad", "Precio", "Subtotal"});
        tablaDetalles.setModel(modeloDetalles);
    }

    public void agregarDetalles(String id, String codigo, String nombre, String precio) {
        String idT;
        boolean existe = false;
        for (int i = 0; i < this.modeloDetalles.getRowCount(); i++) {
            idT = String.valueOf(this.modeloDetalles.getValueAt(i, 0));
            if (idT.equals(id)) {
                existe = true;
            }            
        }
        if (existe) {
            this.mensajeError("El artículo ya ha sido agregado");
        } else {
            this.modeloDetalles.addRow(new Object[]{id, codigo, nombre, "1", precio, precio});
        }
    }
    
    private void calcularTotales() {
        
    }
    
    private void limpiar() {
        txtNombreProveedor.setText("");
        txtIdProveedor.setText("");
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
        btnDesactivar = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        cboNumPagina = new javax.swing.JComboBox<>();
        cboTotalPorPagina = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNombreProveedor = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtIdProveedor = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        btnSeleccionaProveedor = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtImpuesto = new javax.swing.JTextField();
        cboTipoComprobante = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtSerieComprobante = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtNumComprobante = new javax.swing.JTextField();
        txtCodigo = new javax.swing.JTextField();
        btnVerArticulo = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDetalles = new javax.swing.JTable();
        txtSubtotal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtTotalImpuesto = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Ingresos Almacén");

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

        btnDesactivar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnDesactivar.setText("Anular");
        btnDesactivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesactivarActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 897, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnDesactivar, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(249, 249, 249)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboNumPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboTotalPorPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblTotalRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(0, 9, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnNuevo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalRegistros)
                    .addComponent(jLabel10)
                    .addComponent(cboNumPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTotalPorPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(btnDesactivar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabGeneral.addTab("Listado", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Proveedor (*)");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Artículo");

        txtNombreProveedor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                btnGuardarAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
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

        txtIdProveedor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Tipo Comprobante (*)");

        btnSeleccionaProveedor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSeleccionaProveedor.setText("...");
        btnSeleccionaProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionaProveedorActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Impuesto (*)");

        txtImpuesto.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtImpuesto.setText("0.18");

        cboTipoComprobante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BOLETA", "FACTURA", "TICKET", "GUIA" }));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Serie comprobante");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Número Comprobante (*)");

        txtCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoKeyReleased(evt);
            }
        });

        btnVerArticulo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnVerArticulo.setText("Ver");
        btnVerArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerArticuloActionPerformed(evt);
            }
        });

        tablaDetalles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tablaDetalles);

        txtSubtotal.setEditable(false);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Subtotal");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Total Impuesto");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Total");

        txtTotalImpuesto.setEditable(false);

        txtTotal.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(100, 100, 100)
                                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnVerArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(cboTipoComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel9)
                                        .addGap(54, 54, 54)
                                        .addComponent(txtSerieComprobante))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtNombreProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnSeleccionaProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNumComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 909, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                    .addGap(717, 717, 717)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel8))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtTotalImpuesto, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                                        .addComponent(txtTotal)
                                        .addComponent(txtSubtotal)))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSeleccionaProveedor)
                    .addComponent(jLabel5)
                    .addComponent(txtImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel9)
                    .addComponent(cboTipoComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSerieComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(txtNumComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerArticulo)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtTotalImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar)
                    .addComponent(jLabel4))
                .addContainerGap())
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
            .addComponent(tabGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
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

    private void btnDesactivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesactivarActionPerformed
        if (tablaListado.getSelectedRowCount() == 1) {
            String id = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 0));
            String nombre = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 2));
            if (JOptionPane.showConfirmDialog(this, "¿Deseas desactivar el registro: " + nombre + "?", "Desactivar", JOptionPane.YES_NO_OPTION) == 0) {
                String resp = this.CONTROL.anular(Integer.parseInt(id));
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

    private void cboNumPaginaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboNumPaginaActionPerformed
        if (this.primeraCarga == false) {
            this.listar("", true);
        }
    }//GEN-LAST:event_cboNumPaginaActionPerformed

    private void cboTotalPorPaginaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTotalPorPaginaActionPerformed
        this.paginar();
    }//GEN-LAST:event_cboTotalPorPaginaActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        tabGeneral.setEnabledAt(0, true);
        tabGeneral.setEnabledAt(1, false);
        tabGeneral.setSelectedIndex(0);
        this.limpiar();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnSeleccionaProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionaProveedorActionPerformed
        FrmSeleccionarProveedorCompra frm = new FrmSeleccionarProveedorCompra(contenedor, this, true);
        frm.toFront();
    }//GEN-LAST:event_btnSeleccionaProveedorActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        /*if (txtNombreProveedor.getText().trim().length() == 0 || txtNombreProveedor.getText().length() > 70) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un nombre y no debe ser mayor a 70 caracteres,\n es obligatorio", "Sistema", JOptionPane.WARNING_MESSAGE);
            txtNombreProveedor.requestFocus();
            return;
        }

        if (txtEmail.getText().length() > 50) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un email y no debe ser mayor a 50 caracteres,\n es obligatorio", "Sistema", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }

        if (txtNumDocumento.getText().trim().length() > 20) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un número de documento no mayor a 20 caracteres", "Sistema", JOptionPane.WARNING_MESSAGE);
            txtNumDocumento.requestFocus();
            return;
        }

        if (txtDireccion.getText().trim().length() > 70) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una direccion no mayor a 70 caracteres", "Sistema", JOptionPane.WARNING_MESSAGE);
            txtDireccion.requestFocus();
            return;
        }

        if (txtTelefono.getText().trim().length() > 15) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un teléfono no mayor a 15 caracteres", "Sistema", JOptionPane.WARNING_MESSAGE);
            txtTelefono.requestFocus();
            return;
        }
         */
        String resp;
        if (this.accion.equals("editar")) {
            //Editar
            //resp = this.CONTROL.actualizar(Integer.parseInt(txtId.getText()), "Cliente", txtNombre.getText().trim(), this.nombreAnt, (String) cboTipoDocumento.getSelectedItem(), txtNumDocumento.getText().trim(), txtDireccion.getText().trim(), txtTelefono.getText().trim(), txtEmail.getText().trim());
            /*if (resp.equals("OK")) {
                this.mensajeOk("Actualizado correctamente");
                this.limpiar();
                this.listar("", false);
                tabGeneral.setEnabledAt(1, false);
                tabGeneral.setEnabledAt(0, true);
                tabGeneral.setSelectedIndex(0);
            } else {
                this.mensajeError(resp);
            }*/
        } else {
            //guardar
            resp = "OK";
            //resp = this.CONTROL.insertar("Cliente", txtNombre.getText().trim(), (String) cboTipoDocumento.getSelectedItem(), txtNumDocumento.getText().trim(), txtDireccion.getText().trim(), txtTelefono.getText().trim(), txtEmail.getText().trim());
            if (resp.equals("OK")) {
                this.mensajeOk("Regisrado correctamente");
                this.limpiar();
                this.listar("", false);
            } else {
                this.mensajeError(resp);
            }
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnGuardarAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_btnGuardarAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuardarAncestorAdded

    private void btnVerArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerArticuloActionPerformed
        FrmSeleccionarArticuloCompra frm = new FrmSeleccionarArticuloCompra(contenedor, this, true);
        frm.toFront();
    }//GEN-LAST:event_btnVerArticuloActionPerformed

    private void txtCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyReleased
        if (txtCodigo.getText().trim().length() > 0) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                entidades.Articulo art;
                art = this.CONTROL.obteneArticuloCodigoIngreso(txtCodigo.getText().trim());
                if (art == null) {
                    this.mensajeError("No existe un artículo con ese código.");
                } else {
                    this.agregarDetalles(Integer.toString(art.getId()), art.getCodigo(), art.getNombre(), Double.toString(art.getPrecioVenta()));
                }
            }
        } else {
            this.mensajeError("Ingrese el código a buscar.");
        }
    }//GEN-LAST:event_txtCodigoKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDesactivar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnSeleccionaProveedor;
    private javax.swing.JButton btnVerArticulo;
    private javax.swing.JComboBox<String> cboNumPagina;
    private javax.swing.JComboBox<String> cboTipoComprobante;
    private javax.swing.JComboBox<String> cboTotalPorPagina;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JLabel lblTotalRegistros;
    private javax.swing.JTabbedPane tabGeneral;
    private javax.swing.JTable tablaDetalles;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    public javax.swing.JTextField txtIdProveedor;
    private javax.swing.JTextField txtImpuesto;
    public javax.swing.JTextField txtNombreProveedor;
    private javax.swing.JTextField txtNumComprobante;
    private javax.swing.JTextField txtSerieComprobante;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTotalImpuesto;
    // End of variables declaration//GEN-END:variables

}
