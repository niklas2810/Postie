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

package me.niklas.postie.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Niklas on 06.06.2018 in postie
 * Used to store reactions with are valid for votes, issued in the {@link me.niklas.postie.command.voting.VoteCommand}.
 *
 * @see me.niklas.postie.manager.ReactionManager
 */
public class Reactions {

    /**
     * All {@link net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote}s which are allowed for yes/no questions.
     */
    public static final List<String> YES_NO_REACTIONS = Arrays.asList("\uD83D\uDC4D", "\uD83D\uDC4E");

    /**
     * All {@link net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote}s which could be allowed for multiple-options votings
     */
    public static final List<String> NUMBERS = Arrays.asList("\u0030\u20E3",
            "\u0031\u20E3", "\u0032\u20E3", "\u0033\u20E3", "\u0034\u20E3",
            "\u0035\u20E3", "\u0036\u20E3", "\u0037\u20E3", "\u0038\u20E3", "\u0039\u20E3");


    /**
     * The {@link net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote} for a white check mark.
     */
    public static final String WHITE_CHECK_MARK = "\u2705";
}
