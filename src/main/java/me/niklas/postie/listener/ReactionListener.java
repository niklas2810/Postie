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


import me.niklas.postie.command.Result;
import me.niklas.postie.core.Postie;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Created by Niklas on 06.06.2018 in postie
 * Used for managing reaction on vote messages.
 *
 * @see me.niklas.postie.manager.ReactionManager
 */
public class ReactionListener extends ListenerAdapter {

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getReactionEmote().isEmote()) return;
        if (event.getUser().isBot()) return;

        Message message = event.getChannel().getMessageById(event.getMessageId()).complete();

        if (!Postie.getInstance().getReactionManager().isPostieEmbed(message)) return;

        if (!Postie.getInstance().getReactionManager().isReactionValid(message, event.getReactionEmote().getName())) {
            try {
                event.getReaction().removeReaction(event.getUser()).queue();
            } catch (PermissionException e) {
                new Result("Lack of Permission", "I need the permission `" + e.getPermission() + "` to work properly!", message).send();
            }
        }

    }
}
