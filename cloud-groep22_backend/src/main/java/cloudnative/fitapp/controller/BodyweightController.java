package cloudnative.fitapp.controller;

import cloudnative.fitapp.domain.Bodyweight;
import cloudnative.fitapp.service.BodyweightService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bodyweight")
public class BodyweightController {

    private final BodyweightService bodyweightService;


    public BodyweightController(BodyweightService bodyweightService) {
        this.bodyweightService = bodyweightService;
    }

    @GetMapping
    public List<Bodyweight> getAllProgress() {
        return bodyweightService.getAllBodyweight();
    }

    @GetMapping("/{id}")
    public List<Bodyweight> getBodyweightByUserId(@PathVariable Long id) {
        return bodyweightService.getBodyweightByUserId(id);
    }

    @PostMapping("/add/{userId}")
    public Bodyweight addBodyweight(@PathVariable Long userId, @RequestBody Bodyweight bodyweight) {
        return bodyweightService.addBodyweight(userId, bodyweight);
    }
}
