package pl.coderslab.cultureBuddies.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.city.City;
import pl.coderslab.cultureBuddies.city.CityRepository;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import javax.validation.Valid;
import javax.validation.Validator;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/app/myEvents/")
public class EventController {
    private final EventService eventService;
    private final CityRepository cityRepository;
    private final BuddyService buddyService;
    private final Validator validator;

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

    @GetMapping("/edit/{eventId}")
    public String prepareEditPage(@PathVariable Long eventId, Model model) throws NotExistingRecordException {
        log.debug("Preparing edit page of event with id {}", eventId);
        Event event = eventService.findByEventById(eventId);
        model.addAttribute(event);
        return "/events/edit";
    }

    @PostMapping("/edit")
    public String processEditPage(@Valid BuddyBook buddyBook, BindingResult result,
                                  @RequestParam Long authorId,
                                  RedirectAttributes attributes) throws NotExistingRecordException {
//        log.debug("Preparing to edit entity {}", buddyBook);
//        if (result.hasErrors()) {
//            log.warn("Entity {} has failed validation! Return to edit view", buddyBook);
//            return "/books/edit";
//        }
//        buddyBookService.updateBuddyBook(buddyBook);
//        attributes.addAttribute("authorId", authorId);
        return "redirect:/app/myBooks/{authorId}";
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
