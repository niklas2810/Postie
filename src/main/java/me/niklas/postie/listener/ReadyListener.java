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
import me.niklas.postie.core.VersionInfo;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

/**
 * Created by Niklas on 20.06.2018 in postie
 */
public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().getPresence().setGame(Game.of(Game.GameType.DEFAULT, "v. " + VersionInfo.VERSION, Postie.getInstance().getInviteLink(event.getJDA())));
        LoggerFactory.getLogger("Invite Link").info(Postie.getInstance().getInviteLink(event.getJDA()));

    }
}
