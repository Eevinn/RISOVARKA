package Back.Paint.controller;

import Back.Paint.domain.Logs;
import Back.Paint.domain.Views;
import Back.Paint.repo.LogsRepo;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
//Заготвка под WebSoket
@RestController
@RequestMapping("logs")
public class LogsController {

    private final LogsRepo logsRepo;

    @Autowired
    public LogsController(LogsRepo logsRepo) {
        this.logsRepo = logsRepo;
    }

    @GetMapping
    @JsonView(Views.IdName.class)
    public List<Logs> list() {
        return logsRepo.findAll();
    }

    @GetMapping("{id}")
    @JsonView(Views.FullLogs.class)
    public Logs getOne(@PathVariable("id") Logs logs) {
        return logs;
    }

    @PostMapping
    public Logs create(@RequestBody Logs logs) {
        logs.setCreationDate(LocalDateTime.now());
        return logsRepo.save(logs);
    }

    @PutMapping("{id}")
    @JsonView(Views.FullLogs.class)
    public Logs update(
            @PathVariable("id") Logs logsFromDb,
            @RequestBody Logs logs
    ) {
        BeanUtils.copyProperties(logs, logsFromDb, "id");
        return logsRepo.save(logsFromDb);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Logs logs) {
        logsRepo.delete(logs);
    }
}
