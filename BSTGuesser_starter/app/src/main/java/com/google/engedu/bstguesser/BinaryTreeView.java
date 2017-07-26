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

package com.google.engedu.bstguesser;

import android.content.Context;
import android.graphics.Canvas;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class BinaryTreeView extends View {

    public static final int TREE_SIZE = 20;
    private BinarySearchTree tree = null;
    private ArrayList<Integer> searchSequence = null;
    private int searchPosition;
    private TextView textView;

    public BinaryTreeView(Context context, TextView textView) {
        super(context);
        this.textView = textView;
    }

    //Inserts the nodes into the tree and generates the order in which the user will have to guess the nodes.
    public void initialize() {
        tree = new BinarySearchTree();
        for (int value : generateRandomSequence(TREE_SIZE)) {
            tree.insert(value);
        }
        tree.positionNodes(this.getWidth());
        searchSequence = generateRandomSequence(TREE_SIZE);
        searchPosition = 0;
        updateMessage();
        invalidate();
    }

    //Helper for the above. Generates a randomly ordered list of the number 1 through 20.
    private ArrayList<Integer> generateRandomSequence(int size) {
        ArrayList<Integer> numbers = new ArrayList<>(size);
        for (int i = 0 ; i < size; i++) {
            numbers.add(i+1);
        }
        Collections.shuffle(numbers);
        return numbers;
    }

    @Override
    //Passes the draw call to the BinarySearchTree object.
    public void onDraw(Canvas canvas) {
        if (tree != null) {
            tree.draw(canvas);
        }
    }

    //Updates the text field at the top of the UI with the desired node.
    private void updateMessage() {
        if (searchPosition < searchSequence.size())
            textView.setText("Looking for node " + searchSequence.get(searchPosition));
        else
            textView.setText("Done!");
    }

    //Handler for user touch. Find the node that was tapped by the user and reveals the correct node if the user chose the wrong node.
    public boolean onTouchEvent(MotionEvent event) {
        if (tree != null && searchPosition < searchSequence.size()) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    int targetValue = searchSequence.get(searchPosition);
                    int hitValue = tree.click(event.getX(), event.getY(), targetValue);
                    if (hitValue != -1) {
                        invalidate();
                        if (hitValue != targetValue) {
                            tree.invalidateNode(targetValue);
                        }
                        searchPosition++;
                        updateMessage();
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }
}
