package com.github.maximumcactus.todo.Classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Todo implements Comparable<Todo>{

    int id;
    String title, desc;
    String end_date;
    ArrayList<Integer> tags;
    int tag;
    boolean has_done;

    public Todo () {}

    public Todo(String title, String desc, String end_date, ArrayList<Integer> tags, boolean has_done) {
        this.title = title;
        this.desc = desc;
        this.end_date = end_date;
        this.tags = tags;
        this.tag = -1;
        this.has_done = has_done;
    }

    public Todo(String title, String desc, String end_date, int tag, boolean has_done) {
        this.title = title;
        this.desc = desc;
        this.end_date = end_date;
        this.tag = tag;
        this.tags = null;
        this.has_done = has_done;
    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public String getShortDesc(int char_count){
        if (desc.length() <= char_count){
            return desc;
        }
        return desc.substring(0, char_count) + "...";
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getEnd_date() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.parse(end_date);
    }

    public String getEnd_date_str(){
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public ArrayList<Integer> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Integer> tags) {
        this.tags = tags;
    }

    public int getTag(){
        return tag;
    }

    public String getTagName(){
        FileHandler fileHandler = new FileHandler();
        Tag tag = (Tag) fileHandler.read(FileDataType.TAG, this.tag);
        return tag.getName();
    }

    public String[] getTagsName(){
        ArrayList<String> temp_tagsName = new ArrayList<>();
        FileHandler fileHandler = new FileHandler();

        for (int tagID: tags){
            Tag tag = (Tag) fileHandler.read(FileDataType.TAG, tagID);
            temp_tagsName.add(tag.getName());
        }
        String[] tagsName = new String[temp_tagsName.size()];
        temp_tagsName.toArray(tagsName);
        return tagsName;
    }

    public boolean getHas_done(){
        return has_done;
    }

    public void setHas_done(){
        has_done = true;
    }

    public void resetHas_done(){
        has_done = false;
    }

    public boolean save(){
        FileHandler fileHandler = new FileHandler();
        return fileHandler.add(FileDataType.TODO, this);
    }

    public boolean saveChanges(){
        FileHandler fileHandler = new FileHandler();
        int position = fileHandler.search_position(FileDataType.TODO, "id", id);
        return fileHandler.edit(FileDataType.TODO, position, this);
    }

    public boolean delete(){
        FileHandler fileHandler = new FileHandler();
        int position = fileHandler.search_position(FileDataType.TODO, "id", id);
        return fileHandler.delete(FileDataType.TODO, position);
    }

    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(this);
    }

    @Override
    public int compareTo(Todo o) {
        return this.id - o.getId();
    }

    public static Comparator<Todo> sortTitle = new Comparator<Todo>() {
        @Override
        public int compare(Todo o1, Todo o2) {
            return o1.getTitle().compareToIgnoreCase(o2.getTitle());
        }
    };

    public static Comparator<Todo> sortEndDate = new Comparator<Todo>() {
        @Override
        public int compare(Todo o1, Todo o2) {
            try {
                Date o1_date = o1.getEnd_date();
                Date o2_date = o2.getEnd_date();

                return o1_date.compareTo(o2_date);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return 0;
        }
    };

    public static Comparator<Todo> sortHasDone = new Comparator<Todo>() {
        @Override
        public int compare(Todo o1, Todo o2) {
            return Boolean.compare(o1.has_done, o2.has_done);
        }
    };

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        // If the object is compared with itself then return true
        if (obj == this) {
            return true;
        }

        /* Check if obj is an instance of Todo or not
          "null instanceof [type]" also returns false */
        if (!(obj instanceof Todo)) {
            return false;
        }

        // typecast obj to Todo so that we can compare data members
        Todo t = (Todo) obj;

        // Compare the data members and return accordingly
        return t.getId() == this.getId();
    }
}
