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

/**
 * Created by Niklas on 06.06.2018 in postie
 * Stores titles to validate vote {@link net.dv8tion.jda.core.entities.Message}s in the {@link me.niklas.postie.manager.ReactionManager}
 */
public class Titles {

    /**
     * The title for the {@link me.niklas.postie.command.voting.VoteCommand}s {@link me.niklas.postie.command.Result}.
     */
    public static final String PUBLIC_VOTING = "Public Voting";

    /**
     * A title used for votes with multiple choices in the {@link me.niklas.postie.command.voting.VoteCommand} in the {@link me.niklas.postie.command.Result}.
     */
    public static final String MASS_VOTING = "Mass Voting";
}
