package me.defian.demoinflearnrestapi.events;


import me.defian.demoinflearnrestapi.accounts.Account;
import me.defian.demoinflearnrestapi.accounts.AccountAdapter;
import me.defian.demoinflearnrestapi.accounts.CurrentUser;
import me.defian.demoinflearnrestapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value="/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Validated EventDto eventDto, Errors errors,
                                      @CurrentUser Account currentUser
                                      ){

        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }
        eventValidator.validate(eventDto,errors);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManager(currentUser);
        Event newEvent = this.eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createUri = selfLinkBuilder.toUri();

        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<Event> pagedResourcesAssembler,
                                      @CurrentUser Account account){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User principal = (User) authentication.getPrincipal();
        Page<Event> page = this.eventRepository.findAll(pageable);
        var pagedResources = pagedResourcesAssembler.toModel(page, e -> new EventResource(e));
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        if(account != null){
            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(pagedResources);
    }


    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                   @CurrentUser Account currentUser
                                   ){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        } else {
            Event event = optionalEvent.get();
            EventResource eventResource = new EventResource(event);
            eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
            if(event.getManager().equals(currentUser)){
                eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
            }
            return ResponseEntity.ok(eventResource);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Validated EventDto eventDto,
                                      @CurrentUser Account currentUser,
                                      BindingResult bindingResult){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        this.eventValidator.validate(eventDto, bindingResult);
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        Event existingEvent = optionalEvent.get();
        if(!existingEvent.getManager().equals(currentUser)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        this.modelMapper.map(eventDto,existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);
        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }
}
