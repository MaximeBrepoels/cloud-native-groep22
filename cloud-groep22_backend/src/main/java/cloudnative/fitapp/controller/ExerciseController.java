package cloudnative.fitapp.controller;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    @Autowired
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public List<Exercise> getAllExercises() {
        return exerciseService.getAllExercises();
    }

    @PostMapping
    public ResponseEntity<Exercise> createExercise(@RequestBody Exercise exercise, @RequestParam Long workoutId) {
        if (exercise.getName() != null && exercise.getType() == null) {
            Exercise createdExercise = exerciseService.createExerciseByName(exercise.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExercise);
        }
        Exercise createdExercise = exerciseService.createExercise(exercise, workoutId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExercise);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Long id) {
        Exercise exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(exercise);
    }

    @GetMapping("/workout/{workoutId}")
    public ResponseEntity<List<Exercise>> getExercisesByWorkoutId(@PathVariable Long workoutId) {
        List<Exercise> exercises = exerciseService.getExercisesByWorkoutId(workoutId);
        return new ResponseEntity<>(exercises, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public Exercise updateExercise(@PathVariable Long id, @RequestBody Exercise exercise) {
        return exerciseService.updateExercise(id, exercise);
    }

    @DeleteMapping("/workout/{workoutId}/exercise/{exerciseId}")
    public ResponseEntity<String> deleteExerciseFromWorkout(@PathVariable Long workoutId,
            @PathVariable Long exerciseId) {
        String response = exerciseService.deleteExerciseFromWorkout(workoutId, exerciseId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/increase/{id}")
    public Exercise autoIncrease(@PathVariable Long id) {
        return exerciseService.autoIncrease(id);
    }

    @PutMapping("/decrease/{id}")
    public Exercise autoDecrease(@PathVariable Long id) {
        return exerciseService.autoDecrease(id);
    }

    @GetMapping("/user/{userId}")
    public List<Exercise> getExercisesByUserId(@PathVariable Long userId) {
        return exerciseService.getExercisesByUserId(userId);
    }

}
