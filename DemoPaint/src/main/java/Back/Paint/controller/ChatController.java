package Back.Paint.controller;
import Back.Paint.domain.Message;
import Back.Paint.repo.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final MessageRepo wsrepo;

    @Autowired
    public ChatController(MessageRepo messageRepo) {
        this.wsrepo = messageRepo;
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message) {
        message.setCreationDate(LocalDateTime.now());
        System.out.println(message);
        return wsrepo.save(message)
        ;
    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message) {
        System.out.println(message.toString());
        return message;

    }
}
