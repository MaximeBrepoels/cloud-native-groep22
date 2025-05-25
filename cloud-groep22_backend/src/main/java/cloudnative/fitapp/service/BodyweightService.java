package cloudnative.fitapp.service;

import cloudnative.fitapp.domain.Bodyweight;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.repository.BodyweightRepository;
import cloudnative.fitapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BodyweightService {

    @Autowired
    private BodyweightRepository bodyweightRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Bodyweight> getAllBodyweight() {
        return bodyweightRepository.findAll();
    }

    public List<Bodyweight> getBodyweightByUserId(Long id) {
        return bodyweightRepository.findByUserId(id);
    }

    public Bodyweight addBodyweight(Long userId, Bodyweight bodyweight) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));
        bodyweight.setUser(user);
        return bodyweightRepository.save(bodyweight);
    }
}