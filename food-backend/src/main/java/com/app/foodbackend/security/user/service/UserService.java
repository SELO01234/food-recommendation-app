package com.app.foodbackend.security.user.service;

import com.app.foodbackend.food.repository.FoodRepository;
import com.app.foodbackend.security.auth.dto.RegisterRequest;
import com.app.foodbackend.security.user.entity.Role;
import com.app.foodbackend.security.user.entity.User;
import com.app.foodbackend.security.user.entity.UserFoodRating;
import com.app.foodbackend.security.user.repository.RoleRepository;
import com.app.foodbackend.security.user.repository.UserFoodRatingRepository;
import com.app.foodbackend.security.user.repository.UserRepository;
import com.app.foodbackend.security.user.requestDTO.UserRequest;
import com.app.foodbackend.util.UserUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FoodRepository foodRepository;
    private final UserFoodRatingRepository userFoodRatingRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @PostConstruct
    private void initializeDefaultUsers(){
        List<Role> roles = roleService.initializeDefaultRoles();

        List<User> users = new ArrayList<>();

        User adminUser = new User();
        User visitorUser = new User();

        for(int i=0; i < roles.size(); ++i){
            if(roles.get(i).getName().equals("ADMIN")){
                adminUser.setRole(roles.get(i));
            }
            if(roles.get(i).getName().equals("VISITOR")){
                visitorUser.setRole(roles.get(i));
            }
        }

        if(userRepository.numberOfRoles(1) == 0){
            adminUser.setFirstName("Furkan");
            adminUser.setLastName("Furkan");
            adminUser.setEmail("furkan@gmail.com");
            adminUser.setUserName("furkan");
            adminUser.setPassword(passwordEncoder.encode("123"));
            users.add(adminUser);
        }
        if(userRepository.numberOfRoles(2) == 0){
            visitorUser.setFirstName("Ali");
            visitorUser.setLastName("Ali");
            visitorUser.setEmail("ali@gmail.com");
            visitorUser.setUserName("ali");
            visitorUser.setPassword(passwordEncoder.encode("123"));
            users.add(visitorUser);
        }

        if(!users.isEmpty()){
            userRepository.saveAll(users);
        }
    }

    public void saveUser(RegisterRequest request) throws Exception {
        if(request == null || userRepository.existsByUserName(request.getUserName())){
            throw new Exception();
        }
        if(!roleRepository.existsByName(request.getRole().toUpperCase())){
            throw new Exception();
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleService.getRole(request.getRole().toUpperCase()))
                .build();

        userRepository.save(user);
    }

    public void deleteUser(Integer id) throws NullPointerException{
        if(userRepository.existsById(id) == false){
            throw new NullPointerException();
        }

        userRepository.deleteById(id);
        SecurityContextHolder.clearContext();
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Transactional
    public void updateUser(UserRequest user) throws NullPointerException {

        User existingUser = userRepository.findById(user.getId()).orElseThrow(() -> new NullPointerException("User not found in the database"));

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setUserName(user.getUserName());
        existingUser.setEmail(user.getEmail());

        userRepository.save(existingUser);
    }

    public void controlRole(Integer id) throws Exception {
        // Get the authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user has the 'ADMIN' role
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if(isAdmin == false){
            if(!checkId(authentication.getName(), id)){
                throw new Exception();
            }
        }
    }

    public boolean checkId(String username, Integer id) {
        User user = userRepository.findByUserName(username).orElseThrow();
        if(user.getId() != id){
            return false;
        }
        return true;
    }

    @Transactional
    public void addVisitedFood(Integer foodId) throws Exception {

        if(foodRepository.existsById(foodId) == false){
            throw new Exception();
        }

        String username = UserUtil.getUsername();

        User user = userRepository.findByUserName(username).orElseThrow();

        if(userFoodRatingRepository.isFoodExistsByIds(user.getId(), foodId)){
            throw new Exception();
        }

        UserFoodRating userFoodRating = UserFoodRating.builder()
                .user(user)
                .food(foodRepository.findById(foodId).orElseThrow())
                .build();

        userFoodRatingRepository.save(userFoodRating);
    }

    public List<String> findVisitedFoodsByUserId() throws Exception{

        String username = UserUtil.getUsername();

        User user = userRepository.findByUserName(username).orElseThrow();

        return userFoodRatingRepository.findFoodNamesByUserId(user.getId());
    }
}
