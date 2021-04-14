package replicadorultimodato.controlador;

import java.sql.*;
import java.util.Properties;

/**
 *
 * @author robert
 */
public class DataBaseConnection {

    private static DataBaseConnection instance;
    private Connection connection;
    private String ip = "exit2018.cdhgry3a0lbp.us-east-1.rds.amazonaws.com";
    private String puerto = "5432";
    private String nombrebase = "exit2018";

    Properties props = new Properties();

    private DataBaseConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            props.setProperty("user", "masterexit2018");
            props.setProperty("password", "RM87.-09$r81-YtPKapsY96_M");
            props.setProperty("ssl", "true");
            props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
            String url = "jdbc:postgresql://" + ip + ":" + puerto + "/" + nombrebase;
            this.connection = DriverManager.getConnection(url, props);
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("Error al conectar a la base de datos aws: " + ex.getMessage());
        }
    }

    public static DataBaseConnection getInstance() {
        try {
            if (instance == null) {
                instance = new DataBaseConnection();
            } else if (instance.getConnection().isClosed()) {
                instance = new DataBaseConnection();
            }
        } catch (SQLException ex) {
            System.err.println("Error al realizar la conexión de la base de datos AWS: " + ex.getMessage());
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

}
