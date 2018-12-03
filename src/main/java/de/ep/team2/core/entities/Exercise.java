package de.ep.team2.core.entities;

import java.util.LinkedList;
import java.util.List;

// TODO: 03.12.18 add gewichtstyp und link. 

/**
 * Represents a Entry Exercise in the Database
 */
public class Exercise {
    private int id;
    private String name, description;
    private List<String> muscleImgPaths;
    private List<String> otherImgPaths;

    public Exercise(int id, String name, String description,
                    List<String> muscleImgPaths, List<String> otherImgPaths) {
        this.id = id;
        this.name = name;
        this.description = description;
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
        if (muscleImgPaths == null) {
            muscleImgPaths = new LinkedList<>();
        }
        muscleImgPaths.add(path);
    }

    public void addOtherImgPath(String path) {
        if (otherImgPaths == null) {
            otherImgPaths = new LinkedList<>();
        }
        otherImgPaths.add(path);
    }
}
