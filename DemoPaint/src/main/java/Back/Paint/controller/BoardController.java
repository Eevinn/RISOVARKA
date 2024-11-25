package Back.Paint.controller;

import Back.Paint.domain.Board;
import Back.Paint.domain.Message;
import Back.Paint.domain.Views;
import Back.Paint.repo.BoardRepo;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/board")
public class BoardController {

    private final BoardRepo boardRepo;

    @Autowired
    public BoardController(BoardRepo boardRepo) {
        this.boardRepo = boardRepo;
    }

    // Метод для получения всех досок
    @GetMapping
    public List<Board> getAllBoards() {
        return boardRepo.findAll();
    }

    // Метод для получения доски по ID
    @GetMapping("/{id}")
    public Board getBoardById(@PathVariable Long id) {
        return boardRepo.findById(id).orElse(null);
    }

    // Метод для создания новой доски
    @PostMapping
    public Board createBoard(@RequestBody Board board) {
        board.setCreationDate(LocalDateTime.now());
        return boardRepo.save(board);
    }

    // Метод для обновления доски
    @PutMapping("{id}")
    public Board update(
            @PathVariable("id") Board BoardFromDb,
            @RequestBody Board board
    ) {
        BeanUtils.copyProperties(board, BoardFromDb, "id");

        return boardRepo.save(BoardFromDb);
    }

    // Метод для удаления доски
    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable Long id) {
        boardRepo.deleteById(id);
    }

    //Метод для изменения имени доски
    @PutMapping("/{id}/updateName")
    public Board updateName(
            @PathVariable("id") Long id,
            @RequestBody String newName
    ) {
        Board boardFromDb = boardRepo.findById(id).orElseThrow(() -> new RuntimeException("Доска не найдена"));
        boardFromDb.setName(newName);
        return boardRepo.save(boardFromDb);
    }

}
