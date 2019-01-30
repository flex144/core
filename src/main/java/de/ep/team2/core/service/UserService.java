package de.ep.team2.core.service;

import de.ep.team2.core.dtos.RegistrationDto;
import de.ep.team2.core.entities.ConfirmationToken;
import de.ep.team2.core.entities.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Forwards User related requests to the Data Access Object.
 */
@Component
public class UserService {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User getUserByID(Integer id) {
        return DataBaseService.getInstance().getUserById(id);
    }

    public User getUserByEmail(String email) {
        return DataBaseService.getInstance().getUserByEmail(email);
    }

    public void deleteUserByID(Integer id) {
        DataBaseService.getInstance().deleteUserById(id);
    }

    public void createUser(String email, String firstName, String lastName, String password) {
        DataBaseService.getInstance().insertUser(email, firstName, lastName, password);
    }

    public void changeToMod(int id) {
        DataBaseService.getInstance().changeToMod(id);
    }

    /**
     * This method is used for registration. It checks whether an email is valid, if the given passwords match
     * and if the email isn't already registered. When there is no error, it creates a new User in the Database.
     *
     * @param userDto A user Data Transfer Object is used to provide the method with email, password, and
     *                a second password (needs to be equal to first password).
     * @return It returns an error message as String if the given values can't meet the requirements of the method.
     * Otherwise the returned String is empty.
     */
    public String checkToCreateUser(RegistrationDto userDto) {
        String errorMessage = "";
        String email = userDto.getEmail();
        String password = userDto.getPassword();
        String confirmPassword = userDto.getConfirmPassword();

        if (!checkEmailPattern(email)) {                                //check if a valid email
            errorMessage = "Das ist keine E-Mail";
        } else if (!password.equals(confirmPassword)) {                 //check if both passwords are equal
            errorMessage = "Passwörter müssen übereinstimmen!";
        } else if (getUserByEmail(email) != null) {                     //check if email isn't already registered
            errorMessage = "E-Mail: '" + email + "' existiert bereits!";
        } else {
            createUser(email, null, null, encode(password));
        }

        return errorMessage;
    }

    public List<User> getAllUsers() {
        return DataBaseService.getInstance().getAllUsers();
    }

    public User changeUserDetails (User user) {
        return DataBaseService.getInstance().changeUserDetails(user);
    }

    public void changeAdvancedUserDetails(User user) {
        DataBaseService.getInstance().setAdvancedUserData(user.getWeightInKg(), user.getHeightInCm(),
                user.getTrainingsFocus(), user.getTrainingsFrequency(), user.getGender(), user.getExperience(),
                user.getBirthDate(), user.getEmail());
    }

    /**
     * This method creates a secure password hash, using the BCryptPasswordEncoder.
     * It is used to ensure a password isn't saved as plaintext in the database.
     *
     * @param pw Password in plaintext.
     * @return Password as a hash value.
     */
    public String encode(String pw) {
        String hashedPw = passwordEncoder.encode(pw);
        return hashedPw;
    }

    /**
     * Function to see, if a password in plain text matches the hash of the encrypted version of the password.
     * It calls the matches function of the BCrypt PasswordEncoder.
     *
     * @param pw          Password in plaintext
     * @param encryptedPw Password as hash
     * @return True, if passwords match. False, if not.
     */
    public boolean pwMatches(String pw, String encryptedPw) {
        return passwordEncoder.matches(pw, encryptedPw);
    }

    /**
     * This method checks if the given String is a valid E-Mail.
     *
     * @param email E-Mail that needs to be checked.
     * @return "true", if E-Mail is valid, "false", if not.
     */
    public boolean checkEmailPattern(String email) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        return (m.matches() || email.equals(""));
    }

    /**
     * This method searches for a user in the database and sets him to enabled.
     *
     * @param email Email of the user to confirm
     */
    public void confirmUser(String email) {
        DataBaseService.getInstance().confirmUser(email);
    }

    public void createConfirmationToken(ConfirmationToken confirmationToken) {
        DataBaseService.getInstance().createConfirmationToken(confirmationToken);
    }

    public ConfirmationToken findConfirmationToken(String token) {
        return DataBaseService.getInstance().findConfirmationToken(token);
    }

    /**
     * Determines the reason why a given email is flawed and returns it,
     * or the String 'valid' if there is nothing wrong.
     *
     * @param email email to get the error.
     * @return error message.
     */
    public String wrongMailReason(String email) {
        String errorMessage = "valid";
        if (email == null || email.isEmpty()) {
            errorMessage = "E-Mail Feld muss ausgefüllt werden!";
        } else if (!checkEmailPattern(email)) {
            errorMessage = "Das ist keine E-Mail!";
        } else if (getUserByEmail(email) == null) {
            errorMessage = "E-Mail existiert nicht";
        }
        return errorMessage;
    }

    public void deleteTokenById(int tokenId) {
        DataBaseService.getInstance().deleteTokenById(tokenId);
    }

    /**
     * Searches for Users whos email, first or last name contains the String searched for.
     * If the String is left null or empty, returns all User.
     * Removes the Password from the User objects before returning it.
     *
     * @param name String to search for.
     * @return List of User with matching name, or all user if name left blank.
     */
    public List<User> getUserListByName(String name) {
        List<User> toReturn;
        if (name == null || name.equals("")) {
            toReturn = getAllUsers();
        } else {
            toReturn = DataBaseService.getInstance().getUserListByName(name);
        }
        if (toReturn != null && !toReturn.isEmpty()) {
            for (User user : toReturn) {
                user.setPassword(null);
            }
        }
        return toReturn;
    }

    /**
     * Returns a list of all mods in the system and removes the password of the object.
     *
     * @return a list of all mods.
     */
    public List<User> getAllMods() {
        List<User> toReturn = DataBaseService.getInstance().getAllMods();
        if (toReturn != null && !toReturn.isEmpty()) {
            for (User user : toReturn) {
                user.setPassword(null);
            }
        }
        return toReturn;
    }

    /**
     * Sets a random password for a user. It's used when a user forgot his password.
     * @param email Email of user.
     * @return The new random Password as String.
     */
    public String setRandomUserPassword(String email) {
        String newPassword = "";
        DataBaseService db = DataBaseService.getInstance();
        User toChange = getUserByEmail(email);
        if (toChange != null) {
            newPassword = UUID.randomUUID().toString().substring(1,12);
            toChange.setPassword(encode(newPassword));
            db.changeUserDetails(toChange);
        }
        return newPassword;
    }
}
