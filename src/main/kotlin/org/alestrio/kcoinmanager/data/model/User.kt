package org.alestrio.kcoinmanager.data.model

import com.github.vokorm.KEntity
import com.gitlab.mvysny.jdbiorm.Dao
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant

data class User(
    /**
     * This is the database model storing user data.
     */
    override var id : Long? = null,
    var pseudo:String = "",
    var balance:Int = 0,
    var password:String = ""

): KEntity<Long>{
    override fun save(validate: Boolean){
        var modified = Instant.now()
        super.save(validate)
    }

    companion object : Dao<User, Long>(User::class.java)

    fun hashPassword(){
        val oldPw = this.password
        this.password = BCrypt.hashpw(this.password, BCrypt.gensalt())
    }
}