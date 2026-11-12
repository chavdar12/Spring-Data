package com.example.exerciseSpringDataAutoMappingObjects.services;

import com.example.exerciseSpringDataAutoMappingObjects.config.ApplicationBeanConfiguration;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.GameAddDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserLoginDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserRegisterDto;
import com.example.exerciseSpringDataAutoMappingObjects.repositories.GameRepository;
import com.example.exerciseSpringDataAutoMappingObjects.repositories.OrderRepository;
import com.example.exerciseSpringDataAutoMappingObjects.repositories.UserRepository;
import com.example.exerciseSpringDataAutoMappingObjects.services.impl.GameServiceImpl;
import com.example.exerciseSpringDataAutoMappingObjects.services.impl.UserServiceImpl;
import com.example.exerciseSpringDataAutoMappingObjects.utilities.ValidationUtilImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(OutputCaptureExtension.class)
@Import({UserServiceImpl.class, GameServiceImpl.class, ValidationUtilImpl.class, ApplicationBeanConfiguration.class})
class GameStoreTest {

    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    private static GameAddDto overwatch() {
        return new GameAddDto("Overwatch", new BigDecimal("100.00"), new BigDecimal("15.5"),
                "https://www.youtube.com/watch?v=FqnKB22pOC0", "https://us.battle.net/forums/static/images/social-thumbs/overwatch.png",
                "Overwatch is a team-based multiplayer online first-person shooter video game.", LocalDate.of(2016, 5, 24));
    }

    @BeforeEach
    void adminLoggedIn() {
        userService.registerUser(new UserRegisterDto("admin@abv.bg", "Admin1", "Admin1", "Admin Adminov"));
        userService.loginUser(new UserLoginDto("admin@abv.bg", "Admin1"));
    }

    private long overwatchId() {
        return gameRepository.findByTitle("Overwatch").orElseThrow().getId();
    }

    @Test
    void firstUserIsAdminOthersAreNot() {
        userService.registerUser(new UserRegisterDto("user@abv.bg", "User12", "User12", "Normal User"));

        assertThat(userRepository.findAll()).extracting(u -> u.getEmail() + "=" + u.getAdmin())
                .containsExactlyInAnyOrder("admin@abv.bg=true", "user@abv.bg=false");
    }

    @Test
    void registerRequiresUpperLowerDigitAndDottedEmail(CapturedOutput output) {
        userService.registerUser(new UserRegisterDto("weak@abv.bg", "alllower1", "alllower1", "Weak Pass"));
        userService.registerUser(new UserRegisterDto("nodot@abv", "Strong1", "Strong1", "No Dot"));

        assertThat(userRepository.count()).isOne();
        assertThat(output).contains("Enter valid password", "Enter valid email");
    }

    @Test
    void addGameStoresTrailerIdAndRejectsInvalidData(CapturedOutput output) {
        gameService.addGame(overwatch());
        gameService.addGame(new GameAddDto("lowercase", new BigDecimal("-1"), new BigDecimal("1.25"), "short",
                "ftp://x", "too short", LocalDate.now()));

        assertThat(output).contains("Added Overwatch", "Title must start with an uppercase letter", "Price must be positive",
                "Size must have up to 1 digit", "Trailer must be a YouTube video id", "Thumbnail URL must start with",
                "Description must be at least 20 symbols");
        assertThat(gameRepository.findAll()).singleElement()
                .satisfies(game -> assertThat(game.getTrailer()).isEqualTo("FqnKB22pOC0"));
    }

    @Test
    void onlyAdminCanManageGames(CapturedOutput output) {
        userService.logout();
        userService.registerUser(new UserRegisterDto("user@abv.bg", "User12", "User12", "Normal User"));
        userService.loginUser(new UserLoginDto("user@abv.bg", "User12"));

        gameService.addGame(overwatch());

        assertThat(gameRepository.count()).isZero();
        assertThat(output).contains("Only a logged in administrator can manage games");
    }

    @Test
    void editDetailAllAndDelete(CapturedOutput output) {
        gameService.addGame(overwatch());

        gameService.editGame(overwatchId(), Map.of("price", "80.00", "size", "12.0"));
        gameService.editGame(overwatchId(), Map.of("price", "-5"));
        gameService.printAllGames();
        gameService.printGameDetails("Overwatch");
        gameService.deleteGame(overwatchId());

        assertThat(output).contains("Edited Overwatch", "Price must be positive",
                String.format("Overwatch %.2f", 80.0), "Title: Overwatch", "Release date: 24-05-2016", "Deleted Overwatch");
        assertThat(gameRepository.count()).isZero();
    }

    @Test
    void cartBuyOwnedGamesAndDeleteOwnedGame(CapturedOutput output) {
        gameService.addGame(overwatch());

        userService.addItem("Overwatch");
        userService.addItem("Overwatch");
        userService.addItem("Missing");
        userService.buyItems();
        userService.addItem("Overwatch");
        userService.printOwnedGames();

        assertThat(output).contains("Overwatch added to cart.", "Overwatch is already in the cart", "No such game",
                "Successfully bought games:", " -Overwatch", "You already own Overwatch");
        assertThat(orderRepository.count()).isOne();

        gameService.deleteGame(overwatchId());
        assertThat(gameRepository.count()).isZero();
        assertThat(userRepository.findByEmailAndPassword("admin@abv.bg", "Admin1").orElseThrow().getGames()).isEmpty();
    }

    @Test
    void removeItemFromCart(CapturedOutput output) {
        gameService.addGame(overwatch());
        userService.addItem("Overwatch");
        userService.removeItem("Overwatch");
        userService.buyItems();

        assertThat(output).contains("Overwatch removed from cart.", "The cart is empty");
    }
}
