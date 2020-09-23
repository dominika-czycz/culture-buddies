package pl.coderslab.cultureBuddies.city;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;


@RequiredArgsConstructor
public class CityConverter implements Converter<String, City> {
    private CityRepository cityRepository;

    @Autowired
    public void setCityRepository(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public City convert(String s) {
        final long id = Long.parseLong(s);
        final Optional<City> city = cityRepository.findById(id);
        if (city.isEmpty()) {
            throw new IllegalStateException("City does not exist");
        }
        return city.get();
    }
}
