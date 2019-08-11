package org.j3y.discordbot.service

import org.j3y.discordbot.model.User

interface UserService {
    User setUserBlocked(String userTag, boolean isBlocked);
    User setUserAdmin(String userTag, boolean isAdmin);

    boolean isUserBlocked(String userTag);
    boolean isUserAdmin(String userTag);
}
