package org.j3y.discordbot.repository

import org.j3y.discordbot.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo extends JpaRepository<User, String> {

}
