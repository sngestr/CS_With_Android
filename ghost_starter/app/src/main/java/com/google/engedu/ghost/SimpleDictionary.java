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

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    // It simply copies the words from the files into the ArrayList
    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while ((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
                words.add(line.trim());
        }
    }

    @Override
    // checks whether the given word is in the ArrayList.
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    // will naively use binary search to look for some word that starts with the given prefix.
    public String getAnyWordStartingWith(String prefix) {
        if (prefix.isEmpty()) {
            return null;
        } else {
            String word = binary_search(prefix);
            return word;
        }
    }

    @Override
    // will still use binary search but will try to make a smarter word selection than getAnyWordStartingWith
    public String getGoodWordStartingWith(String prefix) {
        String selected = null;
        return selected;
    }

    //finds the index of a word with the keyValue as the prefix
    private String binary_search(String keyValue) {
        String dictionaryWord;

        //declare the start and end index of the dictionary
        int startIndex = 0;
        int endIndex = words.size() - 1;

        //loop until the start index passes the end index
        while (endIndex >= startIndex) {
            int middleIndex = (startIndex + endIndex) / 2;  //find the middle index
            dictionaryWord = words.get(middleIndex);    //gets the word at the middle index

            if (dictionaryWord.startsWith(keyValue)) {    //if the word starts with the prefix; return
                return dictionaryWord;
            }

            //finding the section of the prefix in the dictionary
            if (dictionaryWord.compareTo(keyValue) < 0) { //dictionary word is lexicographically greater than the prefix (less than 0, you didn't pass the prefix)
                startIndex = middleIndex + 1;
            } else {
                endIndex = middleIndex - 1;             //dictionary word is lexicographically less than the prefix (greater than 0, you pass the prefix)
            }
        }
        return null;
    }
}
