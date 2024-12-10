package backend.controller;


import backend.model.Shape;
import backend.model.ShapeMessage;
import backend.services.ShapeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ShapeService shapeService;

    @MessageMapping("/board/{boardId}/shape")
    public void handleShapeMessage(@DestinationVariable String boardId, ShapeMessage message) {
        String action = message.getAction();
        Shape shape = message.getShape();

        switch (action) {
            case "create":
                shapeService.saveShape(shape);//присвот 1
                break;
            case "update":
                shapeService.saveShape(shape);
                break;
            case "delete":
                shapeService.deleteShape(shape.getId());
                break;
        }

        messagingTemplate.convertAndSend("/topic/board/" + boardId + "/shape", message);
    }

}
