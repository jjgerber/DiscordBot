package org.j3y.discordbot.service

import net.dv8tion.jda.api.JDA
import org.j3y.discordbot.model.User
import org.j3y.discordbot.repository.UserRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class DefaultUserService implements UserService {

    JDA api
    UserRepo userRepo

    @Autowired
    DefaultUserService(@Lazy JDA api, UserRepo userRepo) {
        this.api = api
        this.userRepo = userRepo
    }

    @Override
    User setUserBlocked(String userTag, boolean isBlocked) {
        if (!isValidUserTag(userTag)) {
            throw new IllegalArgumentException("Invalid user was provided.");
        }

        User user = userRepo.findById(userTag).orElse(new User(userId: userTag, isAdmin: false))

        user.setIsBlocked(isBlocked)
        userRepo.save(user)

        return user
    }

    @Override
    User setUserAdmin(String userTag, boolean isAdmin) {
        if (!isValidUserTag(userTag)) {
            throw new IllegalArgumentException("Invalid user was provided.");
        }

        User user = userRepo.findById(userTag).orElse(new User(userId: userTag, isBlocked: false))

        user.setIsAdmin(isAdmin)
        userRepo.save(user)

        return user
    }

    @Override
    boolean isUserBlocked(String userTag) {
        User user = userRepo.findById(userTag).orElse(null)
        if (user == null || !user.isBlocked) {
            return false
        }
        return true
    }

    @Override
    boolean isUserAdmin(String userTag) {
        User user = userRepo.findById(userTag).orElse(null)
        if (user == null || !user.isAdmin) {
            return false
        }
        return true
    }

    boolean isValidUserTag(String userTag) {
        try {
            if (api.getUserByTag(userTag) == null) {
                return false
            }
        } catch (IllegalArgumentException ignored) {
            return false
        }

        return true
    }
}
