package de.ep.team2.core.entities;

import de.ep.team2.core.enums.ExperienceLevel;
import de.ep.team2.core.enums.Gender;
import de.ep.team2.core.enums.TrainingsFocus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Class which represents a User of the Database table 'users'.
 */
public class User implements UserDetails {

    private int id;
    private String email, firstName, lastName, password;
    private String role;
    private boolean enabled;
    private Integer heightInCm;
    private Integer weightInKg;
    private Gender gender;
    private TrainingsFocus trainingsFocus;
    private ExperienceLevel experience;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
    private Integer trainingsFrequency;

    public User(int id, String email, String firstName, String lastName, String password,
                String role, boolean enabled, Integer heightInCm, Integer weightInKg,
                Gender gender, TrainingsFocus trainingsFocus, ExperienceLevel experience,
                Date birthDate, Integer trainingsFrequency) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.heightInCm = heightInCm;
        this.weightInKg = weightInKg;
        this.gender = gender;
        this.trainingsFocus = trainingsFocus;
        this.experience = experience;
        this.birthDate = birthDate;
        this.trainingsFrequency = trainingsFrequency;
    }

    public User() {
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, email='%s', firstName='%s', lastName='%s']",
                id, email, firstName, lastName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getHeightInCm() {
        return heightInCm;
    }

    public void setHeightInCm(Integer heightInCm) {
        this.heightInCm = heightInCm;
    }

    public Integer getWeightInKg() {
        return weightInKg;
    }

    public void setWeightInKg(Integer weightInKg) {
        this.weightInKg = weightInKg;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public TrainingsFocus getTrainingsFocus() {
        return trainingsFocus;
    }

    public void setTrainingsFocus(TrainingsFocus trainingsFocus) {
        this.trainingsFocus = trainingsFocus;
    }

    public ExperienceLevel getExperience() {
        return experience;
    }

    public void setExperience(ExperienceLevel experience) {
        this.experience = experience;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * brings the birth date into the format dd.mm.yyyy and returns it as string.
     *
     * @return String of the birth date.
     */
    public String getBirthDateString() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        if (birthDate != null) {
            return format.format(this.birthDate);
        } else {
            return "";
        }
    }

    public Integer getTrainingsFrequency() {
        return trainingsFrequency;
    }

    public void setTrainingsFrequency(Integer trainingsFrequency) {
        this.trainingsFrequency = trainingsFrequency;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final Set<GrantedAuthority> grntAuth = new HashSet<GrantedAuthority>();
        grntAuth.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return getRole();
            }
        });

        return grntAuth;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
