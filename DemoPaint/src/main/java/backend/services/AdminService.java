package backend.services;

import backend.model.LoginTimestamp;
import backend.model.Person;
import backend.repo.BoardRepo;
import backend.repo.LoginTimestampRepository;
import backend.repo.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.time.LocalDate;

@Service
public class AdminService {

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private LoginTimestampRepository loginTimestampRepository;

    @Autowired
    private BoardRepo boardRepo;

    //число зарегистрированных пользователей
    public long countRegisteredUsers() {
        return personRepo.count() - 1;
    }

    //всего создано досок
    public long countTotalBoards() {
        return boardRepo.count();
    }

    //общее время онлайн
    public long calculateTotalOnlineTime() {
        List<LoginTimestamp> timestamps = loginTimestampRepository.findAll();
        return timestamps.stream()
                .filter(ts -> ts.getLogoutTime() != null)
                .mapToLong(ts -> java.time.Duration.between(ts.getLoginTime(), ts.getLogoutTime()).getSeconds())
                .sum();
    }

    //всего забанено
    public long countBannedUsers() {
        return personRepo.findAll().stream()
                .filter(client -> "ROLE_BANNED".equals(client.getRole()))
                .count();
    }

    //число новых пользователей за определенные дни
    public long countNewUsersToday(int days) {
        LocalDate cutoff = LocalDate.now().minusDays(days);
        return personRepo.findAll().stream()
                .filter(person -> person.getCreatedAt().isAfter(cutoff.atStartOfDay()))
                .filter(person -> !"ROLE_ADMIN".equals(person.getRole()))
                .count();
    }

    //количество активных пользователей сейчас
    public long countActiveUsersNow() {
        return loginTimestampRepository.countByLogoutTimeIsNullAndPersonRoleNot("ROLE_ADMIN");
    }

    //количество активных пользователей сегодня
    public long countactiveUsersToday(int days) {
        LocalDate cutoff = LocalDate.now().minusDays(days);
        return loginTimestampRepository.findAll().stream()
                .filter(ts -> ts.getLoginTime().isAfter(cutoff.atStartOfDay()))
                .map(LoginTimestamp::getPerson)
                .filter(person -> !"ROLE_ADMIN".equals(person.getRole()))
                .distinct()
                .count();
    }

    //самые активные пользователи
    public List<Map.Entry<Person, Long>> getTopUsersByOnlineTime(int limit) {
        Map<Person, Long> userOnlineTime = loginTimestampRepository.findAll().stream()
                .filter(ts -> ts.getLogoutTime() != null)
                .filter(ts -> !"ROLE_ADMIN".equals(ts.getPerson().getRole()))
                .collect(Collectors.groupingBy(
                        LoginTimestamp::getPerson,
                        Collectors.summingLong(ts -> java.time.Duration.between(ts.getLoginTime(), ts.getLogoutTime()).getSeconds())
                ));

        return userOnlineTime.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .collect(Collectors.toList());
    }

}
