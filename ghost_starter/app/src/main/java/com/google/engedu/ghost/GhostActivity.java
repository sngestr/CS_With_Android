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

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    private TextView ghost_text;
    private TextView game_status;
    private Button challenge_btn;
    private Button reset_btn;

    private SimpleDictionary simpleDictionary;
    private FastDictionary fastDictionary;

    @Override
    //onCreateOptionsMenu and onOptionsItemSelected which you will not need to edit
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();

        ghost_text = (TextView) findViewById(R.id.ghostText);
        game_status = (TextView) findViewById(R.id.gameStatus);
        challenge_btn = (Button) findViewById(R.id.challenge_btn);
        reset_btn = (Button) findViewById(R.id.reset_btn);

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStart(view);
            }
        });

        challenge_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                challenge_handler();
            }
        });

        //initializing the dictionary by loading the content of words.txt file
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("words.txt");
            fastDictionary = new FastDictionary(inputStream);
            simpleDictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Log.e("cannot load words", e.getMessage());
        }

        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     *
     * @param view
     * @return true
     */

    //the handler for the Reset button but also called from onCreate to determine whether the user or the computer goes first.
    public boolean onStart(View view) {
        challenge_btn.setEnabled(true); //renable challenge button
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    // the helper that handles the computer's turn. Called from onStart
    private void computerTurn() {
        challenge_btn.setEnabled(false);    //disable challenge button

        TextView label = (TextView) findViewById(R.id.gameStatus);
        // Do computer turn stuff then make it the user's turn again
        String currentWord = ghost_text.getText().toString();

        //when it's empty; computer choose a random letter
        if (currentWord.equals("")) {
            Random random = new Random();
            char c = (char) (random.nextInt(26) + 'a');
            currentWord = currentWord + c;
            ghost_text.setText(currentWord);

        } else {
            //Check if the fragment is a word with at least 4 characters. If so declare victory by updating the game status
            if (simpleDictionary.isWord(currentWord) && currentWord.length() <= 4) {
                label.setText("WORD IS VALID! PLAYER LOSES.");            //declare winner
            } else {
                String possible_word = simpleDictionary.getAnyWordStartingWith(currentWord); //Use the dictionary's getAnyWordStartingWith method to get a possible longer word
                //If such a word doesn't exist (method returns null), challenge the user's fragment and declare victory (you can't bluff this computer!)
                if (possible_word == null && !currentWord.equals("")) {
                    label.setText("CANNOT FORM A WORD WITH THIS! COMPUTER WINS! :(");
                    challenge_btn.setEnabled(false);    //disable challenge button
                    return;
                } else {
                    //If such a word does exist; add the next letter of it to the fragment
                    if (!currentWord.equals("")) {
                        String letter = possible_word.substring(currentWord.length(), currentWord.length() + 1);  //get the last letter of the possible word
                        currentWord = currentWord + letter; //add the letter to the current word
                        ghost_text.setText(currentWord);
                    }
                }
            }
        }
        userTurn = true;
        challenge_btn.setEnabled(true); //enable challenge button
        label.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     *
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() > 30 && event.getKeyCode() < 55) {               //check if it's a char key
            char pressedKey = (char) event.getUnicodeChar();
            ghost_text.setText(ghost_text.getText() + String.valueOf(pressedKey)); //add letter to frag

            //indicate if valid word; for testing purposes
            if (simpleDictionary.isWord(ghost_text.getText().toString())) {
                game_status.setText("this is a word");
            } else {
                game_status.setText("this is not a word");
            }

            //invokes the computer turn
            computerTurn();
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    private void challenge_handler() {
        challenge_btn.setEnabled(false);    //disable challenge button
        String currentWord = ghost_text.getText().toString();   //gets the current word fragment

        //check victory; user wins if the current word is at least 4 letters long and is valid
        if (currentWord.length() >= 4 && simpleDictionary.isWord(currentWord)) {
            game_status.setText("USER WINS! IT'S A VALID WORD! :D");
        } else {
            //check if there is a possible word that can be form with the fragment, comp wins if there exist a possible word
            String possibleWord = simpleDictionary.getAnyWordStartingWith(currentWord);
            if (possibleWord != null) {
                game_status.setText("COMPUTER WINS! POSSIBLE WORD: " + possibleWord + " D:");
            } else {
                game_status.setText("USER WINS! NO POSSIBLE WORD CAN BE FORMED. ^ u ^/*");
            }
        }
    }
}
