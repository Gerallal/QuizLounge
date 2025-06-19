package de.thb.quizlounge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Attempt implements Comparable{
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
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


    private Integer rating;

    public void setStartTime() {
        this.startTime = LocalDateTime.now();
    }
    public void setEndTime() {
        this.endTime = LocalDateTime.now();
    }
    public Duration getDuration(){
        if(this.startTime != null && this.endTime != null){
            this.duration = Duration.between(this.startTime, this.endTime);
        }

        return this.duration;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Attempt){
            if(this.getDuration() == null || ((Attempt) o).getDuration() == null){
                return -1;
            }
            if(this.getNumberOfRightAnswers() == ((Attempt) o).getNumberOfRightAnswers()){
                int comparison = this.getDuration().compareTo(((Attempt) o).getDuration());
                return (Integer.compare(comparison, 0)) * -1;
            }
            if(this.getNumberOfRightAnswers() < ((Attempt) o).getNumberOfRightAnswers()){
                return -1;
            }
            return 1;
        }
        return 0;
    }

    public void evaluate(Map<String,String> answers){
        answers.get(quiz.getQuestions().get(0).getQuestionname());
        int correctAnswers = 0;
        for(Question question : quiz.getQuestions()){
            if(answers.get(question.getQuestionname()) != null){
            if(answers.get(question.getQuestionname()).equals(question.getRightAnswer())){
                correctAnswers++;
            }
        }}
        this.numberOfRightAnswers = correctAnswers;
    }
}
