package cloudnative.fitapp.controller;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    @Autowired
    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public Workout createWorkout(@RequestBody Workout workoutIn, @RequestParam Long userId) {
        Workout workout = workoutService.createWorkout(workoutIn.getName(), String.valueOf(userId));
        return workout;
    }

    @GetMapping
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        List<Workout> workouts = workoutService.getAllWorkouts();
        return new ResponseEntity<>(workouts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workout> getWorkoutById(@PathVariable Long id) {
        Optional<Workout> workout = workoutService.getWorkoutById(String.valueOf(id));
        return workout.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Workout>> getWorkoutsByUserId(@PathVariable Long userId) {
        List<Workout> workouts = workoutService.getWorkoutsByUserId(String.valueOf(userId));
        return new ResponseEntity<>(workouts, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(String.valueOf(id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public Workout updateWorkout(@PathVariable Long id, @RequestBody WorkoutUpdateRequest updateRequest) {
        List<String> exerciseIds = updateRequest.getExerciseIds() != null
                ? updateRequest.getExerciseIds().stream().map(String::valueOf).collect(Collectors.toList())
                : null;
        return workoutService.updateWorkout(
                String.valueOf(id),
                updateRequest.getName(),
                updateRequest.getRest(),
                exerciseIds
        );
    }

    @PostMapping("/{id}/addExercise/{goal}")
    public Exercise addExerciseToWorkout(@PathVariable Long id, @RequestBody Exercise exercice, @PathVariable String goal) {
        return workoutService.addExerciseToWorkout(String.valueOf(id), exercice, goal);
    }

}

@Getter
@Setter
class WorkoutUpdateRequest {
    private String name;
    private Integer rest;
    private List<Long> exerciseIds;
}