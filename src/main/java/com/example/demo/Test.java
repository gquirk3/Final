package com.example.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

public class Test extends Application{
    Button button;
    String[] textArray;
    ArrayList<Integer> list = new ArrayList<>();
    Map<String, Integer> freqMap = new HashMap<>();
    LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
    String text = "";  // variable to store the poem
    static TextArea textArea;
    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Text Analyzer - The Raven");
            button = new Button();
            button.setText("Calculate Word Count");

            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {



                    Path fileName = Path.of("poem.txt");
                    try {
                        text = Files.readString(fileName);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    try {
                        post();
                        get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }
            });


            textArea = new TextArea();
            textArea.setPrefHeight(700);
            FlowPane layout = new FlowPane();
            layout.getChildren().add(button);
            layout.getChildren().add(textArea);
            Scene scene = new Scene(layout,400,400);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        launch(args);
    }

    // retrieves data from website as previously done and then enters it into a database
    public void post() throws Exception
    {
        String text = "";  // variable to store the poem
        Path fileName = Path.of("poem.txt");
        try {
            text = Files.readString(fileName);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // cleaning the data to get an accurate word count by
        // removing punctuation and changing everything to lowercase

        convertToLowerCase(text);

        // creating three collections
        // 2 hashmaps - one to get frequency of each word - one to hold the sorted words
        // 1 list to do the sorting/ranking top to bottom




        // iterates through sorted has table and outputs word and count to the database
        // from highest count to lowest - top 20
        int count = 1;  // variable to count to 20
        String key = "mistake";
        int value = 0;
        try {
            Connection con = getConnection();

            for (Map.Entry<String, Integer> e: sortedMap.entrySet())
            {

                if (count < 21)
                {
                    key = e.getKey();
                    value = e.getValue();
                    PreparedStatement posted = con.prepareStatement("INSERT INTO word (words, count) VALUES ('"+key+"', "+value+")");
//	        	System.out.println(count + ". " + e.getKey() + " " + e.getValue());

                    posted.executeUpdate();
                    count += 1;
                }

            }
        }catch(Exception e){
            System.out.println("Can't insert duplicate words");
        }
        finally {
            System.out.println("Insert Completed.");
        }

    }
    // method that gets data from the database
    public static ArrayList<String> get() throws Exception
    {
        try {  // establish connection
            Connection con = getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT * FROM word");  //selects all data

            ResultSet result = statement.executeQuery();
            // outputs data to console
            while(result.next()) {
                textArea.appendText(result.getString("key") + ". " + result.getString("words") + " - " + result.getString("count") +"\n");
                System.out.print(result.getString("key") + ".");
                System.out.print(" ");
                System.out.print(result.getString("words"));
                System.out.print(" ");
                System.out.println(result.getString("count"));
            }
            System.out.println("All records have been selected.");

        }catch(Exception e) {System.out.println(e);}
        return null;
    }
    // method to establish connection to the database
    public static Connection getConnection() throws Exception
    {
        try
        {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/wordOccurences";
            String username = "root";
            String password = "root";
            Class.forName(driver);

            Connection conn = DriverManager.getConnection(url,username,password);
            System.out.println("Connected.");
            return conn;
        }catch(Exception e) {System.out.println(e);}


        return null;
    }
    public void convertToLowerCase(String str2)
    {
        textArray = str2.toLowerCase().split("\\W+");
        // loop to iterate through clean data
        // if word exists - ups the count by 1
        // if word doesn't exist - adds word and sets count to 1
        for (String s : textArray)
        {
            if (freqMap.containsKey(s)) {
                Integer count = freqMap.get(s);
                freqMap.put(s, count + 1);
            } else {
                freqMap.put(s, 1);
            }

        }
        // adds values to list
        for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
            list.add(entry.getValue());
        }
        // sorts list
        Collections.sort(list, new Comparator<Integer>() {
            public int compare(Integer str, Integer str1) {
                return (str1).compareTo(str);
            }
        });
        // uses list and keys from sorted data and
        // adds to new hashtable
        for (Integer str : list) {
            for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
                if (entry.getValue().equals(str)) {
                    sortedMap.put(entry.getKey(), str);
                }
            }
        }
    }
}

