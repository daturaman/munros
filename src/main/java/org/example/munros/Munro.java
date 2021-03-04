package org.example.munros;

import java.util.Objects;

/**
 * POJO representing a single munro entry.
 */
public class Munro {

    private String name;
    private float height;
    private String gridReference;
    private Category category;

    /**
     * Create a munro entry with the provided values.
     *
     * @param name the name of the munro.
     * @param height the height of the munro in metres.
     * @param gridReference the grid reference.
     * @param category whether this is a munro or munro top.
     */
    public Munro(String name, float height, String gridReference, Category category) {
        this.name = name;
        this.height = height;
        this.gridReference = gridReference;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public float getHeight() {
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
        MUN, TOP;
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
}
