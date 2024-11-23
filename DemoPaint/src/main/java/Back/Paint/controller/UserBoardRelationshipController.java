package Back.Paint.controller;

import Back.Paint.domain.UserBoardRelationship;
import Back.Paint.domain.UserBoardRelationshipId;
import Back.Paint.repo.UserBoardRelationshipRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-board-relationship")
public class UserBoardRelationshipController {

    private final UserBoardRelationshipRepo userBoardRelationshipRepo;

    @Autowired
    public UserBoardRelationshipController(UserBoardRelationshipRepo userBoardRelationshipRepo) {
        this.userBoardRelationshipRepo = userBoardRelationshipRepo;
    }

    // Метод для получения всех отношений пользователь-доска
    @GetMapping
    public List<UserBoardRelationship> getAllRelationships() {
        return userBoardRelationshipRepo.findAll();
    }

    // Метод для получения отношения пользователь-доска по ID
    @GetMapping("/{idBoard}/{idUser}")
    public UserBoardRelationship getRelationshipById(
            @PathVariable Long idBoard,
            @PathVariable Long idUser) {
        UserBoardRelationshipId id = new UserBoardRelationshipId();
        id.setIdBoard(idBoard);
        id.setIdUser(idUser);
        return userBoardRelationshipRepo.findById(id).orElse(null);
    }

    // Метод для создания нового отношения пользователь-доска
    @PostMapping
    public UserBoardRelationship createRelationship(@RequestBody UserBoardRelationship relationship) {
        return userBoardRelationshipRepo.save(relationship);
    }

    // Метод для обновления отношения пользователь-доска
    @PutMapping("/{idBoard}/{idUser}")
    public UserBoardRelationship updateRelationship(
            @PathVariable Long idBoard,
            @PathVariable Long idUser,
            @RequestBody UserBoardRelationship updatedRelationship
    ) {
        UserBoardRelationshipId id = new UserBoardRelationshipId();
        id.setIdBoard(idBoard);
        id.setIdUser(idUser);
        UserBoardRelationship relationshipFromDb = userBoardRelationshipRepo.findById(id).orElseThrow(() -> new RuntimeException("Relationship not found"));
        BeanUtils.copyProperties(updatedRelationship, relationshipFromDb, "id");
        return userBoardRelationshipRepo.save(relationshipFromDb);
    }

    // Метод для удаления отношения пользователь-доска
    @DeleteMapping("/{idBoard}/{idUser}")
    public void deleteRelationship(
            @PathVariable Long idBoard,
            @PathVariable Long idUser) {
        UserBoardRelationshipId id = new UserBoardRelationshipId();
        id.setIdBoard(idBoard);
        id.setIdUser(idUser);
        userBoardRelationshipRepo.deleteById(id);
    }
}
