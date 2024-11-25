package backend.controller;

import backend.model.Board;
import backend.model.Person;
import backend.repo.BoardRepo;
import backend.services.PersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardRepo boardRepo;

    @Autowired
    private PersonService personService;


    @PostMapping
    public ResponseEntity<Board> createBoard(@Valid @RequestBody Board board, Authentication authentication) {
        String username = authentication.getName();
        Optional<Person> optionalPerson = personService.getPerson(username);
        Person person = optionalPerson.get();
        board.setUser(person);
        Board savedBoard = boardRepo.save(board);
        return new ResponseEntity<>(savedBoard, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoard(@PathVariable int id, Authentication authentication) {
        Optional<Board> boardOpt = boardRepo.findById(id);
        Board board = boardOpt.get();
        if (!board.getUser().getUsername().equals(authentication.getName())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable int id, @Valid @RequestBody Board boardDetails, Authentication authentication) {
        Optional<Board> boardOpt = boardRepo.findById(id);
        Board board = boardOpt.get();
        if (!board.getUser().getUsername().equals(authentication.getName())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        board.setName(boardDetails.getName());
        board.setText(boardDetails.getText());
        boardRepo.save(board);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable int id, Authentication authentication) {
        Optional<Board> boardOpt = boardRepo.findById(id);
        Board board = boardOpt.get();
        if (!board.getUser().getUsername().equals(authentication.getName())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        boardRepo.delete(board);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public String getAllBoards(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<Person> optionalPerson = personService.getPerson(username);
        if (optionalPerson.isEmpty()) {
            return "redirect:/login";
        }
        Person person = optionalPerson.get();
        List<Board> boards = boardRepo.findAllByUser(person);
        System.out.println("Количество досок для пользователя " + username + ": " + boards.size());
        model.addAttribute("boards", boards);
        model.addAttribute("person", person);
        return "account";
    }
}
