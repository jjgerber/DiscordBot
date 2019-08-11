package org.j3y.discordbot.model;

import javax.persistence.Entity
import javax.persistence.Id;

@Entity
class User {
    @Id
    String userId
    boolean isAdmin
    boolean isBlocked
}
