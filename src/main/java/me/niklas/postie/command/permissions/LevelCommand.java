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

package me.niklas.postie.command.permissions;

import me.niklas.postie.command.Command;
import me.niklas.postie.command.Result;
import me.niklas.postie.core.Postie;
import net.dv8tion.jda.core.entities.Message;

/**
 * Created by Niklas on 23.06.2018 in postie
 */
public class LevelCommand implements Command {
    @Override
    public String getName() {
        return "level";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Displays the level of you or the mentioned user.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"level", "level @Niklas"};
    }

    @Override
    public int getRequiredLevel() {
        return 1;
    }

    @Override
    public Result execute(Message message, String[] args) {
        int level;
        if (message.getMentionedMembers().size() < 2) {
            level = Postie.getInstance().getPermissionManager().getLevelOfUser(message.getGuild().getId(), message.getAuthor().getId());
        } else {
            level = Postie.getInstance().getPermissionManager().getLevelOfUser(message.getGuild().getId(), message.getMentionedMembers().get(1).getUser().getId());
        }

        return new Result(String.format("The level of this user is %s.", level), message);
    }
}
