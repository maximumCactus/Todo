package com.github.maximumcactus.todo.Classes;

import java.util.ArrayList;

public class Tag {
    int id;
    String name;
    ArrayList<Integer> todo_ids;

    public Tag(int id, String name, ArrayList<Integer> todo_ids) {
        this.id = id;
        this.name = name;
        this.todo_ids = todo_ids;
    }

    public Tag(String name, ArrayList<Integer> todo_ids){
        this.name = name;
        this.todo_ids = todo_ids;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Integer> getTodo_ids() {
        return todo_ids;
    }

    public void addTodo_id(int todo_id){
        if (!todo_ids.contains(todo_id)){
            todo_ids.add(todo_id);
        }
    }

    public boolean save(){
        FileHandler fileHandler = new FileHandler();
        return fileHandler.add(FileDataType.TAG, this);
    }

    public boolean saveChanges(){
        FileHandler fileHandler = new FileHandler();
        int pos = fileHandler.search_position(FileDataType.TAG, "id", id);
        return fileHandler.edit(FileDataType.TAG, pos, this);
    }
}
