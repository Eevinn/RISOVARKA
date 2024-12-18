package backend.controller;

import backend.model.Shape;
import backend.model.ShapeMessage;
import backend.services.ShapeService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ShapeService shapeService;

    public WebSocketController(SimpMessagingTemplate messagingTemplate, ShapeService shapeService) {
        this.messagingTemplate = messagingTemplate;
        this.shapeService = shapeService;
    }

    @MessageMapping("/board/{boardId}/shape")
    public void handleShapeMessage(@DestinationVariable String boardId, ShapeMessage message) {
        if (message == null || message.getAction() == null || message.getShape() == null) {
            // Можно добавить логирование или возврат ошибки
            return;
        }

        String action = message.getAction().toLowerCase();
        Shape shape = message.getShape();

        switch (action) {
            case "create":
                // Сохранение новой фигуры
                shapeService.saveShape(shape);
                break;
            case "update":
                // Обновление существующей фигуры
                shapeService.saveShape(shape);
                break;
            case "delete":
                // Удаление фигуры по ее ID
                if (shape.getId() != 0) {
                    shapeService.deleteShape(shape.getId());
                }
                break;
            default:
                // Неизвестное действие — можно добавить логирование или обработку ошибки
                break;
        }

        // Рассылка обновлений всем подписчикам указанной доски
        messagingTemplate.convertAndSend("/topic/board/" + boardId + "/shape", message);
    }

}
