module org.example.javalab1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens org.example.javalab1 to javafx.fxml;
    exports org.example.javalab1;
}