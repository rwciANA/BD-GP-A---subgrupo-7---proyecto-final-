package com.cooperativa.desktop;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/cooperativa";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema de Cooperativa");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout(10, 10));
            root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            root.setBackground(new Color(245, 248, 252));

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            JLabel title = new JLabel("Panel de consultas de cooperativa");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(new Color(24, 76, 132));
            header.add(title, BorderLayout.NORTH);

            JLabel subtitle = new JLabel("Conéctate a PostgreSQL y explora tus tablas de forma sencilla.");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subtitle.setForeground(new Color(90, 103, 116));
            header.add(subtitle, BorderLayout.CENTER);
            root.add(header, BorderLayout.NORTH);

            JTabbedPane tabs = new JTabbedPane();
            tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JPanel resumenPanel = new JPanel(new BorderLayout(10, 10));
            resumenPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            resumenPanel.setBackground(Color.WHITE);

            JTextArea resumenArea = new JTextArea();
            resumenArea.setEditable(false);
            resumenArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            resumenArea.setBackground(new Color(248, 250, 252));
            resumenArea.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 226, 232)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            JScrollPane resumenScroll = new JScrollPane(resumenArea);

            JLabel resumenTitle = new JLabel("Resumen de la base de datos");
            resumenTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
            resumenPanel.add(resumenTitle, BorderLayout.NORTH);
            resumenPanel.add(resumenScroll, BorderLayout.CENTER);

            JPanel datosPanel = new JPanel(new BorderLayout(10, 10));
            datosPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            datosPanel.setBackground(Color.WHITE);

            JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            controls.setOpaque(false);
            JButton cargarBtn = new JButton("Cargar tablas");
            cargarBtn.setBackground(new Color(30, 120, 200));
            cargarBtn.setForeground(Color.WHITE);
            JButton clientesBtn = new JButton("Clientes");
            JButton cuentasBtn = new JButton("Cuentas");
            JButton movimientosBtn = new JButton("Movimientos");
            JComboBox<String> tablaBox = new JComboBox<>();
            JLabel tablaLabel = new JLabel("Tabla:");
            controls.add(cargarBtn);
            controls.add(clientesBtn);
            controls.add(cuentasBtn);
            controls.add(movimientosBtn);
            controls.add(tablaLabel);
            controls.add(tablaBox);
            datosPanel.add(controls, BorderLayout.NORTH);

            JTextArea datosArea = new JTextArea();
            datosArea.setEditable(false);
            datosArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            datosArea.setBackground(new Color(248, 250, 252));
            datosArea.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 226, 232)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            JScrollPane datosScroll = new JScrollPane(datosArea);
            datosPanel.add(datosScroll, BorderLayout.CENTER);

            tabs.addTab("Resumen", resumenPanel);
            tabs.addTab("Datos", datosPanel);
            root.add(tabs, BorderLayout.CENTER);

            JLabel status = new JLabel("Estado: intentando conectar a PostgreSQL...");
            status.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            status.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            root.add(status, BorderLayout.SOUTH);

            cargarBtn.addActionListener(e -> {
                cargarTablas(tablaBox, resumenArea, datosArea, status);
            });

            clientesBtn.addActionListener(e -> cargarModulo("Clientes", datosArea, status));
            cuentasBtn.addActionListener(e -> cargarModulo("Cuentas", datosArea, status));
            movimientosBtn.addActionListener(e -> cargarModulo("Movimientos", datosArea, status));

            tablaBox.addActionListener(e -> {
                String tabla = (String) tablaBox.getSelectedItem();
                if (tabla != null) {
                    cargarDatos(tabla, datosArea, status);
                }
            });

            frame.setContentPane(root);
            frame.setVisible(true);

            cargarTablas(tablaBox, resumenArea, datosArea, status);
        });
    }

    private static void cargarTablas(JComboBox<String> tablaBox, JTextArea resumenArea,
            JTextArea datosArea, JLabel status) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            DatabaseMetaData meta = con.getMetaData();
            ResultSet rs = meta.getTables(null, "public", null, new String[] { "TABLE" });
            List<String> tablas = new ArrayList<>();
            while (rs.next()) {
                tablas.add(rs.getString("TABLE_NAME"));
            }
            tablaBox.removeAllItems();
            for (String tabla : tablas) {
                tablaBox.addItem(tabla);
            }

            if (tablas.isEmpty()) {
                resumenArea.setText("No se encontraron tablas en el esquema public.");
                datosArea.setText("No hay tablas disponibles para mostrar.");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Base de datos: cooperativa\n");
                sb.append("Usuario: ").append(USER).append("\n");
                sb.append("Tablas encontradas: ").append(tablas.size()).append("\n\n");
                for (String tabla : tablas) {
                    sb.append("- ").append(tabla).append("\n");
                }
                resumenArea.setText(sb.toString());
                datosArea.setText("Selecciona una tabla para ver sus datos.");
            }
            status.setText("Estado: conexión exitosa a PostgreSQL. Listo para consultar.");
        } catch (SQLException ex) {
            resumenArea.setText("Error de conexión: " + ex.getMessage());
            datosArea.setText("No se pudo conectar a la base de datos.");
            status.setText("Estado: no se pudo conectar a PostgreSQL.");
        }
    }

    private static void cargarDatos(String tabla, JTextArea area, JLabel status) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM " + quoteIdentifier(tabla) + " LIMIT 20";
            try (PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnas = meta.getColumnCount();
                StringBuilder sb = new StringBuilder();
                sb.append("Tabla: ").append(tabla).append("\n");
                sb.append("Columnas: ").append(columnas).append("\n\n");

                boolean hayDatos = false;
                while (rs.next()) {
                    hayDatos = true;
                    for (int i = 1; i <= columnas; i++) {
                        sb.append(meta.getColumnName(i)).append(" = ")
                                .append(rs.getObject(i)).append("\n");
                    }
                    sb.append("------------------------\n");
                }

                if (!hayDatos) {
                    sb.append("No hay registros en esta tabla.");
                }
                area.setText(sb.toString());
                status.setText("Estado: mostrando datos de la tabla " + tabla + ".");
            }
        } catch (SQLException ex) {
            area.setText("Error al consultar la tabla: " + ex.getMessage());
            status.setText("Estado: error al leer la tabla " + tabla + ".");
        }
    }

    private static void cargarModulo(String modulo, JTextArea area, JLabel status) {
        String[] tablasSugeridas;
        switch (modulo) {
            case "Clientes":
                tablasSugeridas = new String[] { "clientes", "socios", "cliente" };
                break;
            case "Cuentas":
                tablasSugeridas = new String[] { "cuentas", "cuenta", "ahorros" };
                break;
            case "Movimientos":
                tablasSugeridas = new String[] { "movimientos", "transacciones", "movimiento" };
                break;
            default:
                tablasSugeridas = new String[0];
                break;
        }

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            List<String> tablas = obtenerTablas(con);
            String tablaEncontrada = null;
            for (String sugerida : tablasSugeridas) {
                if (tablas.contains(sugerida.toLowerCase())) {
                    tablaEncontrada = sugerida;
                    break;
                }
            }

            if (tablaEncontrada == null) {
                area.setText("No se encontró una tabla correspondiente para " + modulo + ".\n"
                        + "Las tablas disponibles en la base son: " + String.join(", ", tablas));
                status.setText("Estado: no se encontró un módulo disponible para " + modulo + ".");
                return;
            }

            String sql = "SELECT * FROM " + quoteIdentifier(tablaEncontrada) + " LIMIT 15";
            try (PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnas = meta.getColumnCount();
                StringBuilder sb = new StringBuilder();
                sb.append("Módulo: ").append(modulo).append("\n");
                sb.append("Tabla: ").append(tablaEncontrada).append("\n");
                sb.append("Columnas: ").append(columnas).append("\n\n");

                boolean hayDatos = false;
                while (rs.next()) {
                    hayDatos = true;
                    for (int i = 1; i <= columnas; i++) {
                        sb.append(meta.getColumnName(i)).append(" = ")
                                .append(rs.getObject(i)).append("\n");
                    }
                    sb.append("------------------------\n");
                }

                if (!hayDatos) {
                    sb.append("No hay registros disponibles en este módulo.");
                }

                area.setText(sb.toString());
                status.setText("Estado: mostrando vista rápida de " + modulo + ".");
            }
        } catch (SQLException ex) {
            area.setText("No fue posible consultar el módulo " + modulo + ": " + ex.getMessage());
            status.setText("Estado: error al consultar " + modulo + ".");
        }
    }

    private static List<String> obtenerTablas(Connection con) throws SQLException {
        List<String> tablas = new ArrayList<>();
        DatabaseMetaData meta = con.getMetaData();
        ResultSet rs = meta.getTables(null, "public", null, new String[] { "TABLE" });
        while (rs.next()) {
            tablas.add(rs.getString("TABLE_NAME").toLowerCase());
        }
        return tablas;
    }

    private static String quoteIdentifier(String identifier) {
        return '"' + identifier.replace("\"", "\"\"") + '"';
    }
}
