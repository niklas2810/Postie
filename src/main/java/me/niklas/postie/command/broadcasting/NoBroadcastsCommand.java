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
import org.json.JSONObject;

/**
 * Created by Niklas on 18.07.2018 in postie
 */
public class NoBroadcastsCommand implements Command {
    @Override
    public String getName() {
        return "nobroadcasts";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"nobr", "nobrs", "nobroadcast"};
    }

    @Override
    public String getDescription() {
        return "Toggles broadcasting for this server.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"nobroadcasts", "nobr"};
    }

    @Override
    public int getRequiredLevel() {
        return 3;
    }

    @Override
    public Result execute(Message message, String[] args) {
        JSONObject data = Postie.getInstance().getDataManager().get(message.getGuild().getId());
        boolean doBroadcasts = data.has("broadcasts") ? data.getBoolean("broadcasts") : true;
        ;

        doBroadcasts = !doBroadcasts; //Toggle the value

        data.put("broadcasts", doBroadcasts);

        Postie.getInstance().getDataManager().save(message.getGuild().getId(), data);
        String text = "Broadcasts have been ";
        text += doBroadcasts ? "enabled." : "disabled.";
        return new Result("Success", text, message);
    }
}
