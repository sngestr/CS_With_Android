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

import java.util.HashMap;
import java.util.Set;

/*This is an implementation of a single node in our trie.*/

public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    //add a string to the trie.
    public void add(String s) {
        if (s.isEmpty()) {
            this.isWord = true;
        } else {
            //does not contain the key; make a new children
            if (!children.containsKey(s.substring(0, 1))) {
                children.put(Character.toString(s.charAt(0)), new TrieNode());
            }
            TrieNode temp = children.get(s.substring(0, 1));    //put it into a trie node
            temp.add(s.substring(1));
        }
    }

    //checks whether the given word is in the trie.
    public boolean isWord(String s) {
        if (s.isEmpty()) {
            return isWord;
        } else {
            if (children.containsKey(s.substring(0, 1))) {
                TrieNode temp = children.get(s.substring(0, 1));
                temp.isWord(s.substring(1));
            }
        }
        return false;
    }

    //will randomly select a descendant of the node that represents the given prefix.
    public String getAnyWordStartingWith(String s) {
        String word = s;    //get the prefix
        TrieNode t = null;
        for (int k = 0; k < s.length(); k++){
            if(children.containsKey(s.substring(0,1))){
                t = children.get(s.substring(0,1));
                children = t.children;
            } else {
                return null;
            }
        }

        Set keySet = t.children.keySet();

        for(int i = 0; i < keySet.toArray().length; i++){
            TrieNode temp = t.children.get(keySet.toArray()[i]);
            if(temp.isWord){
                word += keySet.toArray()[i];
                break;
            }
        }

        return word;
    }

    //will attempt to pick a descendant of the given prefix that is likely to lead to the computer winning.
    public String getGoodWordStartingWith(String s) {
        return null;
    }
}
