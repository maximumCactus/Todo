package com.github.maximumcactus.todo;

import com.github.maximumcactus.todo.Classes.FileDataType;
import com.github.maximumcactus.todo.Classes.FileHandler;
import com.github.maximumcactus.todo.Classes.Tag;
import com.github.maximumcactus.todo.Classes.Todo;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.MAX_VALUE;

public class TodoList_Controller implements Initializable {

    FileHandler fileHandler = new FileHandler();
    ArrayList<Integer> tag_query = new ArrayList<>();
    ArrayList<String> finish_date_query = new ArrayList<>();

    ArrayList<Todo> todos = new ArrayList<>();

    @FXML
    private TilePane tpList;

    @FXML
    private VBox vbAllTag, vbFinishDate;

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnSearch, btnAddTodo, btnSortId, btnSortTitle, btnSortFinishDate, btnSortHasDone;

    @FXML
    private Parent rootParent;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        todos = fileHandler.read_all(FileDataType.TODO);
        for (Todo todo: todos) {
            try {
                make_todo_container(todo);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        populate_all_tags();
        populate_finish_dates();
    }

    private void make_todo_container(Todo todo) throws ParseException {
        VBox parent;
        HBox parent_title, parent_desc, parent_end_date, parent_tag;
        Label lbl_title, lbl_desc, lbl_end_date, lbl_tag;

        parent = new VBox();
        parent_title = new HBox();
        parent_desc = new HBox();
        parent_end_date = new HBox();
        parent_tag = new HBox();

        lbl_title = new Label();
        lbl_desc = new Label();
        lbl_end_date = new Label();
        lbl_tag = new Label();

        parent.setPrefWidth(Region.USE_COMPUTED_SIZE);
        parent.setBackground(new Background(new BackgroundFill(Color.web("#ccc"), CornerRadii.EMPTY, Insets.EMPTY)));
        parent.setPadding(new Insets(10));
        parent.setCursor(Cursor.HAND);
        parent.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                parent.setBackground(new Background(new BackgroundFill(Color.web("#bbb"), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });
        parent.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                parent.setBackground(new Background(new BackgroundFill(Color.web("#ccc"), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });
        parent.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    TodoView_Controller.setTodoID(todo.getId());
                    rootParent.getScene().setRoot(FXMLLoader.load(getClass().getResource("todo-view.fxml")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        lbl_title.setWrapText(true);
        lbl_desc.setWrapText(true);
        lbl_end_date.setWrapText(true);
        lbl_tag.setWrapText(true);

        lbl_title.setText(todo.getTitle());
        lbl_desc.setText(todo.getShortDesc(50));
        lbl_end_date.setText(dateFormat.format(todo.getEnd_date()));

        if (todo.getTags() == null) {
            HBox parent_single_tag = new HBox();
            parent_single_tag.setBackground(new Background(new BackgroundFill(Color.web("#eee"), new CornerRadii(5), Insets.EMPTY)));
            parent_single_tag.setPadding(new Insets(4,10,4,10));
            parent_single_tag.setPrefWidth(Region.USE_COMPUTED_SIZE);

            lbl_tag.setText(todo.getTagName());
            parent_single_tag.getChildren().add(lbl_tag);

            parent_tag.getChildren().add(parent_single_tag);

        } else {
            //lbl_tag.setText(String.join(",", todo.getTagsName()));

            parent_tag.setSpacing(5);
            parent_tag.setPrefWidth(Region.USE_COMPUTED_SIZE);

            for (String tagName: todo.getTagsName()){
                HBox parent_single_tag = new HBox();
                parent_single_tag.setBackground(new Background(new BackgroundFill(Color.web("#eee"), new CornerRadii(5), Insets.EMPTY)));
                parent_single_tag.setPadding(new Insets(4,10,4,10));
                parent_single_tag.setPrefWidth(Region.USE_COMPUTED_SIZE);

                Label lbl_single_tag = new Label();
                lbl_single_tag.setText(tagName);
                lbl_single_tag.setWrapText(true);
                parent_single_tag.getChildren().add(lbl_single_tag);

                parent_tag.getChildren().add(parent_single_tag);
            }

        }

        parent_title.getChildren().add(lbl_title);
        parent_desc.getChildren().add(lbl_desc);
        parent_end_date.getChildren().add(lbl_end_date);

        parent.getChildren().addAll(parent_title, parent_desc, parent_end_date, parent_tag);
        parent.setMaxWidth(150);

        tpList.getChildren().add(parent);

    }

    private void populate_all_tags() {
        for (Object tag : fileHandler.read_all(FileDataType.TAG)) {
            HBox parent;
            CheckBox chkTag;

            parent = new HBox();
            chkTag = new CheckBox();

            String tagName = ((Tag) tag).getName();

            chkTag.setId("chkTag_" + ((Tag) tag).getId());
            chkTag.setText(tagName);

            chkTag.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    filterByTag(chkTag, ( (Tag) tag ).getId());
                }
            });

            parent.getChildren().add(chkTag);
            vbAllTag.getChildren().add(parent);
        }
    }

    private void populate_finish_dates() {
        ArrayList<Object> read_all_results = fileHandler.read_all(FileDataType.TODO);

        Set<String> result_set = new LinkedHashSet<String>();

        for (Object todo : read_all_results) {
            result_set.add(((Todo) todo).getEnd_date_str());
        }

        for (String finish_date : result_set) {
            HBox parent;
            CheckBox chkFinishDate;

            parent = new HBox();
            chkFinishDate = new CheckBox();

            chkFinishDate.setText(finish_date);

            chkFinishDate.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    filterByFinishDate(chkFinishDate, finish_date);
                }
            });

            parent.setPrefHeight(Region.USE_COMPUTED_SIZE);

            parent.getChildren().add(chkFinishDate);
            vbFinishDate.getChildren().add(parent);
        }
    }

    private void filterByFinishDate(@NotNull CheckBox chkFinishDate, String finish_date) {
        tpList.getChildren().clear();

        if (chkFinishDate.isSelected()) {
            finish_date_query.add(finish_date);
        } else {
            finish_date_query.remove(finish_date);
        }


        todos = fileHandler.search_all(FileDataType.TODO, "end_date", finish_date_query);
        if (todos == null) {
            todos = fileHandler.read_all(FileDataType.TODO);
        }

        for (Todo todo : todos) {
            try {
                make_todo_container(todo);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    private void filterByTag(@NotNull CheckBox chkTag, int tag_id){
        tpList.getChildren().clear();

        if (chkTag.isSelected()) {
            tag_query.add(tag_id);
        } else {
            tag_query.remove( (Object) tag_id);
        }

        todos = fileHandler.search_all(FileDataType.TODO, "tag", tag_query);
        if (todos == null) {
            todos = fileHandler.read_all(FileDataType.TODO);
        }

        Set<Todo> result_set = new LinkedHashSet<Todo>(todos);

        for (Todo todo : result_set) {
            try {
                make_todo_container(todo);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void sortByFinishDate(){
        tpList.getChildren().clear();
        todos.sort(Todo.sortEndDate);
        for (Todo todo: todos){
            try {
                make_todo_container(todo);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void sortByTitle(){
        tpList.getChildren().clear();
        todos.sort(Todo.sortTitle);
        for (Todo todo: todos){
            try {
                make_todo_container(todo);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void sortByHasDone(){
        tpList.getChildren().clear();
        todos.sort(Todo.sortHasDone);
        for (Todo todo: todos){
            try {
                make_todo_container(todo);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void sortById(){
        tpList.getChildren().clear();
        Collections.sort(todos);
        for (Todo todo: todos){
            try {
                make_todo_container(todo);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void onBtnSearchClick() {
        String searchKey = txtSearch.getText();
        tpList.getChildren().clear();

        ArrayList<Object> search_results = new ArrayList<>();
        search_results = fileHandler.search_all(FileDataType.TODO, "title", searchKey);

        if (search_results == null) {
            Label lblNoResult;
            lblNoResult = new Label();
            lblNoResult.setText("No results");
            tpList.getChildren().add(lblNoResult);

        } else {
            for (Object todo : search_results) {
                try {
                    make_todo_container((Todo) todo);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    protected void onBtnSortFinishDateClick(){
        sortByFinishDate();
    }

    @FXML
    protected void onBtnSortTitleClick(){
        sortByTitle();
    }

    @FXML
    protected void onBtnSortIdClick(){
        sortById();
    }

    @FXML
    protected void onBtnSortHasDoneClick(){
        sortByHasDone();
    }

    @FXML
    protected void onBtnAddTodoClick(){
        try {
            TodoForm_Controller.resetIsEditMode();
            rootParent.getScene().setRoot(FXMLLoader.load(getClass().getResource("todo-form.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
