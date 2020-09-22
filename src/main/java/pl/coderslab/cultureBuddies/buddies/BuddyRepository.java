package pl.coderslab.cultureBuddies.buddies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BuddyRepository extends JpaRepository<Buddy, Long> {
    Optional<Buddy> findFirstByUsernameIgnoringCase(String username);

    @Query(nativeQuery = true,
            value = "SELECT b1.* FROM buddies b1 " +
                    "JOIN buddies_relations br on b1.id = br.buddy_id " +
                    "JOIN buddies b2 ON br.buddy_of_id = b2.id JOIN ralations_status rs on br.status_id = rs.id " +
                    "WHERE b2.id = ?1 AND rs.name = ?2")
    List<Buddy> findAllBuddiesOfBuddyWithIdWhereRelationIs(Long buddyId, String relation);

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT b.* FROM buddies b " +
                    "JOIN buddies_books  on b.id = buddies_books.buddy_id " +
                    "JOIN author_book ab on buddies_books.book_id = ab.book_id " +
                    "LEFT JOIN buddies_relations br on b.id = br.buddy_id " +
                    "LEFT JOIN buddies b2 ON br.buddy_of_id = b2.id  " +
                    "JOIN authors a on ab.author_id = a.id " +
                    "WHERE a.id in (?1) AND b.id <> ?2 AND ( b2.id <> ?2 ||  b2.id is null)")
    List<Buddy> findNewBuddiesByAuthors(Collection<Integer> authorsIds, Long excludedBuddyId);

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT b.* FROM buddies b  " +
                    "LEFT JOIN buddies_relations br on b.id = br.buddy_id " +
                    "LEFT JOIN buddies b2 ON br.buddy_of_id = b2.id  " +
                    "WHERE b.username LIKE CONCAT(?1 ,'%') AND b.id <> ?2 AND ( b2.id <> ?2 ||  b2.id is null) ")
    List<Buddy> findNewBuddiesByUsernameStartingWithAndIdNot(String username, Long excludedBuddyId);

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT b.* FROM buddies b " +
                    "JOIN buddies_books  on b.id = buddies_books.buddy_id " +
                    "JOIN author_book ab on buddies_books.book_id = ab.book_id " +
                    "LEFT JOIN buddies_relations br on b.id = br.buddy_id " +
                    "LEFT JOIN buddies b2 ON br.buddy_of_id = b2.id  " +
                    "JOIN authors a on ab.author_id = a.id " +
                    "WHERE a.id in (?1) AND b.username LIKE CONCAT(?2 ,'%') AND b.id <> ?3 AND ( b2.id <> ?3 ||  b2.id is null)")
    List<Buddy> findNewBuddiesByAuthorsAndUsernameLike(Collection<Integer> authorsLastName, String username, Long excludedBuddyId);
}
