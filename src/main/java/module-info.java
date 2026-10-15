module org.ezone.pae.proyectocrudg3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    exports org.ezone.pae.proyectocrudg3;

    opens org.ezone.pae.proyectocrudg3.controller to javafx.fxml;
    opens org.ezone.pae.proyectocrudg3.model to javafx.base;
}