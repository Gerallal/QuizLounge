package de.thb.quizlounge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    long id;
    String username;
    String password;
    @ManyToMany
    private List<User> friends;
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<FriendRequest> friendRequests;

}
