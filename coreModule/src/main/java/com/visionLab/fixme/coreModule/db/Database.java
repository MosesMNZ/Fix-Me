package com.visionLab.fixme.coreModule.db;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;

import java.sql.*;

import com.visionLab.fixme.coreModule.MessageColors;

public class Database {
    private static final String DATABASE_URL = "jdbc:sqlite:resources/transactions.db";
    private static final String INSERT_QUERY = "INSERT INTO transactions(marketComponent_name, brokerComponent_name, op_type, instrument, " +
            "price, quantity, result, comment) VALUES(?,?,?,?,?,?,?,?)";
    private static final String SELECT_QUERY = "SELECT * FROM transactions";
    private static Connection connection = null;

    private static void connect() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            System.out.println(MessageColors.ANSI_RED
                + "Database connection failed" + MessageColors.ANSI_RESET
            );
        }
    }

    private static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
            connection = null;
        } catch (SQLException e) {
            System.out.println(MessageColors.ANSI_RED +
                "Database closing connection failed"
                + MessageColors.ANSI_RESET);
        }
    }

    private static Connection getConnection() {
        if (connection == null) {
            connect();
        }
        return connection;
    }

    public static void insert(String marketComponentName, String brokerComponentName, String type, String instrument,
                              String price, String quantity, String result, String comment) {
        final Connection localConnection = getConnection();
        if (localConnection != null) {
            try (final PreparedStatement pstmt = localConnection.prepareStatement(INSERT_QUERY)) {
                pstmt.setString(1, marketComponentName);
                pstmt.setString(2, brokerComponentName);
                pstmt.setString(3, type);
                pstmt.setString(4, instrument);
                pstmt.setString(5, price);
                pstmt.setString(6, quantity);
                pstmt.setString(7, result);
                pstmt.setString(8, comment);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(MessageColors.ANSI_RED 
                    + "Error on insert"
                    + MessageColors.ANSI_RESET);
            } finally {
                close();
            }
        }
    }

    public static void selectAll() {
        final Connection localConnection = getConnection();
        if (localConnection != null) {
            try (final Statement stmt = localConnection.createStatement();
                final ResultSet rs = stmt.executeQuery(SELECT_QUERY)) {
                final AsciiTable at = new AsciiTable();
                at.addRule();
                at.addRow("id", "marketComponent", "BrokerComponent", "Operation", "Instrument", "Quantity", "Result", "Comment");
                while (rs.next()) {
                    at.addRule();
                    at.addRow(rs.getInt("id"),
                            rs.getString("marketComponent_name"),
                            rs.getString("brokerComponent_name"),
                            rs.getString("op_type"),
                            rs.getString("instrument"),
                            rs.getString("quantity"),
                            rs.getString("result"),
                            rs.getString("comment"));
                }
                at.addRule();
                at.getRenderer().setCWC(new CWC_LongestLine());
                System.out.println(MessageColors.ANSI_GREEN +
                    at.render() + MessageColors.ANSI_RESET);
            } catch (SQLException e) {
                System.out.println(MessageColors.ANSI_RED +
                    "Error on select" + MessageColors.ANSI_RESET);
            } finally {
                close();
            }
        }
    }
}
