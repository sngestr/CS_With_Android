package com.example.scamesdice.scamesdice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int user_overall_score = 0;
    private int user_turn_score = 0;
    private int computer_overall_score = 0;
    private int computer_turn_score = 0;

    private TextView score_status;
    private ImageView dice_image;

    private Button roll_btn;
    private Button hold_btn;
    private Button reset_btn;

    private Handler timerHandler = new Handler();
    private Runnable computerTurn = new Runnable() {
        @Override
        public void run() {
            //Disable the roll and hold buttons
            enable_roll_and_hold(false);

            int dice_num = get_random_dice_number();
            switch_dice_image(dice_num);

            if(dice_num == 1){
                computer_turn_score = 0;
                score_status.setText("Your score: "+user_overall_score+" Computer score: "+ computer_overall_score + " Computer rolled a one.");
                enable_roll_and_hold(true);
                return;
            } else {
                computer_turn_score+=dice_num;
                score_status.setText("Your score: "+user_overall_score+" Computer score: "+ computer_overall_score + " Computer turn score: " + computer_turn_score);
            }

            //computer decides to roll again
            if(computer_turn_score < 20){
                timerHandler.postDelayed(this, 500);            //recursively call run() again with delay
            } else {
                computer_overall_score+=computer_turn_score;    //update computer's overall score
                computer_turn_score = 0;                        //reset turn score
                score_status.setText("Your score: "+user_overall_score+" Computer score: "+ computer_overall_score +  " Computer holds");

                if(check_score_for_winner()){
                    return;
                }

                //re-enable buttons
                enable_roll_and_hold(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score_status = (TextView) findViewById(R.id.scoreStatus);
        dice_image = (ImageView) findViewById(R.id.dice_img_view);

        roll_btn = (Button) findViewById(R.id.roll_btn);
        hold_btn = (Button) findViewById(R.id.hold_btn);
        reset_btn = (Button) findViewById(R.id.reset_btn);

        //roll button is clicked
        roll_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int dice_number = get_random_dice_number();                //gets a number from 1-6
                switch_dice_image(dice_number);                            //switch the image of the dice

                if(dice_number == 1){
                    user_turn_score = 0;                                  //reset turn score to 0
                    score_status.setText("Your score: "+user_overall_score+" Computer score: " + computer_overall_score + " Your turn score: " + user_turn_score);
                    timerHandler.postDelayed(computerTurn, 500);
                } else {
                    user_turn_score += dice_number;                        //add to turn score
                    score_status.setText("Your score: "+user_overall_score+" Computer score: " + computer_overall_score + " Your turn score: " + user_turn_score);
                }
            }
        });

        //reset button is clicked
        reset_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //enable roll and hold button
                enable_roll_and_hold(true);

                //resets the scoreboard
                reset_overall_score();
                reset_turn_score();

                score_status.setText("Your score: "+user_overall_score+" Computer score: " + computer_overall_score + " Your turn score: " + user_turn_score);
            }
        });

        //hold button is clicked
        hold_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_overall_score+=user_turn_score;        //update user overall score
                user_turn_score = 0;                        //update user turn score

                score_status.setText("Your score: "+user_overall_score+" Computer score: " + computer_overall_score);           //update label
                if(check_score_for_winner()){
                    return;
                }

                timerHandler.postDelayed(computerTurn, 500);
            }
        });
    }

    //returns a random dice number
    private int get_random_dice_number(){
        return (int) Math.ceil(Math.random()*6);
    }

    //switch the image of the dice to the corresponding dice number
    private void switch_dice_image(int dice_num){
        //set dice image
        switch(dice_num){
            case 1:
                dice_image.setImageResource(R.drawable.dice1);
                break;
            case 2:
                dice_image.setImageResource(R.drawable.dice2);
                break;
            case 3:
                dice_image.setImageResource(R.drawable.dice3);
                break;
            case 4:
                dice_image.setImageResource(R.drawable.dice4);
                break;
            case 5:
                dice_image.setImageResource(R.drawable.dice5);
                break;
            case 6:
                dice_image.setImageResource(R.drawable.dice6);
                break;
        }
    }

    private boolean check_score_for_winner(){
        if(user_overall_score >= 100){
            enable_roll_and_hold(false);
            reset_overall_score();
            score_status.setText("Your score: "+user_overall_score+" Computer score: " + computer_overall_score + " User wins! :D");
            return true;
        } else if (computer_overall_score >= 100){
            enable_roll_and_hold(false);
            reset_overall_score();
            score_status.setText("Your score: "+user_overall_score+" Computer score: " + computer_overall_score + " Computer wins! D:");
            return true;
        } else {
            return false;
        }
    }

    private void enable_roll_and_hold(boolean enable){
        if(enable){
            roll_btn.setEnabled(true);
            hold_btn.setEnabled(true);
        } else {
            roll_btn.setEnabled(false);
            hold_btn.setEnabled(false);
        }
    }

    private void reset_overall_score(){
        user_overall_score = 0;
        computer_overall_score = 0;
    }

    private void reset_turn_score(){
        user_turn_score = 0;
        computer_turn_score = 0;
    }
}
