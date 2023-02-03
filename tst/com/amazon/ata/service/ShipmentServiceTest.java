package com.amazon.ata.service;

import com.amazon.ata.cost.MonetaryCostStrategy;
import com.amazon.ata.dao.PackagingDAO;
import com.amazon.ata.datastore.PackagingDatastore;
import com.amazon.ata.exceptions.NoPackagingFitsItemException;
import com.amazon.ata.exceptions.UnknownFulfillmentCenterException;
import com.amazon.ata.types.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ShipmentServiceTest {

    private Item smallItem = Item.builder()
            .withHeight(BigDecimal.valueOf(1))
            .withWidth(BigDecimal.valueOf(1))
            .withLength(BigDecimal.valueOf(1))
            .withAsin("abcde")
            .build();

    private Item largeItem = Item.builder()
            .withHeight(BigDecimal.valueOf(1000))
            .withWidth(BigDecimal.valueOf(1000))
            .withLength(BigDecimal.valueOf(1000))
            .withAsin("12345")
            .build();

    private Packaging packaging = new PolyBag(BigDecimal.valueOf(2000));
    private FulfillmentCenter existentFC = new FulfillmentCenter("ABE2");
    private FulfillmentCenter nonExistentFC = new FulfillmentCenter("NonExistentFC");


    private ShipmentOption shipmentOption = ShipmentOption.builder()
            .withItem(smallItem)
            .withPackaging(packaging)
            .withFulfillmentCenter(existentFC)
            .build();

    private ShipmentCost shipmentCost = new ShipmentCost(shipmentOption, BigDecimal.ONE);

    private List<ShipmentOption> shipmentOptions = Arrays.asList(shipmentOption);

    @InjectMocks
    ShipmentService shipmentService;

    @Mock
    PackagingDAO packagingDAO;

    @Mock
    MonetaryCostStrategy monetaryCostStrategy;

    @BeforeEach
    void setUp() { initMocks(this); }


    @Test
    void findBestShipmentOption_existentFCAndItemCanFit_returnsShipmentOption() throws Packaging.UnsupportedOperationsException, UnknownFulfillmentCenterException, NoPackagingFitsItemException {
        // GIVEN & WHEN
        when(packagingDAO.findShipmentOptions(smallItem,existentFC)).thenReturn(shipmentOptions);
        when(monetaryCostStrategy.getCost(shipmentOption)).thenReturn(shipmentCost);
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(smallItem, existentFC);

        // THEN
        assertNotNull(shipmentOption);
    }

    @Test
    void findBestShipmentOption_existentFCAndItemCannotFit_returnsShipmentOption() throws Packaging.UnsupportedOperationsException, UnknownFulfillmentCenterException, NoPackagingFitsItemException {
        // GIVEN
        when(packagingDAO.findShipmentOptions(largeItem,existentFC)).thenThrow(NoPackagingFitsItemException.class);

        // WHEN
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(largeItem, existentFC);

        // THEN
        assertNull(shipmentOption);
    }

    @Test
    void findBestShipmentOption_nonExistentFCAndItemCanFit_returnsShipmentOption() throws Packaging.UnsupportedOperationsException, UnknownFulfillmentCenterException, NoPackagingFitsItemException {
        // GIVEN
        when(packagingDAO.findShipmentOptions(smallItem, nonExistentFC)).thenThrow(UnknownFulfillmentCenterException.class);

        // WHEN
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(smallItem, nonExistentFC);

        // THEN
        assertNull(shipmentOption);
    }

    @Test
    void findBestShipmentOption_nonExistentFCAndItemCannotFit_returnsShipmentOption() throws Packaging.UnsupportedOperationsException, UnknownFulfillmentCenterException, NoPackagingFitsItemException {
        // GIVEN
        when(packagingDAO.findShipmentOptions(largeItem, nonExistentFC)).thenThrow(UnknownFulfillmentCenterException.class);


        // WHEN
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(largeItem, nonExistentFC);

        // THEN
        assertNull(shipmentOption);
    }
}