package cloudnative.fitapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String id;
    private String name;
    private String email;
    private String password;
    private List<String> workoutIds = new ArrayList<>();
    private List<Bodyweight> bodyweightList = new ArrayList<>();
    private Integer streakGoal = 0;
    private Integer streakProgress = 0;
    private Integer streak = 0;

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.workoutIds = new ArrayList<>();
        this.bodyweightList = new ArrayList<>();
    }

    public void updateValuesUser(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public List<Bodyweight> getBodyweight() {
        return bodyweightList;
    }

    public void setBodyweight(List<Bodyweight> bodyweight) {
        this.bodyweightList = bodyweight;
    }
}
