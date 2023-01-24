package com.amazon.ata.cost;

import com.amazon.ata.types.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * A strategy to calculate the cost of a ShipmentOption based on its dollar cost.
 */
public class MonetaryCostStrategy implements CostStrategy {

    private static final BigDecimal LABOR_COST = BigDecimal.valueOf(0.43);
    private final Map<Material, BigDecimal> materialCostPerGram;

    /**
     * Initializes a MonetaryCostStrategy.
     */
    public MonetaryCostStrategy() {
        materialCostPerGram = new HashMap<>();
        materialCostPerGram.put(Material.CORRUGATE, BigDecimal.valueOf(.005));
        materialCostPerGram.put(Material.LAMINATED_PLASTIC, BigDecimal.valueOf(.25));
    }

    @Override
    public ShipmentCost getCost(ShipmentOption shipmentOption) {
        Packaging packaging = shipmentOption.getPackaging();
        BigDecimal materialCost = this.materialCostPerGram.get(packaging.getMaterial());
        BigDecimal cost = null;
        if (packaging instanceof Box) {
            Box box = (Box) packaging;
            cost = box.getMass().multiply(materialCost)
                    .add(LABOR_COST);
        } else {
            PolyBag polyBag = (PolyBag) packaging;
            cost = polyBag.getMass().multiply(materialCost)
                    .add(LABOR_COST);
        }
        return new ShipmentCost(shipmentOption, cost);
    }
}
