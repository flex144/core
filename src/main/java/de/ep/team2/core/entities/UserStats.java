package de.ep.team2.core.entities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserStats {

    private int id;
    private String usermail;
    private Integer total_weight;
    private Integer plans_done;
    private Integer days_done;
    private Date registration_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsermail() {
        return usermail;
    }

    public void setUsermail(String usermail) {
        this.usermail = usermail;
    }

    public Integer getTotal_weight() {
        return total_weight;
    }

    public void setTotal_weight(Integer total_weight) {
        this.total_weight = total_weight;
    }

    public Integer getPlans_done() {
        return plans_done;
    }

    public void setPlans_done(Integer plans_done) {
        this.plans_done = plans_done;
    }

    public Integer getDays_done() {
        return days_done;
    }

    public void setDays_done(Integer days_done) {
        this.days_done = days_done;
    }

    public Date getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(Date registration_date) {
        this.registration_date = registration_date;
    }

    /**
     * brings the registration date into the format dd.mm.yyyy and returns it as string.
     *
     * @return String of the registration date.
     */
    public String getRegDateString() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        if (registration_date != null) {
            return format.format(this.registration_date);
        } else {
            return "";
        }
    }
}
