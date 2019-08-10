package org.j3y.discordbot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface Command {
    String getCommandKey()
    void execute(MessageReceivedEvent messageEvent, String... tokens)
}