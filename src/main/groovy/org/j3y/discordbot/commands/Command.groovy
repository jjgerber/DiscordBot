package org.j3y.discordbot.commands

import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.j3y.discordbot.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import javax.annotation.Nonnull

@Slf4j
abstract class Command extends ListenerAdapter {

    String commandIdentifier
    UserService userService

    @Autowired
    CommandListener(@Value('${bot.command-identifier:@}') String commandIdentifier,
                    UserService userService) {
        this.commandIdentifier = commandIdentifier
        this.userService = userService
    }

    abstract String getCommandKey()
    abstract boolean isAdminCommand()
    abstract void execute(MessageReceivedEvent messageEvent, String... tokens)

    @Override
    void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        User author = event.getAuthor()
        if (author.isBot()) {
            return
        }

        String[] tokens = event.getMessage().getContentRaw().split()
        String inputCommand = tokens[0]

        if ("${commandIdentifier}${getCommandKey()}" == inputCommand) {
            log.info "Received command: {}", inputCommand
            String authorTag = author.getAsTag()

            if (userService.isUserBlocked(authorTag)) {
                event.getChannel().sendMessage("Sorry, " + author.getName() +", you are blocked from using the bot.").queue()
                return
            }

            if (isAdminCommand() && !userService.isUserAdmin(authorTag)) {
                event.getChannel().sendMessage("This command requires admin permissions.").queue()
                return
            }

            this.execute(event, tokens)
        }

    }
}
