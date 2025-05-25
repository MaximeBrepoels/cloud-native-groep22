package cloudnative.fitapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Getter
@Setter
@Entity
@Table(name = "progress")
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    @JsonBackReference
    private Exercise exercise;

    private Double weight;

    private Integer duration;
    
    private Date date;

    public Progress() {
    }

    public Progress(double weight, Date date) {
        this.weight = weight;
        this.date = date;
    }

    public Progress(int duration, Date date) {
        this.duration = duration;
        this.date = date;
    }

}