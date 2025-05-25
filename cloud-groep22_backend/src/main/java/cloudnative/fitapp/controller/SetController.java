package cloudnative.fitapp.controller;

import cloudnative.fitapp.domain.Set;
import cloudnative.fitapp.service.SetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sets")
public class SetController {

    @Autowired
    private SetService setService;

    @GetMapping
    public List<Set> getAllSets() {
        return setService.getAllSets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Set> getSetById(@PathVariable Long id) {
        Set set = setService.getSetById(id);
        if (set == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(set);
    }

    @PostMapping("/exercise/{exerciseId}/addSet")
    public Set addSetToExercise(@PathVariable Long exerciseId, @RequestBody Set set) {
        return setService.addSetToExercise(exerciseId, set);
    }

    @PutMapping("/{id}")
    public Set updateSet(@PathVariable Long id, @RequestBody Set set) {
        return setService.updateSet(id, set);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSet(@PathVariable Long id) {
        if (setService.getSetById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        setService.deleteSet(id);
        return ResponseEntity.noContent().build();
    }
}