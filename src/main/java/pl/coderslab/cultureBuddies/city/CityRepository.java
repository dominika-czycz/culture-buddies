package pl.coderslab.cultureBuddies.city;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CityRepository extends JpaRepository<City, Long> {
}
