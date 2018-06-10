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
import me.niklas.postie.core.VersionInfo;
import net.dv8tion.jda.core.entities.Message;

/**
 * Created by Niklas on 05.06.2018 in postie
 */
public class VersionCommand implements Command {
    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"v"};
    }

    @Override
    public String getDescription() {
        return "Prints out the version of the bot.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"version"};
    }

    @Override
    public Result execute(Message message, String[] args) {
        return new Result("Version", "My current version is v. " + VersionInfo.VERSION, message);
    }
}
