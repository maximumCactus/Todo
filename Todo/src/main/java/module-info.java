module com.github.maximumcactus.todo {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.jetbrains.annotations;

    opens com.github.maximumcactus.todo to javafx.fxml;
    exports com.github.maximumcactus.todo;
    opens com.github.maximumcactus.todo.Classes to com.google.gson;
}