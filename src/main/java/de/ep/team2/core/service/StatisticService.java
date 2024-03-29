package de.ep.team2.core.service;

import de.ep.team2.core.entities.UserStats;

import java.util.Map;

public class StatisticService {

    /**
     * Counts the number of users using the same focus, groups them then returns a map with the name of the focus as String and the counted number as Integer.
     *
     * @return Map with each focus, which has users, and the number of the users using it.
     */
    public Map<String,Integer> getUserFocusStats() {
        return DataBaseService.getInstance().getUserFocusStats();
    }

    /**
     * Counts the number of users with the same experience level, groups them then returns a map with the experience level as String and the counted number as Integer.
     *
     * @return Map with each experience level, which has users, and the number of the users using it.
     */
    public Map<String,Integer> getUserExperienceStats() {
        return DataBaseService.getInstance().getUserExperienceStats();
    }

    /**
     * Counts the number of users having the same trainings frequency, groups them then returns a map with the frequency as String and the counted number as Integer.
     *
     * @return Map with each frequency, which has users, and the number of the users using it.
     */
    public Map<String,Integer> getUserFrequencyStats() {
        return DataBaseService.getInstance().getUserFrequencyStats();
    }

    /**
     * Counts the number of users with the same Role, groups them then returns a map with the Role as String and the counted number as Integer.
     *
     * @return Map with each Role and the number of the users having it.
     */
    public Map<String,Integer> getUserNumberStats() {
        return DataBaseService.getInstance().getUserNumberStats();
    }

    /**
     * Counts the number of users having the same gender, groups them then returns a map with the gender as String and the counted number as Integer.
     *
     * @return Map with each gender and unknown, which users have, and the number of the users having it.
     */
    public Map<String,Integer> getUserGenderStats() {
        return DataBaseService.getInstance().getUserGenderStats();
    }

    /**
     * returns the number of all exercises.
     *
     * @return the number of all exercises.
     */
    public Integer getNumberExercises() {
        return DataBaseService.getInstance().getNumberExercises();
    }

    /**
     * returns the number of all plans.
     *
     * @return the number of all plans.
     */
    public Integer getNumberPlans() {
        return DataBaseService.getInstance().getNumberPlans();
    }

    /**
     * Gets a User Stats object with filled data for the user with the given email.
     *
     * @param userMail mail to specify the user.
     * @return stats object filled with data.
     */
    public UserStats getUserStats(String userMail) {
        return DataBaseService.getInstance().getUserStats(userMail);
    }

    /**
     * adds the value to the total weight stat of the user specified by user Mail.
     *
     * @param userMail Mail to identify user.
     * @param value value to add to the total weight.
     */
    public void increaseWeightDone(String userMail, int value) {
        DataBaseService.getInstance().increaseWeightDone(userMail, value);
    }

    /**
     * Increases the days done stat of the user with email userMail by 1.
     *
     * @param userMail email to specify user.
     */
    public void increaseDaysDone(String userMail) {
        DataBaseService.getInstance().increaseDaysDone(userMail);
    }
}
