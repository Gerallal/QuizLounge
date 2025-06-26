package de.thb.quizlounge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;
    private String questionname;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String rightAnswer;

    @ManyToOne
    private Quiz quiz;
}
