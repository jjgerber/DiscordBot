package org.j3y.discordbot

import org.j3y.discordbot.model.User
import spock.lang.Specification

class UserSpec extends Specification {

    def "Test getters and setters"() {
        given:
        User user = new User()

        when:
        user.setIsAdmin(true)
        user.setIsBlocked(true)
        user.setUserId("foobar")

        then:
        user.isAdmin
        user.isBlocked
        user.userId == "foobar"

        when:
        user.setIsAdmin(false)
        user.setIsBlocked(false)
        user.setUserId("barfoo")

        then:
        !user.isAdmin
        !user.isBlocked
        user.userId == "barfoo"
    }

}
