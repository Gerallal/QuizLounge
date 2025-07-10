package de.thb.quizlounge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Attempt implements Comparable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private User user;

    @ManyToOne
    private Quiz quiz;

    private int numberOfRightAnswers;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;
    private boolean finished;
    private float score;

    public void setStartTime() {
        this.startTime = LocalDateTime.now();
    }

    public void setEndTime() {
        this.endTime = LocalDateTime.now();
    }

    public String getScoreInPercent() {
        return (((int) (this.getScore() * 1000)) / 10f) + "%";
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Attempt) {
            if (this.getDuration() == null || ((Attempt) o).getDuration() == null) {
                return -1;
            }
            if (this.getScore() == ((Attempt) o).getScore()) {
                int comparison = this.getDuration().compareTo(((Attempt) o).getDuration());
                return (Integer.compare(comparison, 0)) * -1;
            }
            if (this.getScore() < ((Attempt) o).getScore()) {
                return -1;
            }
            return 1;
        }
        return 0;
    }



}
