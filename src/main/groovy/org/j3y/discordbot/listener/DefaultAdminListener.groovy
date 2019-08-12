package org.j3y.discordbot.listener

import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.j3y.discordbot.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.Nonnull

@Component
@Slf4j
class DefaultAdminListener extends ListenerAdapter {

    @Autowired
    UserService userService

    @Autowired
    List<String> defaultAdmins

    @Override
    void onReady(@Nonnull ReadyEvent event) {
        log.info("Setting Default Admins: {}", defaultAdmins.join(','))
        defaultAdmins.each {
            try {
                userService.setUserAdmin(it, true)
            } catch(Exception e) {
                log.warn("Unable to set user `{}` as admin: {}", it, e.getMessage())
            }
        }
    }
}
