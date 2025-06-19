package cloudnative.fitapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Workout {

    private String id;
    private String userId;
    private String name;
    private int rest;
    private List<Exercise> exercises = new ArrayList<>();

    @JsonIgnore
    private transient User user;

    public Workout() {
        this.exercises = new ArrayList<>();
    }

    public Workout(String name) {
        this.name = name;
        this.rest = 60;
        this.exercises = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = String.valueOf(user.getId());
        }
    }

    public List<Exercise> getExercises() {
        if (exercises != null) {
            exercises.sort((e1, e2) -> e1.getOrderIndex() - e2.getOrderIndex());
        }
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises != null ? exercises : new ArrayList<>();
    }

    public Exercise addExercise(Exercise exercise) {
        if (exercises == null) {
            exercises = new ArrayList<>();
        }
        exercise.setOrderIndex(exercises.size());
        exercise.setId(System.currentTimeMillis() + "_" + exercises.size());
        exercises.add(exercise);
        exercise.setWorkout(this);
        return exercise;
    }

    public void updateExercisesOrder(List<Exercise> orderedExercises) {
        for (int i = 0; i < orderedExercises.size(); i++) {
            orderedExercises.get(i).setOrderIndex(i);
        }
        this.exercises = orderedExercises;
    }

    public void updateValuesWorkout(String name, int rest, List<Exercise> exercises) {
        this.name = name;
        this.rest = rest;
        this.exercises = exercises != null ? exercises : new ArrayList<>();
    }
}