package world.ucode.cashflow.repos;

import org.springframework.data.jpa.repository.Query;
import world.ucode.cashflow.db.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepo extends CrudRepository<Transaction, Long> {
    List<Transaction> findByAuthor(String author);
    List<Transaction> findByTag(String tag);
    @Query(value = "select * from transaction where wallet = ?1", nativeQuery = true)
    List<Transaction> findByWallet(String wallet);
//    @Query(value = "select * from transaction where tag = ?1", nativeQuery = true)
//    List<Transaction> filterByTag(String tag);
}