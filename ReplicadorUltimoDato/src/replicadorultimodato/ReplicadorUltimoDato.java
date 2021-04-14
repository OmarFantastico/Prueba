package replicadorultimodato;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import replicadorultimodato.controlador.DataBaseConnection;

/**
 *
 * @author robert
 */
public class ReplicadorUltimoDato {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String[] elecciones = {"presidencia_m", 
                                "alcalde_milpa_alta", 
                                "presidencia_m_coahuila",
                                "senador_coahuila",
                                "diputado_coahuila_1",
                                "diputado_coahuila_2",
                                "diputado_coahuila_3",
                                "diputado_coahuila_4",
                                "diputado_coahuila_5",
                                "diputado_coahuila_6",
                                "diputado_coahuila_7"
        };
        PreparedStatement prst;
        ResultSet rs;
        Statement insertar;
        while (true) {
            System.out.println("Ejecutando función: " + new Timestamp(System.currentTimeMillis()));
            for (String eleccion : elecciones) {
                try {
                    prst = DataBaseConnection.getInstance().getConnection().prepareStatement(String.format(" select * from captura_datos_clientes WHERE eleccion = '%s' ORDER BY id DESC LIMIT 1", eleccion));
                    rs = prst.executeQuery();
                    if (rs.next()) {
                        Timestamp t1 = new Timestamp(System.currentTimeMillis());
                        Timestamp t2 = rs.getTimestamp("dia");
                        if (diferenciaEnMinutos(t1, t2) >= 15) {
                            System.out.println("Insertando nuevo dato para '" + eleccion + "'\nÚltimo dato: " + t2 + "\n\n");
                            insertar = DataBaseConnection.getInstance().getConnection().createStatement();
                            insertar.executeUpdate(String.format("INSERT INTO captura_datos_clientes(hora,p1,p2,p3,p4,p5,p6,diferencia,no_respuesta,por_avance_cr,insercion_man_aut,iscr,eleccion,dia) "
                                    + "                                                       VALUES('%s',%f,%f,%f,%f,%f,%f,%f        ,%f          ,%f           ,'auto'           ,'%s','%s'    ,'%s')",
                                    formatearFecha(t1), rs.getDouble("p1"), rs.getDouble("p2"), rs.getDouble("p3"), rs.getDouble("p4"), rs.getDouble("p5"),
                                    rs.getDouble("p6"),rs.getDouble("diferencia"), rs.getDouble("no_respuesta"), rs.getDouble("por_avance_cr"), rs.getString("iscr"), rs.getString("eleccion"), t1));
                        }
                    }
                } catch (SQLException sqle) {
                    System.err.println("Error al obtener los últimos datos: " + sqle.getMessage());
                    sqle.printStackTrace();
                }
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ex) {
                System.err.println("Error en el hilo de ejecución: " + ex.getMessage());
            }
        }
    }

    public static String formatearFecha(Timestamp t) {
        Date date = new Date();
        date.setTime(t.getTime());
        return new SimpleDateFormat("HH:mm").format(date);
    }

    public static long diferenciaEnMinutos(Timestamp currentTime, Timestamp oldTime) {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = currentTime.getTime();
        long diff = milliseconds2 - milliseconds1;
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return diffMinutes;
    }

}
