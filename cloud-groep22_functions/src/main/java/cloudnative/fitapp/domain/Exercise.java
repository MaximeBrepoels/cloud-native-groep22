package cloudnative.fitapp.domain;

import cloudnative.fitapp.enums.WorkoutType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Exercise {

    private String id;
    private String name;
    private WorkoutType type = WorkoutType.WEIGHTS;
    private int rest = 60;
    private Boolean autoIncrease = true;
    private double autoIncreaseFactor = 1.05;
    private double autoIncreaseWeightStep = 2.5;
    private double autoIncreaseStartWeight = 20;
    private int autoIncreaseMinSets = 3;
    private int autoIncreaseMaxSets = 5;
    private int autoIncreaseMinReps = 8;
    private int autoIncreaseMaxReps = 12;
    private int autoIncreaseStartDuration = 30;
    private int autoIncreaseDurationSets = 3;
    private int autoIncreaseCurrentSets = 3;
    private int autoIncreaseCurrentReps = 8;
    private int autoIncreaseCurrentDuration = 30;
    private double autoIncreaseCurrentWeight = 20;
    private List<Set> sets = new ArrayList<>();
    private int orderIndex;
    private List<Progress> progressList = new ArrayList<>();

    @JsonIgnore
    private transient Workout workout;

    public Exercise() {
    }

    public Exercise(String name) {
        this.name = name;
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public Exercise(String name, WorkoutType type, String goal) {
        this.name = name;
        this.type = type;
        this.id = String.valueOf(System.currentTimeMillis());

        if (type != WorkoutType.DURATION) {
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

    public void addProgress(Progress progress) {
        if (this.progressList == null) {
            this.progressList = new ArrayList<>();
        }
        this.progressList.add(progress);
    }

    public void removeProgress(Progress progress) {
        if (progressList != null) {
            progressList.remove(progress);
        }
    }

    public void clearProgress() {
        if (progressList != null) {
            progressList.clear();
        }
    }

    public void setSets(List<Set> sets) {
        this.sets = sets != null ? sets : new ArrayList<>();
    }

    public Set addSet(Set set) {
        if (sets == null) {
            sets = new ArrayList<>();
        }
        set.setId(System.currentTimeMillis() + "_" + sets.size());
        sets.add(set);
        set.setExercise(this);
        return set;
    }
}