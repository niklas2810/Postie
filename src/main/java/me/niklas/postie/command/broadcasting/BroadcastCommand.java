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

package me.niklas.postie.command.broadcasting;

import me.niklas.postie.command.Command;
import me.niklas.postie.command.Result;
import me.niklas.postie.core.Postie;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;

/**
 * Created by Niklas on 18.07.2018 in postie
 */
public class BroadcastCommand implements Command {

    @Override
    public String getName() {
        return "broadcast";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"br"};
    }

    @Override
    public String getDescription() {
        return "Writes something into every server (default channel), only usable by the maintainer of the bot instance.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"broadcast Today I have to announce something very important[...]"};
    }

    @Override
    public int getRequiredLevel() {
        return 1;
    }

    @Override
    public Result execute(Message message, String[] args) {
        boolean valid = message.getAuthor().getId().equals(Postie.getInstance().getMaintainerId());

        if (!valid) {
            return new Result("Only the bot maintainer is permitted to do that.", message);
        }
        PrivateChannel channel = message.getAuthor().openPrivateChannel().complete();
        channel.sendMessage("Hey there! Simply enter the message you want to broadcast here. Send `confirm` to " +
                "confirm your input, then it will be broadcasted. Happy typing!").queue();
        channel.close().queue();
        return new Result("Check your direct messages!", message);
    }
}
