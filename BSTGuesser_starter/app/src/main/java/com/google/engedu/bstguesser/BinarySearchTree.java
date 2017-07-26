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

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.jar.Pack200;

/*
 * Wrapper class for the tree. Most of the actual work is handled by the TreeNode
 * class below so a lot of these are wrappers that call the recursive method on TreeNode.
 */

public class BinarySearchTree {
    private TreeNode root = null;

    public BinarySearchTree() {
    }

    //Inserts a value into the tree. Special handler for empty tree case and passes the rest of the work to TreeNode.
    public void insert(int value) {
        if (root == null) {
            root = new TreeNode(value);     //create new tree
            return;
        } else {
            root.insert(value);             //add node to existing tree
        }
    }

    //Wrapper that causes the nodes to position themselves on the screen.
    public void positionNodes(int width) {
        if (root != null)
            root.positionSelf(0, width, 0);
    }

    //Wrapper.
    public void draw(Canvas c) {
        root.draw(c);
    }

    //Wrapper.
    public int click(float x, float y, int target) {
        return root.click(x, y, target);
    }

    //Helper that finds the node with a given value in the tree.
    //find the node that they were supposed to click.
    private TreeNode search(int value) {
        TreeNode current = root;
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        return current;
    }

    //Fine the node of the specified value and mark it as invalid in the UI.
    public void invalidateNode(int targetValue) {
        TreeNode target = search(targetValue);
        target.invalidate();
    }
}
