package cloudnative.fitapp.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Bodyweight {

    private String id;
    private Double bodyWeight;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate date;

    @JsonIgnore
    private transient User user;

    public Bodyweight() {
        this.date = LocalDate.now();
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public Bodyweight(Double bodyWeight) {
        this.bodyWeight = bodyWeight;
        this.date = LocalDate.now();
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public Bodyweight(Double bodyWeight, LocalDate date) {
        this.bodyWeight = bodyWeight;
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