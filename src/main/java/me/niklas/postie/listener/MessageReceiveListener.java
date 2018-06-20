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

package me.niklas.postie.listener;

import me.niklas.postie.command.CommandBuilder;
import me.niklas.postie.command.Instruction;
import me.niklas.postie.command.Result;
import me.niklas.postie.core.Postie;
import me.niklas.postie.exception.BotNotMentionedException;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

/**
 * Created by Niklas on 05.06.2018 in postie
 * Used to process input in order to process commands or printing out answers.
 *
 * @see me.niklas.postie.manager.CommandManager
 * @see me.niklas.postie.manager.AnswersManager
 */
public class MessageReceiveListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        if (message.getMentionedMembers().size() > 0 && message.getMentionedMembers().get(0).equals(event.getGuild().getSelfMember())) {
            if (message.getContentDisplay().startsWith("@")) {
                try {
                    Instruction instruction = CommandBuilder.build(message);
                    Result r = Postie.getInstance().getCommandManager().handle(instruction);
                    if (r != null) {
                        r.send();
                    } else { //Not found OR answer
                        if (Postie.getInstance().getAnswersManager().hasAnswerFor(message.getGuild().getId(), instruction.getAllInput())) {
                            new Result(Postie.getInstance().getAnswersManager().getAnswerForInput(message.getGuild().getId(), instruction.getAllInput()), message).send();
                        } else {
                            message.addReaction("\u2754").queue();
                        }
                    }
                } catch (BotNotMentionedException e) {
                    LoggerFactory.getLogger(this.getClass()).error("The bot has not been mentioned: ", e);
                } catch (PermissionException e) {
                    new Result("Lack of Permission", "I need the permission `" + e.getPermission() + "` to work properly!", message).send();
                }
            }

        }
    }
}
