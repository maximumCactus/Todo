package com.github.maximumcactus.todo;

import com.github.maximumcactus.todo.Classes.FileDataType;
import com.github.maximumcactus.todo.Classes.FileHandler;
import com.github.maximumcactus.todo.Classes.Todo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class TodoView_Controller implements Initializable {
    @FXML
    Label lblTitle, lblDesc, lblFinishDate;

    @FXML
    Button btnBack, btnMarkDone, btnEdit, btnDelete;

    @FXML
    Parent rootParent;

    @FXML
    HBox hbTags;

    private static int id = -1;

    public static void setTodoID(int id){
        TodoView_Controller.id = id;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FileHandler fileHandler = new FileHandler();
        Todo todo = fileHandler.read(FileDataType.TODO, id);
        lblTitle.setText(todo.getTitle());
        lblDesc.setText(todo.getDesc());
        lblFinishDate.setText(todo.getEnd_date_str());

        if (todo.getTags() == null) {
            makeTag(todo.getTagName());
        }else{
            for (String tagName: todo.getTagsName()){
                makeTag(tagName);
            }
        }
    }

    @FXML
    protected void onBtnMarkDoneClick(){
        FileHandler fileHandler = new FileHandler();
        Todo todo = fileHandler.read(FileDataType.TODO, id);
        todo.setHas_done();
        todo.saveChanges();
    }

    @FXML
    protected void onBtnEditClick(){
        TodoForm_Controller.setIsEditMode(id);
        try {
            rootParent.getScene().setRoot(FXMLLoader.load(getClass().getResource("todo-form.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void onBtnBackClick(){
        try{
            rootParent.getScene().setRoot(FXMLLoader.load(getClass().getResource("todo-list.fxml")));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void onBtnDeleteClick(){
        Alert confirmDelete = new Alert(Alert.AlertType.WARNING);
        confirmDelete.setContentText("Are you sure to delete this item? It will be permanently gone.");
        Optional<ButtonType> result = confirmDelete.showAndWait();
        if (result.get() == ButtonType.OK){
            FileHandler fileHandler = new FileHandler();
            Todo todo = fileHandler.read(FileDataType.TODO, id);
            todo.delete();

            try {
                rootParent.getScene().setRoot(FXMLLoader.load(getClass().getResource("todo-list.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void makeTag(String tagName){
        HBox hbTag = new HBox();
        hbTag.setBackground(new Background(new BackgroundFill(Color.web("#ffffffcc"), new CornerRadii(5), Insets.EMPTY)));
        hbTag.setPrefHeight(Region.USE_COMPUTED_SIZE);
        hbTag.setPadding(new Insets(4,10,4,10));

        Label lblTag = new Label(tagName);
        lblTag.setWrapText(true);
        lblTag.setPrefHeight(Region.USE_COMPUTED_SIZE);
        hbTag.getChildren().add(lblTag);
        hbTags.getChildren().add(hbTag);

    }

}
