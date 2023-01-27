package com.amazon.ata.cost;

import com.amazon.ata.types.ShipmentCost;
import com.amazon.ata.types.ShipmentOption;

import java.math.BigDecimal;

public class WeightedCostStrategy implements CostStrategy {

    MonetaryCostStrategy monetaryCostStrategy;
    CarbonCostStrategy carbonCostStrategy;

    public WeightedCostStrategy(MonetaryCostStrategy monetaryCostStrategy, CarbonCostStrategy carbonCostStrategy) {
        this.monetaryCostStrategy = monetaryCostStrategy;
        this.carbonCostStrategy = carbonCostStrategy;
    }

    @Override
    public ShipmentCost getCost(ShipmentOption shipmentOption) {
        BigDecimal carbonCost = carbonCostStrategy.getCost(shipmentOption).getCost().multiply(BigDecimal.valueOf(.2));
        BigDecimal monetaryCost = monetaryCostStrategy.getCost(shipmentOption).getCost().multiply(BigDecimal.valueOf(.8));
        BigDecimal blendedCost = carbonCost.add(monetaryCost);
        return new ShipmentCost(shipmentOption, blendedCost);
    }
}
