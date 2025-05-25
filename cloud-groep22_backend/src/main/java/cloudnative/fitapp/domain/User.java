package cloudnative.fitapp.domain;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Container(containerName = "users")
public class User {

    @Id
    private String id;

    private String name;

    @PartitionKey
    private String email;

    @JsonIgnore
    private String password;

    private List<String> workoutIds = new ArrayList<>();

    private List<Bodyweight> bodyweightList = new ArrayList<>();

    private Integer streakGoal = 0;
    private Integer streakProgress = 0;
    private Integer streak = 0;

    // Transient fields for compatibility
    @JsonIgnore
    private transient List<Workout> workouts = new ArrayList<>();

    @JsonIgnore
    private transient List<Bodyweight> bodyweight = new ArrayList<>();

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.workoutIds = new ArrayList<>();
        this.bodyweightList = new ArrayList<>();
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

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
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