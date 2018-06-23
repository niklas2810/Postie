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

package me.niklas.postie.command.general;

import me.niklas.postie.command.Command;
import me.niklas.postie.command.Result;
import me.niklas.postie.core.Postie;
import me.niklas.postie.util.Reactions;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;

/**
 * Created by Niklas on 20.06.2018 in postie
 */
public class InviteCommand implements Command {

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Sends an invite link to the user.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"invite"};
    }

    @Override
    public int getRequiredLevel() {
        return 1;
    }

    @Override
    public Result execute(Message message, String[] args) {
        PrivateChannel channel = message.getAuthor().openPrivateChannel().complete();
        String inviteLink = String.format("You can invite me to your guild using this link: %s", Postie.getInstance().getInviteLink(message.getJDA()));
        channel.sendMessage(inviteLink).queue();
        message.addReaction(Reactions.WHITE_CHECK_MARK).queue();
        return new Result(message);
    }
}
