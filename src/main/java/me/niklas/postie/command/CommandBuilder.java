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

import me.niklas.postie.exception.BotNotMentionedException;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;

/**
 * Created by Niklas on 05.06.2018 in postie
 */
public class CommandBuilder {

    /**
     * Builds an {@link Instruction}
     *
     * @param message The original {@link Message} object, probably from {@link me.niklas.postie.listener.MessageReceiveListener}
     * @return The {@link Instruction}
     * @throws BotNotMentionedException If {@link me.niklas.postie.core.Postie} was not mentioned in the {@link Message}
     */
    public static Instruction build(Message message) throws BotNotMentionedException {
        if (!message.getContentDisplay().startsWith("@" + message.getGuild().getSelfMember().getEffectiveName())) {
            throw new BotNotMentionedException("The bot was not mentioned in the given message.");
        }
        //Index 0: Mention
        int nameLength = message.getGuild().getSelfMember().getEffectiveName().split(" ").length;
        String content = message.getContentDisplay().trim();
        String command = content.split(" ")[nameLength];
        String[] args = content.split(" ").length > 1 ?
                Arrays.copyOfRange(content.split(" "), nameLength + 1, content.split(" ").length)
                : new String[]{};
        return new Instruction(command, args, message);
    }
}
