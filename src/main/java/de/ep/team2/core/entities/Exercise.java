package de.ep.team2.core.entities;

public class Exercise {
    private int id;
    private String name, description, imgPath;

    public Exercise(int id, String name, String description, String imgPath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imgPath = imgPath;
    }

    public Exercise() {}

    @Override
    public String toString() {
        return String.format(
                "Exercise[id=%d, name='%s', description='%s', imgPath='%s']",
                id, name, description, imgPath);
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

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
