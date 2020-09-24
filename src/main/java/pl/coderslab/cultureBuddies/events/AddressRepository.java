package pl.coderslab.cultureBuddies.events;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findFirstByCityAndStreetAndNumberAndFlatNumber(String city, String Street, String number, String flatNumber);
}
