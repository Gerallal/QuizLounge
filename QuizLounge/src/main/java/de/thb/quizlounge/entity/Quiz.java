package de.thb.quizlounge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;
    private String title;
    private String description;
    //private String author;

    @ManyToOne
    @JoinColumn(name = "author_id") // Optional: explizite Spalte
    private User author;

    @OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY)
    private List<Attempt> attempts;

    @OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY)
    private List<Question> questions;



}
