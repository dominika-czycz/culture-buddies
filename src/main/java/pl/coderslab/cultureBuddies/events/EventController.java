package pl.coderslab.cultureBuddies.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.city.City;
import pl.coderslab.cultureBuddies.city.CityRepository;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import javax.validation.Valid;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/app/myEvents/")
public class EventController {
    private final EventService eventService;
    private final CityRepository cityRepository;
    private final BuddyService buddyService;

    @GetMapping
    public String prepareAllPage(Model model) throws NotExistingRecordException {
        log.info("Preparing events page...");
        final List<Event> events = eventService.getEventsOfPrincipal();
        log.debug("Buddy's events list with {} positions has been found. ", events.size());
        final List<Event> joined = eventService.getJoinedEvents();
        final List<City> cities = cityRepository.findAll();
        model.addAttribute("events", events);
        model.addAttribute("joined", joined);
        model.addAttribute("cities", cities);
        return "/events/events";
    }

    @GetMapping("/add")
    public String prepareAddPage(Model model) {
        log.info("Preparing add event page...");
        model.addAttribute(new Event());
        return "/events/add";
    }

    @PostMapping("/add")
    public String processAddPage(@Valid Event event, BindingResult result) {
        log.debug("Entity to save:  {}", event);
        if (isNotValid(event, result)) return "/events/add";
        eventService.save(event);
        return "redirect:/app/myEvents/";
    }

    private boolean isNotValid(Event event, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Entity {} fails validation.", event);
            return true;
        }
        return false;
    }

    @ModelAttribute("types")
    public List<EventType> types() {
        return eventService.findAllEventsTypes();
    }

    @ModelAttribute("buddy")
    public Buddy buddy() throws NotExistingRecordException {
        return buddyService.getPrincipal();
    }
}
