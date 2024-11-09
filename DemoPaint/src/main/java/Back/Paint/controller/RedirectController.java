package Back.Paint.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("Redirect")
public class RedirectController {
    @GetMapping
    public ResponseEntity<Void> redirect(){
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://localhost:5173")).build();
    }
}


