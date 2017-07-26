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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
This is an implementation of our dictionary that stores the words in a trie.
The interface is the same as SimpleDictionary and the given implementation is
complete as all the actual functionality goes into the TrieNode class.
 */

public class FastDictionary implements GhostDictionary {

    private TrieNode root;

    public FastDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        root = new TrieNode();
        String line = null;
        while ((line = in.readLine()) != null) {
            String word = line.trim();
            //add all words that has at least length 4
            if (word.length() >= MIN_WORD_LENGTH)
                root.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return root.isWord(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        return root.getAnyWordStartingWith(prefix);
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        return root.getGoodWordStartingWith(prefix);
    }
}
