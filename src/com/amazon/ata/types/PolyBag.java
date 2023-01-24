package com.amazon.ata.types;

import java.math.BigDecimal;
import java.util.Objects;

public class PolyBag extends Packaging {

    private BigDecimal volume;


    /**
     * Instantiates a new PolyBag object.
     */
    public PolyBag(BigDecimal volume) {
        super(Material.LAMINATED_PLASTIC);
        this.volume = volume;

    }
    public BigDecimal getVolume() {
        return volume;
    }

    public boolean canFitItem(Item item) {
        return this.getVolume().compareTo(item.getLength()
                .multiply(item.getWidth()).multiply(item.getHeight())) > 0;
    }

    /**
     * Returns the mass of the packaging in grams. The packaging weighs 1 gram per square centimeter.
     * @return the mass of the packaging
     */
    public BigDecimal getMass() {
        // Math.sqrt() doesn't support accepting a BigDecimal
        // make an approximation using double values and covert back to BigDecimal
        double doubleVolume = getVolume().doubleValue();
        double doubleMass = Math.ceil(Math.sqrt(doubleVolume) * 0.6);
        return BigDecimal.valueOf(doubleMass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PolyBag)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PolyBag polyBag = (PolyBag) o;
        return getVolume().equals(polyBag.getVolume());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getVolume());
    }
}
