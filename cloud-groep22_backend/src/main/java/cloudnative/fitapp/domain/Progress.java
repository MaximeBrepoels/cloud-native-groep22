package cloudnative.fitapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Progress {

    private String id;
    private Double weight;
    private Integer duration;
    private Date date;

    @JsonIgnore
    private transient Exercise exercise;

    public Progress() {
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public Progress(double weight, Date date) {
        this.weight = weight;
        this.date = date;
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public Progress(int duration, Date date) {
        this.duration = duration;
        this.date = date;
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
}