package com.ngocrong.server.pgsql;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PgSQLConnect {

    private static final Logger logger = Logger.getLogger(PgSQLConnect.class);
    private static Connection conn;

    public static Connection getConnection() {
        return conn;
    }

    public static synchronized void init(String host, int port, String database, String user, String pass) {
        // Thay đổi URL cho PostgreSQL
        String url = String.format("jdbc:postgresql://%s:%d/%s?sslmode=disable", host, port, database);

        try {
            // Thay đổi driver cho PostgreSQL
            Class.forName("org.postgresql.Driver"); // Kiểm tra driver
            logger.debug("PostgreSQL connect: " + url);
            conn = DriverManager.getConnection(url, user, pass);
            logger.debug("Successful connection");
        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL driver not found!", e);
            throw new RuntimeException("PostgreSQL driver not found", e);
        } catch (SQLException e) {
            logger.error("Failed to connect to the database!", e);
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }

    public static synchronized boolean close() {
        if (conn != null) {
            try {
                logger.debug("Closing connection to database");
                conn.close();
                return true;
            } catch (SQLException e) {
                logger.error("Failed to close connection", e);
                return false;
            } finally {
                conn = null; // Đặt lại kết nối thành null sau khi đóng
            }
        }
        logger.debug("No connection to close");
        return false;
    }
}
