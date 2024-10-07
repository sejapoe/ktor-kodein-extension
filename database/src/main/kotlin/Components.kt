import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.kodein.di.instance
import ru.sejapoe.KodeinComponent
import kotlin.coroutines.CoroutineContext

abstract class KodeinService : KodeinComponent() {
    protected val database: Database by instance()
    suspend fun <T> suspendedTransaction(
        context: CoroutineContext? = null,
        transactionIsolation: Int? = null,
        nested: Boolean = true,
        statement: suspend Transaction.() -> T
    ): T = TransactionManager.currentOrNull()
        .let { if (nested) null else it }
        ?.let { statement(it) }
        ?: newSuspendedTransaction(
            context = context,
            db = database,
            transactionIsolation = transactionIsolation,
            statement = statement
        )

}
