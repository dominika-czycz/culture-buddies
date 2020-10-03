package pl.coderslab.cultureBuddies.buddies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface BuddyRepository extends JpaRepository<Buddy, Long> {
    Optional<Buddy> findFirstByUsernameIgnoringCase(String username);


    @Query(nativeQuery = true,
            value = "SELECT b1.* FROM buddies b1 " +
                    "JOIN buddies_relations br on b1.id = br.buddy_id " +
                    "JOIN buddies b2 ON br.buddy_of_id = b2.id " +
                    "WHERE b2.id = ?1 AND br.status_id = ?2")
    List<Buddy> findAllBuddiesOfBuddyWithIdWhereRelationId(Long buddyId, Long relationId);

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT b.* FROM buddies b " +
                    "JOIN buddies_books  on b.id = buddies_books.buddy_id " +
                    "JOIN author_book ab on buddies_books.book_id = ab.book_id " +
                    "LEFT JOIN buddies_relations br on b.id = br.buddy_id " +
                    "WHERE ab.author_id in (?1) AND b.id <> ?2 " +
                    "AND ( br.buddy_of_id <> ?2 ||  br.buddy_of_id is null)")
    List<Buddy> findNewBuddiesByAuthors(ArrayList<Long> authorsIds, Long excludedBuddyId);

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT b.* FROM buddies b  " +
                    "LEFT JOIN buddies_relations br on b.id = br.buddy_id " +
                    "WHERE b.username LIKE CONCAT(?1 ,'%') AND b.id <> ?2 " +
                    "AND ( br.buddy_of_id <> ?2 ||  br.buddy_of_id is null) ")
    List<Buddy> findNewBuddiesByUsernameStartingWithAndIdNot(String username, Long excludedBuddyId);

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT b.* FROM buddies b " +
                    "JOIN buddies_books  on b.id = buddies_books.buddy_id " +
                    "JOIN author_book ab on buddies_books.book_id = ab.book_id " +
                    "LEFT JOIN buddies_relations br on b.id = br.buddy_id " +
                    "WHERE ab.author_id in (?1) AND b.id <> ?3 " +
                    "AND ( br.buddy_of_id <> ?3 ||  br.buddy_of_id is null) " +
                    "AND b.username LIKE CONCAT(?2 ,'%') ")
    List<Buddy> findNewBuddiesByAuthorsAndUsernameLike(ArrayList<Long> authorsIds, String username, Long excludedBuddyId);

    @Query("SELECT DISTINCT b from Buddy b LEFT JOIN FETCH b.events e where b.id=?1")
    Optional<Buddy> findByIdWithEvents(Long id);
}
