package org.j3y.discordbot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdminConfig {

    @Bean("defaultAdmins")
    @ConfigurationProperties('bot.admins')
    List<String> getAdmins() {
        return []
    }

}
