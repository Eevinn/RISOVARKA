package Back.DemoPaint.service;

import Back.DemoPaint.storage.UserStorage;
import Back.DemoPaint.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageService {

    private UserStorage userstorage;
    private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    public void setUserstorage(UserStorage userstorage) {
        this.userstorage = userstorage;
    }

    @Autowired
    @Qualifier("storageServicePasswordEncoder")
    public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean addUser(String username, String password, String email) {
        if (userstorage.existsByLogin(username)) {
            return false;
        }
        else {
            userstorage.save(new User(null, username, passwordEncoder.encode(password), email,"ROLE_USER"));
            return true;
        }
    }

    public User getUser(int id) {
        return  userstorage.findById(id);
    }

    public List<User> getAllUsers() {
        return userstorage.findAllByRole("ROLE_USER");
    }

    public void deleteUser(int id) {
        userstorage.deleteById(id);
    }

}
