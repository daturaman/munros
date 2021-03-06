package org.example.marilyn;

import static java.lang.Float.parseFloat;
import static org.example.marilyn.Munro.Category.valueOf;

import java.util.Objects;

/**
 * POJO representing a single munro entry.
 */
public class Munro {

    private static final int CATEGORY = 28;
    private static final int NAME = 6;
    private static final int HEIGHT_METRES = 10;
    private static final int GRID_REF = 14;
    private String name;
    private Float height;
    private String gridReference;
    private Category category;

    /**
     * The default constructor.
     */
    public Munro() {
        //Need this for Jackson
    }

    /**
     * Create a munro entry with the provided values.
     */
    public Munro(String[] entry) {
        this.name = entry[NAME];
        this.height = parseFloat(entry[HEIGHT_METRES]);
        this.gridReference = entry[GRID_REF];
        this.category = valueOf(entry[CATEGORY]);
    }

    public String getName() {
        return name;
    }

    public Float getHeight() {
        return height;
    }

    public String getGridReference() {
        return gridReference;
    }

    public Category getCategory() {
        return category;
    }

    /**
     * Describes a Munro's category.
     */
    public enum Category {
        MUN, TOP
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Munro munro = (Munro) o;
        return Float.compare(munro.height, height) == 0 && name.equals(munro.name) && gridReference
                .equals(munro.gridReference) && category == munro.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, height, gridReference, category);
    }

    @Override
    public String toString() {
        return "Munro{" +
                "name='" + name + '\'' +
                ", height=" + height +
                ", gridReference='" + gridReference + '\'' +
                ", category=" + category +
                '}';
    }
}
