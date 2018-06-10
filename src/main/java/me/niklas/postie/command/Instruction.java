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

package me.niklas.postie.command;

import net.dv8tion.jda.core.entities.Message;

/**
 * Created by Niklas on 05.06.2018 in postie
 */
public class Instruction {

    private final String command;
    private final String[] args;
    private final Message message;

    Instruction(String command, String[] args, Message message) {
        this.command = command;
        this.args = args;
        this.message = message;
    }

    /**
     * @return The {@link Command} name, which could be an alias in some cases, too.
     */
    public String getCommand() {
        return command;
    }

    /**
     * @return The Parameters given by the {@link net.dv8tion.jda.core.entities.User}s input.
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * @return The original {@link Message} by the {@link net.dv8tion.jda.core.entities.User}.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @return All input from the {@link Message}, excluding the mention.
     */
    public String getAllInput() {
        return command + " " + String.join(" ", args).trim();
    }
}
