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

package me.niklas.postie.command.managment;

import me.niklas.postie.command.Command;
import me.niklas.postie.command.Result;
import me.niklas.postie.core.Postie;
import net.dv8tion.jda.core.entities.Message;

/**
 * Created by Niklas on 15.07.2018 in postie
 */
public class LeaveCommand implements Command {
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Deletes all data about the guild & makes the bot leave the server.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"leave"};
    }

    @Override
    public int getRequiredLevel() {
        return 3;
    }

    @Override
    public Result execute(Message message, String[] args) {
        String guildId = message.getGuild().getId();
        Postie.getInstance().getPermissionManager().removeGuild(guildId);
        Postie.getInstance().getDataManager().remove(guildId);
        Postie.getInstance().getAnswersManager().removeGuild(guildId);
        message.getGuild().leave().queue();
        return new Result(message);
    }
}
