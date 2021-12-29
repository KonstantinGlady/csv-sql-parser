package com.gik.csvsqlparser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ParkingItemProcessor implements ItemProcessor<ParkingViolations, ParkingViolations> {

    @Override
    public ParkingViolations process(ParkingViolations parkingViolations) {
        final String platedId = "****" + parkingViolations.getPlateId().substring(3);
        final String summonsNumber = "******";
        final String registrationState = parkingViolations.getRegistrationState();

        final ParkingViolations pv = new ParkingViolations(summonsNumber, platedId, registrationState);

        log.info("converting: " + parkingViolations + " into: " + pv);
        return pv;
    }
}
