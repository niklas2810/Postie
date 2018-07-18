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

import me.niklas.postie.core.Postie;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONObject;


/**
 * Created by Niklas on 18.07.2018 in postie
 */
public class PrivateMessageReceiveListener extends ListenerAdapter {

    private String broadcastMessage = null;

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equalsIgnoreCase(Postie.getInstance().getMaintainerId())) return;

        if (event.getMessage().getContentRaw().trim().equalsIgnoreCase("confirm")) { //User wants to send message
            if (broadcastMessage == null) { //Nothing typed yet
                event.getChannel().sendMessage("Please enter a message first!").queue();
            } else { //Something entered -> send it
                Message confirmation = event.getChannel().sendMessage("Sending messages...").complete();

                String message = "**Broadcast by the bot maintainer**:\n\n---------------\n\n" + broadcastMessage
                        + "\n\n---------------\n\nIf you wish to not see these messages, please use the command `nobroadcasts`, or for short, `nobr`.";
                for (Guild g : event.getJDA().getGuilds()) { //For each guild
                    JSONObject guildData = Postie.getInstance().getDataManager().get(g.getId());
                    if (guildData.has("broadcasts") && !guildData.getBoolean("broadcasts")) continue; //Skip if disabled
                    try {
                        g.getDefaultChannel().sendMessage(message).queue();
                    } catch (Exception expected) {
                    } //If the bot has not the permission to do it.
                }

                confirmation.editMessage("All messages has been sent.").queue();
                broadcastMessage = null;
            }
        } else {
            broadcastMessage = event.getMessage().getContentRaw();
            event.getChannel().sendMessage("Your text has been saved.").queue();
        }
    }
}
