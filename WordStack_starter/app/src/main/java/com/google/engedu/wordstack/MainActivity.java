/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.wordstack;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 3;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1, word2;
    private String enteredWord1 = "", enteredWord2 = "";
    private Stack<LetterTile> placedTiles = new Stack<LetterTile>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();
                //add word of same length to the array list
                if(word.length() == WORD_LENGTH){
                    words.add(word);
                }
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        View word1LinearLayout = findViewById(R.id.word1);
        //word1LinearLayout.setOnTouchListener(new TouchListener());
        word1LinearLayout.setOnDragListener(new DragListener());
        View word2LinearLayout = findViewById(R.id.word2);
        //word2LinearLayout.setOnTouchListener(new TouchListener());
        word2LinearLayout.setOnDragListener(new DragListener());
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }
                //push the touched title onto placedTiles
                placedTiles.push(tile);
                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);
                    if (stackedLayout.empty()) {
                        TextView messageBox = (TextView) findViewById(R.id.message_box);
                        messageBox.setText(word1 + " " + word2);
                    }
                    //push the draggable tile onto the stack
                    placedTiles.push(tile);

                    //add the entered letter from the corresponding enteredword based on it's layout
                    ViewGroup group = (ViewGroup) tile.getParent();
                    LinearLayout word1Layout = (LinearLayout) findViewById(R.id.word1);
                    LinearLayout word2Layout = (LinearLayout) findViewById(R.id.word2);

                    if(group.getId() == word1Layout.getId()){
                        enteredWord1 = enteredWord1 + tile.getText();
                    }
                    if(group.getId() == word2Layout.getId()){
                        enteredWord2 = enteredWord2 + tile.getText();
                    }

                    //if the words are all in the box; check if those are valid words
                    if(stackedLayout.empty()){
                        findIfValidWord();
                    }
                    return true;
            }
            return false;
        }
    }

    public boolean onStartGame(View view) {
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        TextView otherResult = (TextView) findViewById(R.id.other_words_box);

        stackedLayout.clear();
        messageBox.setText("Game started");
        otherResult.setText("");

        //clear the strings
        String scrambledWord = "";
        enteredWord1 = "";
        enteredWord2 = "";

        //clear layout view
        LinearLayout word1LinearLayout = (LinearLayout) findViewById(R.id.word1);
        LinearLayout word2LinearLayout = (LinearLayout) findViewById(R.id.word2);

        word1LinearLayout.removeAllViews();
        word2LinearLayout.removeAllViews();

        //clear stackLayout
        stackedLayout.clear();

        //clear placedTile stack
        placedTiles.clear();

        //randomly pick two words from the array of same length words
        word1 = words.get((int)Math.ceil(Math.random()*words.size()));
        word2 = words.get((int)Math.ceil(Math.random()*words.size()));

        //check if it's the same word; change word2 if same
        while(word1.equals(word2)){
            word2 = words.get((int)Math.ceil(Math.random()*words.size()));
        }

        //Scramble the letters in word1 and word2 to make a scrambled word
        int counter1 = 0;
        int counter2 = 0;

        //randomly choose a word to get a letter from
        while(counter1 < WORD_LENGTH && counter2 < WORD_LENGTH){
            if(Math.random() < 0.5){
                scrambledWord = scrambledWord + word1.charAt(counter1);
                counter1++;
            } else {
                scrambledWord = scrambledWord + word2.charAt(counter2);
                counter2++;
            }
        }

        //Check if we got all the letters into the scrambledWord
        if(counter1 < WORD_LENGTH){
            for(int i = counter1; i < WORD_LENGTH; i++){
                scrambledWord = scrambledWord + word1.charAt(i);
            }
        }
        if(counter2 < WORD_LENGTH){
            for(int i = counter2; i < WORD_LENGTH; i++){
                scrambledWord = scrambledWord + word2.charAt(i);
            }
        }

        //make each letter into a letter tile and push into stack
        for(int j = scrambledWord.length()-1 ; j >= 0; j--){
            LetterTile tile = new LetterTile(this, scrambledWord.charAt(j));
            stackedLayout.push(tile);
        }

        return true;
    }

    public boolean onUndo(View view) {
        if(!placedTiles.isEmpty()){
            TextView otherResult = (TextView) findViewById(R.id.other_words_box);

            ViewGroup group = (ViewGroup) placedTiles.peek().getParent();

            //move back onto stackedLayouts
            LetterTile popped = placedTiles.peek();
            popped.moveToViewGroup(stackedLayout);

            //delete the popped letter from the corresponding enteredword based on it's layout
            LinearLayout word1Layout = (LinearLayout) findViewById(R.id.word1);
            LinearLayout word2Layout = (LinearLayout) findViewById(R.id.word2);

            if(group.getId() == word1Layout.getId()){
                enteredWord1 = enteredWord1.substring(0, enteredWord1.length()-1);
                otherResult.setText(enteredWord1);
            }
            if(group.getId() == word2Layout.getId()){
                enteredWord2 = enteredWord2.substring(0, enteredWord2.length()-1);
            }
        }
        return true;
    }

    //checks if the user input is a valid word are also in the dictionary
    private void findIfValidWord(){
        TextView otherResult = (TextView) findViewById(R.id.other_words_box);

        if(word1.equals(enteredWord1) && word2.equals(enteredWord2)){
            otherResult.setText("You got it right! :D");
        } else if(words.contains(enteredWord1) && words.contains(enteredWord2)){
            otherResult.setText(enteredWord1 + " and " + enteredWord2 + " are also words in the dictionary!");
        } else if (words.contains(enteredWord1)){
            otherResult.setText(enteredWord1 + " is a word in the dictionary, but " + enteredWord2 + " is not.");
        } else if (words.contains(enteredWord2)){
            otherResult.setText(enteredWord2 + " is a word in the dictionary, but " + enteredWord1 + " is not.");
        } else {
            otherResult.setText(enteredWord1 + " and " + enteredWord2 + " are not a words in the dictionary.");
        }
    }
}
