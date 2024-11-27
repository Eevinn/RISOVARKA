package backend.controller;

import backend.model.Board;
import backend.model.Shape;
import backend.repo.BoardRepo;
import backend.services.ShapeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/shapes")
public class ShapeController {

    @Autowired
    private ShapeService shapeService;

    @Autowired
    private BoardRepo boardRepo;

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<Shape>> getShapesByBoard(@PathVariable int boardId, Authentication authentication) {
        String username = authentication.getName();
        Optional<Board> optionalBoard = boardRepo.findById(boardId);
        Board board = optionalBoard.get();
        List<Shape> shapes = shapeService.getShapesByBoard(board);
        return new ResponseEntity<>(shapes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Shape> addShape(@RequestBody Shape shape, Authentication authentication) {
        String username = authentication.getName();
        Board board = shape.getBoard();
        Shape savedShape = shapeService.saveShape(shape);
        return new ResponseEntity<>(savedShape, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shape> updateShape(@PathVariable int id, @RequestBody Shape shape, Authentication authentication) {
        Optional<Shape> optionalShape = shapeService.getShapeById(id);
        Shape existingShape = optionalShape.get();
        existingShape.setShape(shape.getShape());
        Shape updatedShape = shapeService.saveShape(existingShape);
        return new ResponseEntity<>(updatedShape, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShape(@PathVariable int id, Authentication authentication) {
        Optional<Shape> optionalShape = shapeService.getShapeById(id);
        Shape shape = optionalShape.get();
        shapeService.deleteShape(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}