package cloudnative.fitapp.repository;

import cloudnative.fitapp.domain.Bodyweight;
import cloudnative.fitapp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BodyweightRepository {

    @Autowired
    private UserRepository userRepository;

    public List<Bodyweight> findAll() {
        List<Bodyweight> allBodyweights = new ArrayList<>();
        List<User> users = (List<User>) userRepository.findAll();
        for (User user : users) {
            if (user.getBodyweightList() != null) {
                for (Bodyweight bw : user.getBodyweightList()) {
                    bw.setUser(user);
                    allBodyweights.add(bw);
                }
            }
        }
        return allBodyweights;
    }

    public List<Bodyweight> findByUserId(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent() && userOpt.get().getBodyweightList() != null) {
            List<Bodyweight> bodyweights = userOpt.get().getBodyweightList();
            for (Bodyweight bw : bodyweights) {
                bw.setUser(userOpt.get());
            }
            return bodyweights;
        }
        return new ArrayList<>();
    }

    public Bodyweight save(Bodyweight bodyweight) {
        if (bodyweight.getUser() != null) {
            User user = bodyweight.getUser();
            if (user.getBodyweightList() == null) {
                user.setBodyweightList(new ArrayList<>());
            }

            // Check if bodyweight already exists
            boolean found = false;
            for (int i = 0; i < user.getBodyweightList().size(); i++) {
                if (user.getBodyweightList().get(i).getId().equals(bodyweight.getId())) {
                    user.getBodyweightList().set(i, bodyweight);
                    found = true;
                    break;
                }
            }

            if (!found) {
                user.getBodyweightList().add(bodyweight);
            }

            userRepository.save(user);
        }
        return bodyweight;
    }
}