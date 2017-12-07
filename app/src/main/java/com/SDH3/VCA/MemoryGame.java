package com.SDH3.VCA;

import java.util.ArrayList;
import java.util.Arrays;

public class MemoryGame {
    private ArrayList<String> fruits;
    private ArrayList<String> vehicles;
    private ArrayList<String> buildings;
    private ArrayList<String> correctWords;
    private ArrayList<String> gameWords;
    private int level;

    public MemoryGame(){
        correctWords = new ArrayList<>();
        gameWords = new ArrayList<>();
        fruits = new ArrayList<>(Arrays.asList(
                "APPLE", "ORANGE", "PEAR", "BANANA", "MANGO", "STRAWBERRY", "GRAPE", "PEACH"));
        vehicles = new ArrayList<>(Arrays.asList(
                "CAR", "TRUCK", "BICYCLE", "MOTORBIKE", "PLANE", "HELICOPTER", "BOAT", "YACHT"));
        buildings = new ArrayList<>(Arrays.asList(
                "HOUSE", "SHED", "HOSPITAL", "BLOCK", "STATION", "MANSION", "SHOP", "SCHOOL"));
        level = 1;
    }

    public void init(){
    }

    public String getGameWords(){
        String str = "";
        for(int a = 0; a < gameWords.size(); a++) {
            str += gameWords.get(a);
            if(a != gameWords.size()-1)
                str += ", ";
        }
        return str;
    }

    public String getCorrectWords(){
        String str = "";
        for(int a = 0; a < correctWords.size(); a++) {
            str += correctWords.get(a);
            if(a != correctWords.size()-1)
                str += ", ";
        }
        return str;
    }

    public boolean checkWord(String str){
        boolean found = false;
        boolean entered = false;
        for(int a = 0; a < gameWords.size(); a++){
            if(gameWords.get(a).equals(str.toUpperCase())) {
                entered = isEntered(str.toUpperCase(), true);
                if(!entered)
                    found = true;
                correctWords.add(str.toUpperCase());
                a = gameWords.size();
            }
        }
        return found;
    }

    public boolean isEntered(String str, boolean mode){
        boolean entered = false;
        if(mode){
            for (int a = 0; a < correctWords.size(); a++) {
                if (correctWords.get(a).equals(str))
                    entered = true;
            }
        }
        else{
            for (int a = 0; a < gameWords.size(); a++) {
                if (gameWords.get(a).equals(str))
                    entered = true;
            }
        }
        return entered;
    }

    public void launchGame(){
        int arr = 0;
        int word = 0;
        for(int a = 0; a < level*2; a++){
            arr = (int)(Math.random() * 3);
            word = (int)(Math.random() * 8);
            if(arr == 0) {
                if(!isEntered(fruits.get(word), false))
                    gameWords.add(fruits.get(word));
                else
                    a--;
            }
            else if(arr == 1) {
                if(!isEntered(vehicles.get(word), false))
                    gameWords.add(vehicles.get(word));
                else
                    a--;
            }
            else if(arr == 2) {
                if(!isEntered(buildings.get(word), false))
                    gameWords.add(buildings.get(word));
                else
                    a--;
            }
        }
    }

    public String getLevel(){
        return "Level " + level;
    }

    public int getLevelInt(){
        return level;
    }

    public boolean isWon(){
        if(correctWords.size() == gameWords.size())
            return true;
        else return false;
    }

    public boolean nextLevel(){
        if(level < 4) {
            level++;
            correctWords = new ArrayList<>();
            gameWords = new ArrayList<>();
            launchGame();
            return true;
        }
        else return false;
    }

    public void restartLevel(){
        correctWords = new ArrayList<>();
        gameWords = new ArrayList<>();
        launchGame();
    }
}
