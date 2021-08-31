package com.github.maximumcactus.todo;

import com.github.maximumcactus.todo.Classes.FileDataType;
import com.github.maximumcactus.todo.Classes.FileHandler;
import com.github.maximumcactus.todo.Classes.Tag;
import com.github.maximumcactus.todo.Classes.Todo;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class TodoForm_Controller implements Initializable {
    @FXML
    private Parent rootParent;

    @FXML
    private Button btnSave, btnCancel, btnAddTag;

    @FXML
    private TextField txtTitle, txtTag;

    @FXML
    private TextArea txtDesc;

    @FXML
    private DatePicker dpFinishDate;

    @FXML
    private HBox hbTags;

    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private ArrayList<Integer> tagIds = new ArrayList<>();
    private ArrayList<String> tagNames = new ArrayList<>();


    private static boolean isEditMode = false;
    private static int id = new FileHandler().get_last_id(FileDataType.TODO) + 1;

    public static void setIsEditMode(int id){
        isEditMode = true;
        TodoForm_Controller.id = id;
    }
    public static void resetIsEditMode(){
        isEditMode = false;
        id = new FileHandler().get_last_id(FileDataType.TODO) + 1;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (isEditMode) {
            FileHandler fileHandler = new FileHandler();
            Todo todo = fileHandler.read(FileDataType.TODO, id);
            String title, desc;
            LocalDate finish_date;
            String tag;

            title = todo.getTitle();
            desc = todo.getDesc();
            finish_date = LocalDate.parse(todo.getEnd_date_str(), dateFormat);

            txtTitle.setText(title);
            txtDesc.setText(desc);
            dpFinishDate.setValue(finish_date);

            if (todo.getTags() == null){
                tag = todo.getTagName();
                makeTag(tag);
                tagNames.add(tag);
            }else {
                for (String tagName : todo.getTagsName()) {
                    makeTag(tagName);
                    tagNames.add(tagName);
                }
            }

        }
    }


    @FXML
    protected void onBtnSaveClick() throws IOException {

        Alert confirmSave = new Alert(Alert.AlertType.CONFIRMATION);
        confirmSave.setContentText("Confirm Save Changes?");
        confirmSave.initStyle(StageStyle.UTILITY);

        Optional<ButtonType> result = confirmSave.showAndWait();
        if (result.get() == ButtonType.OK){
            addTags();

            String title, desc, finish_date;
            boolean has_done;
            Todo todo;

            title = txtTitle.getText();
            desc = txtDesc.getText();
            finish_date = dateFormat.format(dpFinishDate.getValue());

            if (isEditMode){
                FileHandler fileHandler = new FileHandler();
                Todo read_todo = fileHandler.read(FileDataType.TODO, id);
                has_done = read_todo.getHas_done();
            }else{
                has_done = false;
            }

            if (tagIds.size() == 1){
                todo = new Todo(title,desc,finish_date,tagIds.get(0), has_done);
            }else{
                todo = new Todo(title, desc, finish_date, tagIds, has_done);
            }

            if (isEditMode){
                todo.setId(id);
                todo.saveChanges();

                TodoView_Controller.setTodoID(id);
                rootParent.getScene().setRoot(FXMLLoader.load(getClass().getResource("todo-view.fxml")));

            }else {
                todo.save();
                rootParent.getScene().setRoot(FXMLLoader.load(getClass().getResource("todo-list.fxml")));
            }


        }
    }

    @FXML
    protected void onBtnCancelClick(){
        try {
            if (isEditMode) {
                TodoView_Controller.setTodoID(id);
                rootParent.getScene().setRoot(FXMLLoader.load(getClass().getResource("todo-view.fxml")));
            }else{
                rootParent.getScene().setRoot(FXMLLoader.load(getClass().getResource("todo-list.fxml")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onBtnAddTagClick(){
        String tagName = txtTag.getText();
        if (!tagName.trim().isEmpty()){
            tagNames.add(tagName);
            makeTag(tagName);
        }
    }

    private void makeTag(String tagName){
        HBox hbTag = new HBox();
        hbTag.setBackground(new Background(new BackgroundFill(Color.web("#ffffffcc"), new CornerRadii(5), Insets.EMPTY)));
        hbTag.setPrefHeight(Region.USE_COMPUTED_SIZE);
        hbTag.setPadding(new Insets(4,10,4,10));
        hbTag.setSpacing(10);

        Label lblTag = new Label(tagName);
        lblTag.setWrapText(true);
        lblTag.setPrefHeight(Region.USE_COMPUTED_SIZE);

        Label lblDel = new Label("×");
        lblDel.setTextFill(Color.web("#888"));
        lblDel.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        lblDel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tagNames.remove(tagName);
                hbTags.getChildren().remove(hbTag);
            }
        });

        hbTag.getChildren().addAll(lblTag, lblDel);
        hbTags.getChildren().add(hbTag);
    }

    private void addTags(){
        FileHandler fileHandler = new FileHandler();

        for (String tagName: tagNames) {
            Tag tag = fileHandler.search_exact(FileDataType.TAG, "name", tagName);
            if (tag == null) {
                int last_id = fileHandler.get_last_id(FileDataType.TAG);
                tagIds.add(last_id + 1);

                ArrayList<Integer> todo_id = new ArrayList<>();
                todo_id.add(id);
                Tag new_tag = new Tag(tagName, todo_id);
                new_tag.save();

            } else {
                tagIds.add(tag.getId());
                tag.addTodo_id(id);
                tag.saveChanges();
            }
        }
    }
}
