package cloudnative.fitapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "sets")
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int reps;

    private int weight;

    private int duration;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    @JsonBackReference
    private Exercise exercise;

    public Set() {
    }

    public Set(int reps, int weight, int duration) {
        this.reps = reps;
        this.weight = weight;
        this.duration = duration;
    }

    public void updateValuesSet(int reps, int weight, int duration, Exercise exercise) {
        this.reps = reps;
        this.weight = weight;
        this.duration = duration;
        this.exercise = exercise;
    }
}
