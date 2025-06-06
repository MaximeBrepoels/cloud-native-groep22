package cloudnative.fitapp.domain;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Container(containerName = "users")
public class User {

    @Id
    private String id;

    private String name;

    @PartitionKey
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

    // Don't store authorities in the database - generate them when needed
    @JsonIgnore
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