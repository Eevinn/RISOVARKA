package backend.services;

import backend.model.Board;
import backend.model.Shape;
import backend.repo.ShapeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShapeService {

    @Autowired
    private ShapeRepo shapeRepo;

    public Shape saveShape(Shape shape) {
        return shapeRepo.save(shape);
    }

    public List<Shape> getShapesByBoard(Board board) {
        return shapeRepo.findAllByBoard(board);
    }

    public void deleteShape(int id) {
        shapeRepo.deleteById(id);
    }

    public Optional<Shape> getShapeById(int id) {
        return shapeRepo.findById(id);
    }
}