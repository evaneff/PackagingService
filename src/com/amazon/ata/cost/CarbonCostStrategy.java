package com.amazon.ata.cost;

import com.amazon.ata.types.*;

import java.math.BigDecimal;

public class CarbonCostStrategy implements CostStrategy {

    //sustainability index = Corrugate 0.017 per gram of mass
    //                     = Laminated plastic 0.012 per gram of mass


    public CarbonCostStrategy() {
    }

    @Override
    public ShipmentCost getCost(ShipmentOption shipmentOption) {
        Packaging packaging = shipmentOption.getPackaging();
        BigDecimal carbonCost = null;
            if (packaging instanceof Box) {
                Box box = (Box) packaging;
                carbonCost = box.getMass().multiply(BigDecimal.valueOf(0.017));
            } else {
                PolyBag polyBag = (PolyBag) packaging;
                carbonCost = polyBag.getMass().multiply(BigDecimal.valueOf(0.012));
            }
        return new ShipmentCost(shipmentOption, carbonCost);
    }
}
