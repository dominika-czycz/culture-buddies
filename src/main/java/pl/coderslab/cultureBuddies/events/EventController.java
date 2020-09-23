package pl.coderslab.cultureBuddies.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.cultureBuddies.city.City;
import pl.coderslab.cultureBuddies.city.CityRepository;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/app/myEvents/")
public class EventController {
    private final EventService eventService;
    private final CityRepository cityRepository;

    @GetMapping
    public String prepareAllPage(Model model) throws NotExistingRecordException {
        log.info("Preparing events page...");
        final List<Event> events = eventService.getEventsOfPrincipal();
        log.debug("Buddy's events list with {} positions has been found. ", events.size());
        final List<Event> joined = eventService.getJoinedEvents();
        List<EventType> eventTypes = eventService.findAllEventsTypes();
        final List<City> cities = cityRepository.findAll();
        model.addAttribute("events", events);
        model.addAttribute("types", eventTypes);
        model.addAttribute("joined", joined);
        model.addAttribute("cities", cities);
        return "/events/events";
    }
}
