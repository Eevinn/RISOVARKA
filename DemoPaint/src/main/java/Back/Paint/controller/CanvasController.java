package Back.Paint.controller;

import Back.Paint.domain.*;
import Back.Paint.repo.CanvasRepo;
import Back.Paint.repo.MessageRepo;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;




import Back.Paint.domain.Message;
import Back.Paint.domain.Views;
import Back.Paint.repo.MessageRepo;
import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

        import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("canvas")
public class CanvasController {
    private final CanvasRepo canvasRepo;

    @Autowired
    public CanvasController(CanvasRepo canvasRepo) {
        this.canvasRepo = canvasRepo;
    }

    @GetMapping
    public List<Canvas> list() {
        return canvasRepo.findAll();
    }

    @GetMapping("{id}")
    public Canvas getOne(@PathVariable("id") Canvas canvas) {
        return canvas;
    }

    @PostMapping
    public Canvas create(@RequestBody Canvas canvas) {
        return canvasRepo.save(canvas);
    }

    @PutMapping("{id}")
    public Canvas update(
            @PathVariable("id") Canvas messageFromDb,
            @RequestBody Canvas canvas
    ) {
        BeanUtils.copyProperties(canvas, messageFromDb, "id");

        return canvasRepo.save(messageFromDb);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Canvas canvas) {
        canvasRepo.delete(canvas);
    }
}
