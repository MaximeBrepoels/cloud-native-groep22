package cloudnative.fitapp.dto;

import cloudnative.fitapp.domain.User;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import cloudnative.fitapp.domain.Bodyweight;

@Getter
@Setter
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private List<String> workoutIds;
    private List<Bodyweight> bodyweightList;
    private Integer streakGoal;
    private Integer streakProgress;
    private Integer streak;

    public UserResponse(String id, String name, String email, List<String> workoutIds,
                        List<Bodyweight> bodyweightList, Integer streakGoal,
                        Integer streakProgress, Integer streak) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.workoutIds = workoutIds;
        this.bodyweightList = bodyweightList;
        this.streakGoal = streakGoal;
        this.streakProgress = streakProgress;
        this.streak = streak;
    }

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.workoutIds = user.getWorkoutIds();
        this.bodyweightList = user.getBodyweightList();
        this.streakGoal = user.getStreakGoal();
        this.streakProgress = user.getStreakProgress();
        this.streak = user.getStreak();
    }
}