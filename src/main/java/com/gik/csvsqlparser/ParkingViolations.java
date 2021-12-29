package com.gik.csvsqlparser;

import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ParkingViolations {

    private String summonsNumber;

    private String plateId;

    private String registrationState;

}
