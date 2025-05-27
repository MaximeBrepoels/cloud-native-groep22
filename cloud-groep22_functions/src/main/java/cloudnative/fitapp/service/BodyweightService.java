package cloudnative.fitapp.service;

import cloudnative.fitapp.domain.Bodyweight;
import cloudnative.fitapp.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java Bodyweight Service for Azure Functions.
 */
public class BodyweightService {

    private final CosmosDBService cosmosDBService;

    public BodyweightService(CosmosDBService cosmosDBService) {
        this.cosmosDBService = cosmosDBService;
    }

    public List<Bodyweight> getAllBodyweight() {
        List<Bodyweight> allBodyweights = new ArrayList<>();
        List<User> users = cosmosDBService.findAll("users", User.class);
        
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

    public List<Bodyweight> getBodyweightByUserId(Long id) {
        String query = String.format("SELECT * FROM c WHERE c.id = '%s'", id);
        List<User> users = cosmosDBService.query("users", query, User.class);
        
        if (!users.isEmpty() && users.get(0).getBodyweightList() != null) {
            List<Bodyweight> bodyweights = users.get(0).getBodyweightList();
            for (Bodyweight bw : bodyweights) {
                bw.setUser(users.get(0));
            }
            return bodyweights;
        }
        return new ArrayList<>();
    }

    public Bodyweight addBodyweight(Long userId, Bodyweight bodyweight) {
        String query = String.format("SELECT * FROM c WHERE c.id = '%s'", userId);
        List<User> users = cosmosDBService.query("users", query, User.class);
        
        if (users.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = users.get(0);
        bodyweight.setUser(user);
        bodyweight.setId(String.valueOf(System.currentTimeMillis() + (int)(Math.random() * 1000)));
        
        if (user.getBodyweightList() == null) {
            user.setBodyweightList(new ArrayList<>());
        }
        user.getBodyweightList().add(bodyweight);
        
        cosmosDBService.update("users", user, user.getEmail(), User.class);
        return bodyweight;
    }
}
