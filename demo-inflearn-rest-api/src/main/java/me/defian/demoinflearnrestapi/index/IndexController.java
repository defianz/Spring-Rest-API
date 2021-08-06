package me.defian.demoinflearnrestapi.index;

import me.defian.demoinflearnrestapi.events.EventController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
public class IndexController {

    @GetMapping("/api")
    public ResponseEntity index(){
        var index = new RepresentationModel<>();
        index.add(linkTo(EventController.class).withRel("events"));
        return ResponseEntity.ok(index);
    }
//    @GetMapping("/api")
//    public RepresentationModel index(){
//        var index = new RepresentationModel<>();
//        index.add(linkTo(EventController.class).withRel("events"));
//        return index;
//    }
}
