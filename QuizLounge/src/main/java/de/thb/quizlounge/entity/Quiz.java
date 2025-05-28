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
    private User author; //sollte am besten automatisch eingefügt werden, je nach User

    @OneToMany(mappedBy = "quiz", fetch = FetchType.EAGER)
    private List<Question> questions;


}
