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

package me.niklas.postie.comparator;

import me.niklas.postie.command.Command;

import java.util.Comparator;

/**
 * Created by Niklas on 07.06.2018 in postie
 */
public class CommandComparator implements Comparator<Command> {

    /**
     * Compares two {@link Command}s by name.
     * Used in the {@link me.niklas.postie.manager.CommandManager}.
     *
     * @param first  The first {@link Command}.
     * @param second The second {@link Command}.
     * @return Which should be sorted higher.
     */
    @Override
    public int compare(Command first, Command second) {
        return first.getName().compareTo(second.getName());
    }
}
