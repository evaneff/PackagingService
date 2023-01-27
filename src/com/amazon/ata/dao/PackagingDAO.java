package com.amazon.ata.dao;

import com.amazon.ata.datastore.PackagingDatastore;
import com.amazon.ata.exceptions.NoPackagingFitsItemException;
import com.amazon.ata.exceptions.UnknownFulfillmentCenterException;
import com.amazon.ata.types.FcPackagingOption;
import com.amazon.ata.types.FulfillmentCenter;
import com.amazon.ata.types.Item;
import com.amazon.ata.types.Packaging;
import com.amazon.ata.types.ShipmentOption;

import java.util.*;

/**
 * Access data for which packaging is available at which fulfillment center.
 */
public class PackagingDAO {
    /**
     * A list of fulfillment centers with the packaging options they provide.
     */
    private Map<FulfillmentCenter, Set<Packaging>> fcPackagingOptions = new HashMap<>();

    /**
     * Instantiates a PackagingDAO object.
     * @param datastore Where to pull the data from for fulfillment center/packaging available mappings.
     */
    public PackagingDAO(PackagingDatastore datastore) {
        List<FcPackagingOption> packagingOptions = new ArrayList<>(datastore.getFcPackagingOptions());

        for (FcPackagingOption option : packagingOptions) {
            if (fcPackagingOptions.containsKey(option.getFulfillmentCenter())) {
                Set<Packaging> existingOptionSet = fcPackagingOptions.get(option.getFulfillmentCenter());
                existingOptionSet.add(option.getPackaging());
            } else {
                Set<Packaging> newOptionSet = new HashSet<>();
                newOptionSet.add(option.getPackaging());
                fcPackagingOptions.put(option.getFulfillmentCenter(), newOptionSet);
            }
        }

    }

    /**
     * Returns the packaging options available for a given item at the specified fulfillment center. The API
     * used to call this method handles null inputs, so we don't have to.
     *
     * @param item the item to pack
     * @param fulfillmentCenter fulfillment center to fulfill the order from
     * @return the shipping options available for that item; this can never be empty, because if there is no
     * acceptable option an exception will be thrown
     * @throws UnknownFulfillmentCenterException if the fulfillmentCenter is not in the fcPackagingOptions list
     * @throws NoPackagingFitsItemException if the item doesn't fit in any packaging at the FC
     */
    public List<ShipmentOption> findShipmentOptions(Item item, FulfillmentCenter fulfillmentCenter)
            throws UnknownFulfillmentCenterException, NoPackagingFitsItemException, Packaging.UnsupportedOperationsException {

        // Check all FcPackagingOptions for a suitable Packaging in the given FulfillmentCenter
        List<ShipmentOption> result = new ArrayList<>();
        boolean fcFound = false;
        String fcCode = fulfillmentCenter.getFcCode();
        Set<Packaging> packagingOptions = fcPackagingOptions.get(fulfillmentCenter);
            if (packagingOptions != null) {
                fcFound = true;
                for (Packaging packaging : packagingOptions) {
                    if (packaging.canFitItem(item)) {
                        System.out.println("Packaging found");
                        result.add(ShipmentOption.builder()
                                .withItem(item)
                                .withPackaging(packaging)
                                .withFulfillmentCenter(fulfillmentCenter)
                                .build());
                    }
                }
            }

        // Notify caller about unexpected results
        if (!fcFound) {
            throw new UnknownFulfillmentCenterException(
                    String.format("Unknown FC: %s!", fulfillmentCenter.getFcCode()));
        }

        if (result.isEmpty()) {
            throw new NoPackagingFitsItemException(
                    String.format("No packaging at %s fits %s!", fulfillmentCenter.getFcCode(), item));
        }

        return result;
    }
}
