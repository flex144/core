package de.ep.team2.core.entities;

import de.ep.team2.core.enums.WeightType;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Entry Exercise in the Database
 */
public class Exercise {
    private int id;
    private String name, description;
    private WeightType weightType;
    private String videoLink;
    private List<String> muscleImgPaths = new LinkedList<>();
    private List<String> otherImgPaths = new LinkedList<>();

    public Exercise(int id, String name, String description,
                    WeightType weightType, String videoLink,
                    List<String> muscleImgPaths, List<String> otherImgPaths) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.weightType = weightType;
        this.videoLink = videoLink;
        this.muscleImgPaths = muscleImgPaths;
        this.otherImgPaths = otherImgPaths;
    }

    public Exercise() {}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format(
                "Exercise[id=%d, name='%s', description='%s'",
                id, name, description));
        if (otherImgPaths != null) {
            for (String s : otherImgPaths) {
                builder.append(String.format(", imgPath='%s'", s));
            }
        }
        builder.append(']');
        return builder.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WeightType getWeightType() {
        return weightType;
    }

    public void setWeightType(WeightType weightType) {
        this.weightType = weightType;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public List<String> getMuscleImgPaths() {
        return muscleImgPaths;
    }

    public void setMuscleImgPaths(List<String> muscleImgPaths) {
        this.muscleImgPaths = muscleImgPaths;
    }

    public List<String> getOtherImgPaths() {
        return otherImgPaths;
    }

    public void setOtherImgPaths(List<String> otherImgPaths) {
        this.otherImgPaths = otherImgPaths;
    }

    public void addMuscleImgPath(String path) {
        muscleImgPaths.add(path);
    }

    public void addOtherImgPath(String path) {
        otherImgPaths.add(path);
    }
}
