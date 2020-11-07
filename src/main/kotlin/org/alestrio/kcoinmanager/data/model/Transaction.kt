package org.alestrio.kcoinmanager.data.model

import com.github.vokorm.KEntity
import com.gitlab.mvysny.jdbiorm.Dao
import java.time.Instant
import javax.validation.constraints.NotEmpty

data class Transaction(
    /**
     * This is the database model storing a transaction
     */
    override var id : Long? = null,
    private var source:String = "",
    private var destination:String = "",
    private var amount:Int = 0

) : KEntity<Long> {
    override fun save(validate: Boolean){
        var modified = Instant.now()
        super.save(validate)
    }

    companion object : Dao<Transaction, Long>(Transaction::class.java)
}