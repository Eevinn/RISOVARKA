package Back.DemoPaint.controller;

import Back.DemoPaint.service.StorageService;
import Back.DemoPaint.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/employee")
public class AdministratorController {
    private StorageService storageService;
    @Autowired
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/add")
    public String bookForm(Model model) {
        List<User> users = storageService.getAllUsers();
        model.addAttribute("users", users);
        return "add";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id, Model model) {
        storageService.deleteUser(id);
        return "redirect:/employee/add";
    }

}
