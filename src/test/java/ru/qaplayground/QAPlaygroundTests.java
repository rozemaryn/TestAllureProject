package ru.qaplayground;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.ResponseLoggingFilter;
import net.datafaker.Faker;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.qaplayground.dto.UserCreatedOrGetResponse;
import ru.qaplayground.dto.UserCreatedRequest;
import ru.qaplayground.dto.UserUpdateRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;

public class QAPlaygroundTests extends BaseTest {

    private final SoftAssertions softly = new SoftAssertions();
    private final Faker faker = new Faker();

    @Test
    public void createUserSuccessfully() {
        UserCreatedRequest newUser = new UserCreatedRequest(faker.internet().emailAddress(), faker.internet().password(),
                faker.name().fullName(), faker.name().firstName());

        UserCreatedOrGetResponse response = step("Создаём нового пользователя", () -> createUser(newUser));

        step("Проверяем, что UUID юзера !=null", () -> assertNotNull(response.getUuid()));
        step("Проверяем параметры юзера", () -> {
            assertSoftly(softly -> {
                softly.assertThat(newUser.getEmail()).isEqualTo(response.getEmail());
                softly.assertThat(newUser.getName()).isEqualTo(response.getName());
                softly.assertThat(newUser.getNickname()).isEqualTo(response.getNickname());
                softly.assertThat(response.getNickname()).isNotEmpty();
            });
        });

        String listOfAllUsers = step("Получаем список всех юзеров", this::getAllUsers);
        step("Проверяем, что созданный юзер есть в списке", () -> {
            boolean expectedUUID = listOfAllUsers.contains(response.getUuid());
            assertTrue(expectedUUID, "UUID созданного юзера не найден в списке");
        });

        UserCreatedOrGetResponse getUserByID = step("Получаем юзера по UUID", () -> getUserByUUID(response.getUuid()));
        step("Проверяем, что UUID созданного юзера совпадает с тем, что в ответе", () ->
                assertEquals(response.getUuid(), getUserByID.getUuid(), "UUID не совпадают")
        );
    }

    @Test
    public void deleteUserSuccessfully() {
        step("Проверяем, что в системе есть пользователи", () -> {
            String listOfAllUsers = checkIfUsersArePresent();
            ReadContext ctx = JsonPath.parse(listOfAllUsers);
            List<String> uuids = ctx.read("$.users[*].uuid");

            step("Удаляем первого пользователя в списке", () -> {
                RestAssured
                        .given()
                        .headers(headers)
                        .filter(new AllureRestAssured())
                        .when()
                        .delete(baseURL + "users/" + uuids.get(0))
                        .then().log().all()
                        .statusCode(204);
            });

            step("Проверяем, что пользователь был удален", () -> {
                String listOfAllUsersAfterDeletion = getAllUsers();
                assertFalse(listOfAllUsersAfterDeletion.contains(uuids.get(0)),
                        "UUID не должен присутствовать в ответе");
            });

            step("Проверяем, что пользователь не найден по UUID после удаления", () -> {
                checkUserNotFoundByUUID(uuids.get(0));
            });
        });
    }

    @Test
    public void searchByKeyword() {
        step("Получаем ключевое слово из списка игр", () -> {
            String keyword = getKeywordFromListOfGames();

            step("Ищем игры по ключевому слову: " + keyword, () -> {
                String searchResults = given()
                        .headers(headers)
                        .filter(new AllureRestAssured())
                        .when()
                        .get(baseURL + "games/search?query=" + keyword)
                        .then().log().all()
                        .statusCode(200)
                        .extract().asString();

                step("Проверяем, что поля ответа сооветствуют ТЗ, а названия содержат ключевое слово",
                        () -> {
                    checkingJsonResponseFromSearch(searchResults, keyword);
                });
            });
        });
    }

    @Test
    public void testCannotUpdateUserWithDuplicateData() {
        step("Проверяем, что в системе есть пользователи", () -> {
            String listOfAllUsers = checkIfUsersArePresent();

            DocumentContext jsonContext = JsonPath.parse(listOfAllUsers);

            step("Извлекаем email и nickname всех пользователей", () -> {
                List<String> emails = jsonContext.read("$.users[*].email");
                List<String> nicknames = jsonContext.read("$.users[*].nickname");
                List<String> uuids = jsonContext.read("$.users[*].uuid");

                String emailOfFirstUser = emails.get(0);
                String nicknameOfFirstUser = nicknames.get(0);
                String uuidOfSecondUser = uuids.get(1);

                UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
                userUpdateRequest.setEmail(emailOfFirstUser);
                userUpdateRequest.setNickname(nicknameOfFirstUser);

                step("Обновляем пользователя с уже существующими данными", () -> {
                    String responseBody = given()
                            .headers(headers)
                            .filter(new AllureRestAssured())
                            .when()
                            .body(userUpdateRequest)
                            .patch(baseURL + "users/" + uuidOfSecondUser)
                            .then().log().all()
                            .statusCode(409)
                            .extract().response()
                            .getBody().asString();

                    step("Проверяем сообщение об ошибке", () -> {
                        String expectedMessage = "User with the following \"email\" already exists: " + emailOfFirstUser;
                        String actualMessage = JsonPath.read(responseBody, "$.message");
                        assertEquals(expectedMessage, actualMessage, "Сообщение об ошибке не соответствует ожидаемому");
                    });
                });
            });
        });
    }

    @Test
    public void addGameToTheWishlistSuccessfully() {
        step("Выбираем рандомного юзера", () -> {
            String listOfAllUsers = checkIfUsersArePresent();
            final ReadContext[] jsonContext = {JsonPath.parse(listOfAllUsers)};
            List<String> uuidsOfUsers = jsonContext[0].read("$.users[*].uuid");
            String uuidOfRandomUser = getRandomElement(uuidsOfUsers);

            step("Выбираем рандомную игру", () -> {
                String listOfAllGames = getAllGames();
                jsonContext[0] = JsonPath.parse(listOfAllGames);
                List<String> uuidsOfGames = jsonContext[0].read("$.games[*].uuid");
                String uuidOfRandomGame = getRandomElement(uuidsOfGames);

                step("Добавляем игру в вишлист", () -> {
                    String addGameToWishlistResponse = addGameToWishlist(uuidOfRandomGame, uuidOfRandomUser);

                    step("Проверяем, что UUID игры есть в ответе", () -> {
                        String actualUuid = JsonPath.read(addGameToWishlistResponse, "$.items[0].uuid");
                        assertEquals(uuidOfRandomGame, actualUuid, "UUID не соответствует ожидаемому значению");
                    });
                });
            });
        });
    }

    private static String addGameToWishlist(String uuidOfRandomGame, String uuidOfRandomUser) {
        return given()
                .headers(headers)
                .filter(new AllureRestAssured())
                .when()
                .body("{" + "\"item_uuid\": \"" + uuidOfRandomGame + "\"}")
                .post(baseURL + "users/" + uuidOfRandomUser + "/wishlist/add")
                .then().log().all()
                .statusCode(200)
                .extract().response()
                .getBody().asString();
    }


    private String checkIfUsersArePresent() {
        String listOfAllUsers = getAllUsers();

        if (listOfAllUsers.isEmpty()) {
            for (int i = 0; i < 2; i++) {
                UserCreatedRequest newUser = new UserCreatedRequest(faker.internet().emailAddress(), faker.internet().password(),
                        faker.name().fullName(), faker.name().firstName());

                createUser(newUser);
            }
        }
        return listOfAllUsers;
    }

    private UserCreatedOrGetResponse getUserByUUID(String UUID) {
        return given()
                .headers(headers)
                .filter(new AllureRestAssured())
                .when()
                .get(baseURL + "users/" + UUID)
                .then().log().all()
                .statusCode(200)
                .extract().as(UserCreatedOrGetResponse.class);
    }

    private static void checkUserNotFoundByUUID(String UUID) {
        given()
                .headers(headers)
                .filter(new AllureRestAssured())
                .filter(new ResponseLoggingFilter())
                .when()
                .get(baseURL + "users/" + UUID)
                .then().log().all()
                .statusCode(404);
    }

    private String getAllUsers() {
        return given()
                .headers(headers)
                .filter(new AllureRestAssured())
                .when()
                .get(baseURL + "users")
                .then().log().all()
                .statusCode(200)
                .extract().asString();
    }

    private static UserCreatedOrGetResponse createUser(UserCreatedRequest newUser) {
        return given()
                .headers(headers)
                .filter(new AllureRestAssured())
                .when()
                .body(newUser)
                .post(baseURL + "users")
                .then().log().all()
                .statusCode(200)
                .extract().as(UserCreatedOrGetResponse.class);
    }


    private String getAllGames() {
        return given()
                .headers(headers)
                .filter(new AllureRestAssured())
                .when()
                .get(baseURL + "games")
                .then().log().all()
                .statusCode(200)
                .extract().asString();
    }

    private String getKeywordFromListOfGames() {
        String listOfAllGames = getAllGames();
        ReadContext ctx = JsonPath.parse(listOfAllGames);
        List<String> titlesOfGames = ctx.read("$.games[*].title");

        List<String> articles = Arrays.asList("the", "a", "an", "of", "in", "on", "at", "by", "for", "with");

        List<String> processedTitles = titlesOfGames.stream()
                .flatMap(title -> Arrays.stream(title.split(" ")))
                .filter(word -> !articles.contains(word.toLowerCase()))
                .toList();

        return getRandomElement(processedTitles);
    }

    private void checkingJsonResponseFromSearch(String response, String keyword) {
        // Извлечение всех полей
        List<List<String>> categoryUuids = JsonPath.read(response, "$.games[*].category_uuids");
        List<Integer> prices = JsonPath.read(response, "$.games[*].price");
        List<String> titles = JsonPath.read(response, "$.games[*].title");
        List<String> uuids = JsonPath.read(response, "$.games[*].uuid");
        int total = JsonPath.read(response, "$.meta.total");

        // Проверка, что все поля присутствуют и не пустые
        Map<String, List<?>> fields = Map.of(
                "category_uuids", categoryUuids,
                "prices", prices,
                "titles", titles,
                "uuids", uuids
        );

        fields.forEach((name, list) -> Assertions.assertFalse(list.isEmpty(), name + " field is empty"));

        // Проверка, что каждый title содержит ключевое слово (без учета регистра)
        Assertions.assertTrue(titles.stream().allMatch(title ->
                        title.toLowerCase().contains(keyword.toLowerCase())),
                "Not all titles contain the keyword");
    }

    private static <T> T getRandomElement(List<T> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size() - 1));
    }
}
