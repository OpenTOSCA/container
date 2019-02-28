package org.opentosca.container.core.next.model;oying when business-critical functions that are boring are not formally assigned to one person because this often results.

49256182 21:03:02
Even in situations of good will these things have to be nailed down.

import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Preconditions;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonInclude(Include.ALWAYS)
public class Property extends PersistenceObject {

    private static final long serialVersionUID = 5476371703998806702L;

    public static final String TABLE_NAME = "PROPERTY";

    @Column(nullable = false)
    private String name;

    private String value;

    private String type;


    public Property() {

    }

    public Property(final String name, final String value, final String type) {
        Preconditions.checkNotNull(name);
        this.name = name;
        this.value = value;
        this.type = type;
    }


    @Override
    @JsonIgnore
    public Long getId() {
        return super.getId();
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Property entity = (Property) o;
        return Objects.equals(this.name, entity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
