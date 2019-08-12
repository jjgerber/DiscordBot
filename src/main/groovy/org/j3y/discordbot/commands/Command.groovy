package org.j3y.discordbot.commands

import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.j3y.discordbot.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import javax.annotation.Nonnull

@Slf4j
class Command extends ListenerAdapter {

    @Value('${bot.command-prefix:@}')
    String prefix

    @Autowired
    UserService userService

    String getCommandKey() {
        return null
    }

    boolean isAdminCommand() {
        return false
    }

    void execute(MessageReceivedEvent messageEvent, String... tokens) {

    }

    @Override
    void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        User author = event.getAuthor()
        if (author.isBot()) {
            return
        }

        String[] tokens = event.getMessage().getContentRaw().split()
        String inputCommand = tokens[0]

        if ("${prefix}${getCommandKey()}" == inputCommand) {
            log.info "Received command: {}", inputCommand
            String authorTag = author.getAsTag()

            if (userService.isUserBlocked(authorTag)) {
                sendMessage(event.getChannel(), "Sorry, ${author.getName()}, you are blocked from using the bot.")
                return
            }

            if (isAdminCommand() && !userService.isUserAdmin(authorTag)) {
                sendMessage(event.getChannel(), "This command requires admin permissions.")
                return
            }

            this.execute(event, tokens)
        }
    }

    static void sendMessage(MessageChannel channel, String message) {
        channel.sendMessage(message).queue()
    }
}
