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

package me.niklas.postie.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niklas on 05.06.2018 in postie
 */
public class Result {

    private final String title;
    private final String description;
    private final Message origin;
    private final List<String> reactions = new ArrayList<>();
    private boolean deleteOrigin = false;
    private String tag = "";
    private boolean useEmbed = true;

    /**
     * Initializes a new Result which will not send any message to the {@link net.dv8tion.jda.core.entities.Channel}.
     *
     * @param origin The original {@link Message}.
     */
    public Result(Message origin) {
        this.origin = origin;
        this.title = null;
        this.description = null;
    }

    /**
     * Initializes a new Result which is supposed to be displayed as a {@link net.dv8tion.jda.core.entities.MessageEmbed}.
     *
     * @param title       The title of the {@link net.dv8tion.jda.core.entities.MessageEmbed}.
     * @param description The description of the {@link net.dv8tion.jda.core.entities.MessageEmbed}.
     * @param origin      The original {@link Message}.
     */
    public Result(String title, String description, Message origin) {
        this.title = title;
        this.description = description;
        this.origin = origin;
    }

    /**
     * Initializes a new Result which is supposed to be displayed as standard text.
     *
     * @param text   The text of the answer.
     * @param origin The original {@link Message}.
     */
    public Result(String text, Message origin) {
        this.title = "";
        this.description = text;
        this.origin = origin;
        this.useEmbed = false;
    }

    /**
     * @return The title of the message, displayed in the headline after the bot's name.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The description, the main text field in the {@link net.dv8tion.jda.core.entities.MessageEmbed}.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The original {@link Message}
     */
    public Message getOrigin() {
        return origin;
    }

    /**
     * @return The {@link net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote}s which are added as the {@link Message} is sent.
     */
    public List<String> getReactions() {
        return reactions;
    }

    /**
     * @return Whether a {@link net.dv8tion.jda.core.entities.MessageEmbed} will be used to display the result.
     */
    public boolean useEmbed() {
        return useEmbed;
    }

    /**
     * Sets whether a {@link net.dv8tion.jda.core.entities.MessageEmbed} will be used to display the result.
     * Without, only the description will be displayed.
     *
     * @param useEmbed Whether a {@link net.dv8tion.jda.core.entities.MessageEmbed} will be used to display the result.
     * @return The object itself.
     */
    public Result useEmbed(boolean useEmbed) {
        this.useEmbed = useEmbed;
        return this;
    }

    /**
     * Adds reactions which are displayed as soon as the {@link Message} is sent.
     *
     * @param reactions A {@link List} of all reactions <b>(in Unicode)</b>
     * @return The object itself.
     */
    public Result reactWhenSent(List<String> reactions) {
        this.reactions.addAll(reactions);
        return this;
    }

    /**
     * Adds a tag to the footer. That can be used to identify some important values later on.
     * Does not work when working with non-embed results.
     *
     * @param tag The tag which should be displayed (should be as short as possible.
     * @return The object itself.
     * @see me.niklas.postie.command.voting.VoteCommand
     */
    public Result addFooterTag(String tag) {
        this.tag = tag;
        return this;
    }

    /**
     * Sends the {@link Message} into the same {@link net.dv8tion.jda.core.entities.MessageChannel} as the original {@link Message}.
     */
    public void send() {
        if (!useEmbed) {
            origin.getChannel().sendMessage(description).queue(message -> {
                if (reactions.size() > 0) {
                    reactions.forEach(reaction -> message.addReaction(reaction).queue());
                }
            });
        } else if (title != null && title.length() > 0 && description != null && description.length() > 0) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Postie - " + title);
            builder.setDescription(description);
            String footer = "Reply to " + origin.getMember().getEffectiveName();
            footer += tag != null ? " <" + tag + ">" : "";
            builder.setFooter(footer, origin.getAuthor().getAvatarUrl());
            origin.getChannel().sendMessage(builder.build()).queue(message -> {
                if (reactions.size() > 0) {
                    reactions.forEach(reaction -> message.addReaction(reaction).queue());
                }
            });
        }
        if (deleteOrigin) {
            origin.delete().queue();
        }
    }

    /**
     * Sets whether the original {@link Message} should be deleted as soon as the result is sent.
     *
     * @param delete Whether the original {@link Message} should be deleted.
     * @return The object itself.
     */
    public Result deleteOrigin(boolean delete) {
        this.deleteOrigin = delete;
        return this;
    }
}
