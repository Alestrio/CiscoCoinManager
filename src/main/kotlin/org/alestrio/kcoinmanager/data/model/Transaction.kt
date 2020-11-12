package org.alestrio.kcoinmanager.data.model

import com.github.vokorm.*
import com.gitlab.mvysny.jdbiorm.Dao
import java.time.Instant

data class Transaction(
    /**
     * This is the database model storing a transaction
     */
    override var id : Long? = null,
    var source:String = "",
    var destination:String = "",
    var amount:Int = 0

) : KEntity<Long> {
    override fun save(validate: Boolean){
        var modified = Instant.now()
        super.save(validate)
    }

    companion object : Dao<Transaction, Long>(Transaction::class.java)
}