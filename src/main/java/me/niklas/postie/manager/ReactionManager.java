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

package me.niklas.postie.manager;

import me.niklas.postie.util.Reactions;
import me.niklas.postie.util.Titles;
import me.niklas.postie.util.Util;
import net.dv8tion.jda.core.entities.Message;

import java.util.Collections;
import java.util.List;

/**
 * Created by Niklas on 06.06.2018 in postie
 */
public class ReactionManager {

    /**
     * Checks if the reaction to a specific message is valid
     *
     * @param message      The message
     * @param reactionName The name of the reaction
     * @return Whether is it allowed to react in that way
     */
    public boolean isReactionValid(Message message, String reactionName) {
        return !isPostieEmbed(message) || (getAllowedReactions(message).size() > 0 && getAllowedReactions(message).contains(reactionName));
    }

    /**
     * Checks whether the {@link Message} is one where reactions are restricted.
     *
     * @param message The {@link Message} which should be checked.
     * @return Whether the {@link Message} is one where reactions are restricted.
     */
    public boolean isPostieEmbed(Message message) {
        if (!message.getAuthor().getId().equals(message.getJDA().getSelfUser().getId())) return false;
        if (message.getEmbeds().size() != 1) return false;

        String title = message.getEmbeds().get(0).getTitle();

        return title.contains(Titles.PUBLIC_VOTING) || title.contains(Titles.MASS_VOTING);
    }

    /***
     * Get the allowed {@link net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote}s for a {@link Message}.
     * @param message The {@link Message}.
     * @return Which {@link net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote}s are allowed.
     */
    private List<String> getAllowedReactions(Message message) {
        if (!isPostieEmbed(message)) return Collections.emptyList();

        String title = message.getEmbeds().get(0).getTitle();

        if (title.contains(Titles.PUBLIC_VOTING)) return Reactions.YES_NO_REACTIONS;
        if (title.contains(Titles.MASS_VOTING)) {
            String footer = message.getEmbeds().get(0).getFooter().getText();
            String tag = footer.contains("<") && footer.contains(">") ?
                    footer.substring(footer.lastIndexOf("<") + 1, footer.lastIndexOf(">")) : "";
            if (Util.isInteger(tag)) {
                return Reactions.NUMBERS.subList(1, Integer.valueOf(tag) + 1);
            }
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }
}
