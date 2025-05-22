package de.thb.quizlounge.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FriendRequest{
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    private boolean accepted;



}
