package backend.controller;

import backend.model.Board;
import backend.model.Shape;
import backend.repo.BoardRepo;
import backend.services.ShapeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/shapes")
public class ShapeController {

    private final ShapeService shapeService;
    private final BoardRepo boardRepo;

    public ShapeController(ShapeService shapeService, BoardRepo boardRepo) {
        this.shapeService = shapeService;
        this.boardRepo = boardRepo;
    }

    /**
     * Получение списка фигур для указанной доски.
     */
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<Shape>> getShapesByBoard(@PathVariable int boardId, Authentication authentication) {
        // Проверяем наличие доски
        Optional<Board> optionalBoard = boardRepo.findById(boardId);
        if (optionalBoard.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Board board = optionalBoard.get();
        // Опционально можно проверить права доступа пользователя, используя authentication

        List<Shape> shapes = shapeService.getShapesByBoard(board);
        return ResponseEntity.ok(shapes);
    }

    /**
     * Добавление новой фигуры.
     */
    @PostMapping
    public ResponseEntity<Shape> addShape(@RequestBody Shape shape, Authentication authentication) {
        // Можно проверить: является ли пользователь владельцем доски shape.getBoard()?
        // Если нет, вернуть 403 Forbidden.
        if (shape.getBoard() == null) {
            return ResponseEntity.badRequest().build();
        }

        Shape savedShape = shapeService.saveShape(shape);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShape);
    }

    /**
     * Обновление фигуры.
     */
    @PutMapping
    public ResponseEntity<Shape> updateShape(@RequestBody Shape shape, Authentication authentication) {
        int id = shape.getId();
        Optional<Shape> optionalShape = shapeService.getShapeById(id);

        if (optionalShape.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Shape existingShape = optionalShape.get();
        // Проверяем права доступа, если нужно.

        existingShape.setShape(shape.getShape());
        Shape updatedShape = shapeService.saveShape(existingShape);
        return ResponseEntity.ok(updatedShape);
    }

    /**
     * Удаление фигуры.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShape(@PathVariable int id, Authentication authentication) {
        Optional<Shape> optionalShape = shapeService.getShapeById(id);

        if (optionalShape.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Проверяем права доступа, если требуется.

        shapeService.deleteShape(id);
        return ResponseEntity.noContent().build();
    }
}
