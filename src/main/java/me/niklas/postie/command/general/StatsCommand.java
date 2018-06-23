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
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Message;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

/**
 * Created by Niklas on 23.06.2018 in postie
 */
public class StatsCommand implements Command {

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Shows statistics about the bot.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"stats"};
    }

    @Override
    public int getRequiredLevel() {
        return 1;
    }

    @Override
    public Result execute(Message message, String[] args) {
        Result result = new Result("Statistics", message);

        String usedMemory = (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1000000) + " MB";
        String maxMemory = (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1000000) + " MB";
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();

        long uptime = rb.getUptime();
        long days = TimeUnit.MILLISECONDS.toDays(uptime);
        long hours = TimeUnit.MILLISECONDS.toHours(uptime) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime) % 60;

        StringBuilder builder = new StringBuilder();
        builder.append(days).append(" Days, ").append(hours).append("Hours, ").append(minutes).append(" Minutes and ").append(seconds).append(" Seconds");

        result.addField("OS", System.getProperty("os.name"));
        result.addField("RAM", usedMemory + " / " + maxMemory);
        result.addField("JDA version", JDAInfo.VERSION);
        result.addField("Bot version", "v. " + VersionInfo.VERSION);
        result.addField("Ping", message.getJDA().getPing() + "ms");
        result.addField("Java version", System.getProperty("java.runtime.version").replace("+", "_"));
        result.addField("Uptime", builder.toString());

        return result;
    }
}
