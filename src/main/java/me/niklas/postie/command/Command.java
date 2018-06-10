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
public interface Command {

    /**
     * @return The name of the command
     */
    String getName();

    /**
     * @return All aliases, keywords which can be used to call the command, too. In most cases they are abbreviations or synonyms.
     */
    String[] getAliases();

    /**
     * @return The description, a short text which describes what the command does.
     */
    String getDescription();

    /**
     * @return Some examples to show the usage/syntax of the command.
     */
    String[] getExamples();

    /**
     * Processes a {@link Message} in a specific way, e.g. sorting the input.
     *
     * @param message The original {@link Message} sent by the {@link net.dv8tion.jda.core.entities.User}.
     * @param args    The arguments which were recognized, for normal using the {@link CommandBuilder}.
     * @return A {@link Result} which can be used as an answer.
     */
    Result execute(Message message, String[] args);

}
