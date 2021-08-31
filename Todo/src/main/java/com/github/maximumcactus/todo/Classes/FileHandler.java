package com.github.maximumcactus.todo.Classes;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

public class FileHandler {

    public static String MAIN_PATH = "src\\main\\java\\com\\github\\maximumcactus\\todo\\Classes\\";

    String filename = null;
    Type readType = null;

    // READ METHODS
    // -- Read all objects from a JSON file
    public <ReadClass> ArrayList<ReadClass> read_all(FileDataType fileDataType){

        populate_file_details(fileDataType);
        ArrayList<ReadClass> temp_results = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            JsonElement rootNode = JsonParser.parseReader(br);
            JsonArray mainArr = rootNode.getAsJsonArray();

            for (int i = 0; i < mainArr.size(); i++){
                JsonObject readObj = mainArr.get(i).getAsJsonObject();
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();
                ReadClass readResult = gson.fromJson(readObj, readType);
                temp_results.add(readResult);

            }

            //Object[] results = new Object[temp_results.size()];
            //temp_results.toArray(results);

            return temp_results;

        } catch (IOException e) {
            System.out.println(e);
        }

        generate_file(fileDataType);
        return null;
    }

    // -- Read object with selected ID from a JSON file
    public <ReadClass> ReadClass read(FileDataType fileDataType, int id){

        populate_file_details(fileDataType);

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            JsonElement rootNode = JsonParser.parseReader(br);
            JsonArray mainArr = rootNode.getAsJsonArray();

            for (int i = 0; i < mainArr.size(); i++){
                JsonObject readObj = mainArr.get(i).getAsJsonObject();
                int readId = readObj.get("id").getAsInt();

                if (readId == id) {
                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting();
                    Gson gson = builder.create();
                    ReadClass result = gson.fromJson(readObj, readType);
                    return result;

                }

            }

            return null;

        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }

    // EDIT METHODS
    // -- Get the position of object to be edited, then update the object
    public <EditClass> boolean edit(FileDataType fileDataType, int position, EditClass edited_obj){
        populate_file_details(fileDataType);

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            JsonElement rootNode = JsonParser.parseReader(br);
            JsonArray mainArr = rootNode.getAsJsonArray();

            JsonObject toEditObj = mainArr.get(position).getAsJsonObject();
            int toEditObjId = toEditObj.get("id").getAsInt();

            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();

            JsonObject toReplaceObj = (JsonObject) gson.toJsonTree(edited_obj);
            toReplaceObj.addProperty("id",toEditObjId);
            mainArr.set(position, toReplaceObj);

            String new_json = gson.toJson(mainArr);

            PrintWriter printWriter = new PrintWriter(filename);
            printWriter.write(new_json);
            printWriter.close();

            return true;

        } catch (IOException e) {
            System.out.println(e);
        }

        return false;
    }

    // DELETE METHODS
    // -- Get the position of object to be edited, then delete the object
    public <EditClass> boolean delete(FileDataType fileDataType, int position){
        populate_file_details(fileDataType);

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            JsonElement rootNode = JsonParser.parseReader(br);
            JsonArray mainArr = rootNode.getAsJsonArray();
            mainArr.remove(position);

            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();


            String new_json = gson.toJson(mainArr);

            PrintWriter printWriter = new PrintWriter(filename);
            printWriter.write(new_json);
            printWriter.close();

            return true;

        } catch (IOException e) {
            System.out.println(e);
        }

        return false;
    }

    // INSERT METHODS
    // -- Get the latest ID, increment by one and insert a new entry to specific JSON file
    public <AddClass> boolean add(FileDataType fileDataType, AddClass added_obj){
        populate_file_details(fileDataType);
        int last_id = 0;

        // TODO: Handle case where JSON file is empty
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            JsonElement rootNode = JsonParser.parseReader(br);
            JsonArray mainArr = rootNode.getAsJsonArray();

            JsonObject last_obj = mainArr.get(mainArr.size() - 1).getAsJsonObject();
            last_id = last_obj.get("id").getAsInt() + 1;

            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(added_obj);
            jsonObject.addProperty("id", last_id);

            mainArr.add(jsonObject);
            String new_json = gson.toJson(mainArr);

            PrintWriter printWriter = new PrintWriter(filename);
            printWriter.write(new_json);
            printWriter.close();

            return true;

        } catch (IOException e) {
            System.out.println(e);
        }

        return false;
    }

    // SEARCH METHODS
    // -- Get the first object that matches search key
    public <SearchClass, Attribute> SearchClass search(FileDataType fileDataType, String attr_name, Attribute attr_val){

        populate_file_details(fileDataType);

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            JsonElement rootNode = JsonParser.parseReader(br);
            JsonArray mainArr = rootNode.getAsJsonArray();

            for (int i = 0; i < mainArr.size(); i++){
                JsonObject readObj = mainArr.get(i).getAsJsonObject();

                if (attr_val instanceof Integer){
                    int read_val = readObj.get(attr_name).getAsInt();
                    if (read_val == (Integer) attr_val) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();
                        return gson.fromJson(readObj, readType);

                    }

                }else if (attr_val instanceof String){
                    String read_val = readObj.get(attr_name).getAsString();
                    if (read_val.toLowerCase().contains( ( (String) attr_val ).toLowerCase() )) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();
                        return gson.fromJson(readObj, readType);

                    }

                }else if (attr_val instanceof Double){
                    double read_val = readObj.get(attr_name).getAsDouble();
                    if (read_val == (Double) attr_val) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();
                        return gson.fromJson(readObj, readType);

                    }
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }

        return null;

    }// -- Get the first object that matches search key

    // -- Get the first object which matches exactly with search key, for matching string values only
    public <SearchClass, Attribute> SearchClass search_exact(FileDataType fileDataType, String attr_name, Attribute attr_val){

        populate_file_details(fileDataType);

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            JsonElement rootNode = JsonParser.parseReader(br);
            JsonArray mainArr = rootNode.getAsJsonArray();

            for (int i = 0; i < mainArr.size(); i++){
                JsonObject readObj = mainArr.get(i).getAsJsonObject();

                if (attr_val instanceof String){
                    String read_val = readObj.get(attr_name).getAsString();
                    if (read_val.equalsIgnoreCase(( (String) attr_val ))) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();
                        return gson.fromJson(readObj, readType);

                    }

                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }

        return null;

    }

    // -- Get all objects that matches search key
    public <SearchClass, Attribute> ArrayList<SearchClass> search_all(FileDataType fileDataType, String attr_name, Attribute attr_val){
        populate_file_details(fileDataType);
        ArrayList<SearchClass> results = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            JsonElement rootNode = JsonParser.parseReader(br);
            JsonArray mainArr = rootNode.getAsJsonArray();

            for (int i = 0; i < mainArr.size(); i++){
                JsonObject readObj = mainArr.get(i).getAsJsonObject();

                if (attr_val instanceof Integer){
                    int read_val = readObj.get(attr_name).getAsInt();
                    if (read_val == (Integer) attr_val) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();
                        SearchClass result = gson.fromJson(readObj, readType);
                        results.add(result);

                    }

                }else if (attr_val instanceof String){
                    String read_val = readObj.get(attr_name).getAsString();
                    if (read_val.toLowerCase().contains( ( (String) attr_val ).toLowerCase() )) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();
                        SearchClass result = gson.fromJson(readObj, readType);
                        results.add(result);

                    }

                }else if (attr_val instanceof Double) {
                    double read_val = readObj.get(attr_name).getAsDouble();
                    if (read_val == (Double) attr_val) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();
                        SearchClass result = gson.fromJson(readObj, readType);
                        results.add(result);

                    }

                }else if (attr_val instanceof Boolean){
                    boolean read_val = readObj.get(attr_name).getAsBoolean();
                    if (read_val == (Boolean) attr_val) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();
                        SearchClass result = gson.fromJson(readObj, readType);
                        results.add(result);

                    }

                }else if (attr_val instanceof ArrayList){
                    if ( ( (ArrayList) attr_val ).isEmpty() ) {
                        break;
                    }
                    Object attr_val_elem = ( (ArrayList) attr_val ).get(0);

                    if (attr_val_elem instanceof String){
                        String read_val = readObj.get(attr_name).getAsString();
                        for (String val: (ArrayList<String>) attr_val ){
                            if (read_val.toLowerCase().contains( val.toLowerCase() )) {
                                GsonBuilder builder = new GsonBuilder();
                                builder.setPrettyPrinting();
                                Gson gson = builder.create();
                                SearchClass result = gson.fromJson(readObj, readType);
                                results.add(result);
                            }
                        }

                    }else if (attr_val_elem instanceof Integer){
                        if (attr_name.equals("tag")){
                            JsonElement tags_val = readObj.get("tags");

                            for (Integer val: ((ArrayList<Integer>) attr_val)){
                                if (tags_val == null) {
                                    Integer tag_val = readObj.get("tag").getAsInt();
                                    if (tag_val.equals(val)) {
                                        GsonBuilder builder = new GsonBuilder();
                                        builder.setPrettyPrinting();
                                        Gson gson = builder.create();
                                        SearchClass result = gson.fromJson(readObj, readType);
                                        results.add(result);

                                    }

                                }else{
                                    for (int j = 0; j < tags_val.getAsJsonArray().size(); j++){
                                        int tag = tags_val.getAsJsonArray().get(j).getAsInt();
                                        if (tag == val){
                                            GsonBuilder builder = new GsonBuilder();
                                            builder.setPrettyPrinting();
                                            Gson gson = builder.create();
                                            SearchClass result = gson.fromJson(readObj, readType);
                                            results.add(result);
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

            if (!results.isEmpty()){
                return results;
            }


        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }

    // -- Get the position of first occurrence of a JSON object that matches search key
    public <SearchClass, Attribute> int search_position(FileDataType fileDataType, String attr_name, Attribute attr_val){
        populate_file_details(fileDataType);

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            JsonElement rootNode = JsonParser.parseReader(br);
            JsonArray mainArr = rootNode.getAsJsonArray();

            for (int i = 0; i < mainArr.size(); i++){
                JsonObject readObj = mainArr.get(i).getAsJsonObject();

                if (attr_val instanceof Integer){
                    int read_val = readObj.get(attr_name).getAsInt();
                    if (read_val == (Integer) attr_val) {
                        return i;
                    }

                }else if (attr_val instanceof String){
                    String read_val = readObj.get(attr_name).getAsString();
                    if (read_val.equals( (String) attr_val) ) {
                        return i;
                    }

                }else if (attr_val instanceof Double){
                    double read_val = readObj.get(attr_name).getAsDouble();
                    if (read_val == (Double) attr_val) {
                        return i;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }

        return -1;
    }

    // MISC METHODS
    // -- Get the last ID of a JSON Array
    public int get_last_id(FileDataType fileDataType){
        populate_file_details(fileDataType);

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            JsonElement rootNode = JsonParser.parseReader(br);
            JsonArray mainArr = rootNode.getAsJsonArray();

            JsonObject lastObject = mainArr.get(mainArr.size() - 1).getAsJsonObject();
            return lastObject.get("id").getAsInt();

        } catch (IOException e) {
            System.out.println(e);
        }

        return -1;
    }

    // -- Get the file path and class to perform file operations on
    private void populate_file_details(FileDataType fileDataType){
        if (fileDataType == FileDataType.TODO){
            filename = MAIN_PATH + "todo.json";
            readType = Todo.class;
        }else if (fileDataType == FileDataType.TAG){
            filename = MAIN_PATH + "tag.json";
            readType = Tag.class;
        }
    }

    // -- Generate and initialize a file if it's missing
    private void generate_file(FileDataType fileDataType){
        populate_file_details(fileDataType);
        File file = new File(filename);
        try {
            PrintWriter pw = new PrintWriter(file);
            pw.println("[");
            pw.println("]");
            pw.flush();
            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
