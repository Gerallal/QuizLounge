package de.thb.quizlounge.service;

import de.thb.quizlounge.entity.FriendRequest;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.repository.FriendRequestRepository;
import de.thb.quizlounge.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FriendRequestService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public void sendFriendRequest(String receiverUsername, User sender) {
        // Sicherstellen, dass der Sender persistiert ist
        sender = userRepository.findById(sender.getId())
                .orElseThrow(() -> new RuntimeException("Sender nicht gefunden"));

        // Empfänger aus der Datenbank laden
        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Empfänger nicht gefunden"));

        if (receiver.equals(sender)) {
            throw new RuntimeException("Du kannst dir selbst keine Freundschaftsanfrage senden.");
        }

        // Neues FriendRequest-Objekt erstellen und setzen
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setAccepted(false);

        // Speichern
        friendRequestRepository.save(request);
    }
}

