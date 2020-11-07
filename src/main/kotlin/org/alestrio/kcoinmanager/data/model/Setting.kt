package org.alestrio.kcoinmanager.data.model

import com.github.vokorm.KEntity
import com.gitlab.mvysny.jdbiorm.Dao
import java.time.Instant

data class Setting(
    /**
     * This is the database model storing the programme settings
     */
    override var id : Long? = null,
    var skey:String = "",
    var value:String = ""

) : KEntity<Long>{
    override fun save(validate: Boolean) {
        var modified = Instant.now()
        super.save(validate)
    }

    companion object : Dao<Setting, Long>(Setting::class.java)
}