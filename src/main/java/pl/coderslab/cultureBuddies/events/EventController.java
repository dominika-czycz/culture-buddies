package pl.coderslab.cultureBuddies.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.city.CityRepository;
import pl.coderslab.cultureBuddies.exceptions.EmptyKeysException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;

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
    public String prepareAllPage() throws NotExistingRecordException {
        log.info("Preparing events page...");
        return "/events/events";
    }


    @GetMapping("/add")
    public String prepareAddPage(Model model) {
        log.info("Preparing add event page...");
        model.addAttribute(new Event());
        return "/events/add";
    }

    @PostMapping("/add")
    public String processAddPage(@Valid Event event, BindingResult result) throws NotExistingRecordException {
        log.debug("Entity to save:  {}", event);
        if (isNotValid(event, result)) return "/events/add";
        eventService.save(event);
        return "redirect:/app/myEvents/";
    }

    @GetMapping("/edit/{eventId}")
    public String prepareEditPage(@PathVariable Long eventId, Model model) throws NotExistingRecordException {
        log.debug("Preparing edit page of event with id {}", eventId);
        Event event = eventService.findEventById(eventId);
        model.addAttribute(event);
        return "/events/edit";
    }

    @PostMapping("/edit")
    public String processEditPage(@Valid Event event, BindingResult result) throws NotExistingRecordException {
        log.debug("Preparing to edit entity {}", event);
        if (isNotValid(event, result)) return "/events/edit";
        eventService.updateEvent(event);
        return "redirect:/app/myEvents/";
    }

    @GetMapping("/delete/{eventId}")
    public String prepareDeletePage(@PathVariable Long eventId, Model model) throws NotExistingRecordException {
        log.debug("Preparing delete page of event with id {}", eventId);
        final Event event = eventService.findEventByIdWithBuddies(eventId);
        if (event.getBuddies().size() > 1) {
            String errorMessage = "The event " + event.getTitle() + " cannot be deleted.  Buddies are already here.";
            model.addAttribute("errorMessage", errorMessage);
            log.warn("Entity {} cannot be deleted. Buddies are already here.", event);
            return "/events/events";
        }
        model.addAttribute(event);
        return "/events/delete";
    }

    @PostMapping("/delete")
    public String processDeletePage(@RequestParam Long eventId) throws NotExistingRecordException {
        log.debug("Preparing to delete event with id:  {}.", eventId);
        eventService.remove(eventId);
        return "redirect:/app/myEvents/";
    }

    @GetMapping("/cancel/{eventId}")
    public String prepareCancelPage(@PathVariable Long eventId, Model model) throws NotExistingRecordException {
        log.debug("Preparing cancel page of event with id {}", eventId);
        final Event event = eventService.findEventById(eventId);
        model.addAttribute(event);
        return "/events/cancel";
    }

    @PostMapping("/cancel")
    public String processCancelPage(@RequestParam Long eventId) throws NotExistingRecordException {
        log.debug("Preparing to leave event with id:  {}.", eventId);
        eventService.leave(eventId);
        return "redirect:/app/myEvents/";
    }


    private boolean isNotValid(Event event, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Entity {} fails validation.", event);
            return true;
        }
        return false;
    }

    @GetMapping("/info/{eventId}")
    public String processInfoPage(@PathVariable Long eventId, Model model) throws NotExistingRecordException {
        final Event event = eventService.findEventByIdWithBuddies(eventId);
        model.addAttribute(event);
        return "/events/info";
    }

    @PostMapping("/search")
    public String processResultsPage(@RequestParam(required = false) String username,
                                     @RequestParam(required = false) String title,
                                     @RequestParam(required = false) Long typeId,
                                     @RequestParam(required = false) String city, Model model) throws EmptyKeysException, NotExistingRecordException {
        log.debug("keywords: username {}, title {}, type id {} and city {}", username, title, typeId, city);
        log.info("Looking for events...");
        List<Event> events = eventService.findByUsernameTitleTypeIdOrCity(username, title, typeId, city);
        log.debug("{} results found", events.size());
        log.debug("{}", events);
        model.addAttribute("eventsToJoin", events);
        return "/events/join";
    }

    @PostMapping("/join")
    public String processJoinPage(@RequestParam Long eventId) throws NotExistingRecordException, RelationshipAlreadyCreatedException {
        log.debug("Preparing to join to the event with id {}.", eventId);
        eventService.joinEvent(eventId);
        return "redirect:/app/myEvents/";
    }

    @ModelAttribute("types")
    public List<EventType> types() {
        return eventService.findAllEventsTypes();
    }


    @ModelAttribute("buddy")
    public Buddy buddy() throws NotExistingRecordException {
        return buddyService.getPrincipal();
    }

    @ModelAttribute("events")
    public List<Event> events() throws NotExistingRecordException {
        return eventService.getEventsOfPrincipal();
    }

    @ModelAttribute("joined")
    public List<Event> joined() throws NotExistingRecordException {
        return eventService.getJoinedEvents();
    }

    @ModelAttribute("profilePictureDir")

    public String profilePictureDir() {
        return "/pictures/buddyPictures/";
    }

    @ModelAttribute("defaultPicture")
    public String defaultPicture() {
        return "defaultPicture.png";
    }
}
