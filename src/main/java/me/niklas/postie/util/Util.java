/*
 *
 *      Copyright 2018 Niklas Arndt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package me.niklas.postie.util;

/**
 * Created by Niklas on 08.06.2018 in postie
 * Contains basic utility functions which are not associated with a specific bot feature.
 */
public class Util {

    /**
     * Determines whether the input is a Integer.
     *
     * @param input The String which could be an Integer.
     * @return Whether it is an integer.
     */
    public static boolean isInteger(String input) {
        try {
            Integer.valueOf(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
