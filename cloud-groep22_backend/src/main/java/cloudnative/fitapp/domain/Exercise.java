package cloudnative.fitapp.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import cloudnative.fitapp.enums.WorkoutType;

@Getter
@Setter
@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    @JsonBackReference
    private Workout workout;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private WorkoutType type = WorkoutType.WEIGHTS;

    @Column(nullable = false)
    private int rest = 60;

    @Column(nullable = false)
    private Boolean autoIncrease = true;

    @Column(nullable = false)
    private double autoIncreaseFactor = 1.05;

    @Column(nullable = false)
    private double autoIncreaseWeightStep = 2.5;

    @Column(nullable = false)
    private double autoIncreaseStartWeight = 20;

    @Column(nullable = false)
    private int autoIncreaseMinSets = 3;

    @Column(nullable = false)
    private int autoIncreaseMaxSets = 5;

    @Column(nullable = false)
    private int autoIncreaseMinReps = 8;

    @Column(nullable = false)
    private int autoIncreaseMaxReps = 12;

    @Column(nullable = false)
    private int autoIncreaseStartDuration = 30;

    @Column(nullable = false)
    private int autoIncreaseDurationSets = 3;

    @Column(nullable = false)
    private int autoIncreaseCurrentSets = 3;

    @Column(nullable = false)
    private int autoIncreaseCurrentReps = 8;

    @Column(nullable = false)
    private int autoIncreaseCurrentDuration = 30;

    @Column(nullable = false)
    private double autoIncreaseCurrentWeight = 20;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Set> sets = new ArrayList<>();

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Progress> progressList = new ArrayList<>();

    public Exercise() {
    }

    public Exercise(String name) {
        this.name = name;
    }

    public Exercise(String name, WorkoutType type, String goal) {
        this.name = name;
        this.type = type;
        if (type != WorkoutType.DURATION){
            switch (goal) {
                case "POWER":
                    this.rest = 240;
                    this.autoIncreaseFactor = 1.05;
                    this.autoIncreaseWeightStep = 2.5;
                    this.autoIncreaseStartWeight = 40;
                    this.autoIncreaseMinSets = 3;
                    this.autoIncreaseMaxSets = 4;
                    this.autoIncreaseMinReps = 2;
                    this.autoIncreaseMaxReps = 8;
                    this.autoIncreaseCurrentSets = 3;
                    this.autoIncreaseCurrentReps = 2;
                    this.autoIncreaseCurrentWeight = 40;
                    break;
                case "MUSCLE":
                    this.rest = 180;
                    this.autoIncreaseFactor = 1.1;
                    this.autoIncreaseWeightStep = 2.5;
                    this.autoIncreaseStartWeight = 20;
                    this.autoIncreaseMinSets = 3;
                    this.autoIncreaseMaxSets = 4;
                    this.autoIncreaseMinReps = 8;
                    this.autoIncreaseMaxReps = 15;
                    this.autoIncreaseCurrentSets = 3;
                    this.autoIncreaseCurrentReps = 8;
                    this.autoIncreaseCurrentWeight = 20;
                    break;
                case "ENDURANCE":
                    this.rest = 90;
                    this.autoIncreaseFactor = 1.15;
                    this.autoIncreaseWeightStep = 2.5;
                    this.autoIncreaseStartWeight = 10;
                    this.autoIncreaseMinSets = 3;
                    this.autoIncreaseMaxSets = 4;
                    this.autoIncreaseMinReps = 12;
                    this.autoIncreaseMaxReps = 20;
                    this.autoIncreaseCurrentSets = 3;
                    this.autoIncreaseCurrentReps = 12;
                    this.autoIncreaseCurrentWeight = 10;
                    break;
                default:
                    break;
            } 
        }
        
    }

    public Exercise(String name, WorkoutType type, int rest, Boolean autoIncrease, int autoIncreaseFactor,
            double autoIncreaseWeightStep, double autoIncreaseStartWeight, int autoIncreaseMinSets,
            int autoIncreaseMaxSets, int autoIncreaseMinReps, int autoIncreaseMaxReps,
            int autoIncreaseStartDuration, int autoIncreaseDurationSets, int autoIncreaseCurrentSets,
            int autoIncreaseCurrentReps, int autoIncreaseCurrentDuration) {
        this.name = name;
        this.type = type;
        this.rest = rest;
        this.autoIncrease = autoIncrease;
        this.autoIncreaseFactor = autoIncreaseFactor;
        this.autoIncreaseWeightStep = autoIncreaseWeightStep;
        this.autoIncreaseStartWeight = autoIncreaseStartWeight;
        this.autoIncreaseMinSets = autoIncreaseMinSets;
        this.autoIncreaseMaxSets = autoIncreaseMaxSets;
        this.autoIncreaseMinReps = autoIncreaseMinReps;
        this.autoIncreaseMaxReps = autoIncreaseMaxReps;
        this.autoIncreaseStartDuration = autoIncreaseStartDuration;
        this.autoIncreaseDurationSets = autoIncreaseDurationSets;
        this.autoIncreaseCurrentSets = autoIncreaseCurrentSets;
        this.autoIncreaseCurrentReps = autoIncreaseCurrentReps;
        this.autoIncreaseCurrentDuration = autoIncreaseCurrentDuration;
        this.sets = new ArrayList<>();
        this.orderIndex = 0;
    }

    public void addProgress(Progress progress) {
        this.progressList.add(progress);
    }

    public void removeProgress(Progress progress) {
        progressList.remove(progress);
    }

    public void clearProgress() {
        progressList.clear();
    }

    public void setSets(List<Set> sets) {
        this.sets = sets != null ? sets : new ArrayList<>();
    }

    public Set addSet(Set set) {
        sets.add(set);
        set.setExercise(this);
        return set;
    }

}
