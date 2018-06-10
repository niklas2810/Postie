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

package me.niklas.postie.manager;

import me.niklas.postie.command.Command;
import me.niklas.postie.command.Result;
import net.dv8tion.jda.core.entities.Message;

/**
 * Created by Niklas on 06.06.2018 in postie
 */
public class StandardsManager {

    /**
     * @param origin The original {@link Message}
     * @return The default {@link Result} when a command was not found.
     */
    public Result getCommandNotFoundResult(Message origin) {
        return new Result("Not found!", "This command was not found.", origin);
    }


    /**
     * @param command The {@link Command} you want to show the examples for.
     * @param origin  The original {@link Message}
     * @return Some examples to show how to use the {@link Command}, displayed as a {@link Result}.
     */
    public Result getExamples(Command command, Message origin) {
        return new Result("How to use", String.join("\n", command.getExamples()).trim(), origin);
    }
}
