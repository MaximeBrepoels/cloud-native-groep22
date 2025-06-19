package cloudnative.fitapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Set {

    private String id;
    private int reps;
    private int weight;
    private int duration;

    @JsonIgnore
    private transient Exercise exercise;

    public Set() {
    }

    public Set(int reps, int weight, int duration) {
        this.reps = reps;
        this.weight = weight;
        this.duration = duration;
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public Long getId() {
        try {
            return Long.parseLong(this.id);
        } catch (NumberFormatException e) {
            return this.id.hashCode() & 0xffffffffL;
        }
    }

    public void setId(Long id) {
        this.id = String.valueOf(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void updateValuesSet(int reps, int weight, int duration, Exercise exercise) {
        this.reps = reps;
        this.weight = weight;
        this.duration = duration;
        this.exercise = exercise;
    }
}