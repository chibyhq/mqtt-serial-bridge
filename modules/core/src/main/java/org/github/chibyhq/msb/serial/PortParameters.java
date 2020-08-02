package org.github.chibyhq.msb.serial;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PortParameters {
    @Builder.Default
    Integer baudRate = 115200;
    @Builder.Default
    Integer dataBits = 8;
    @Builder.Default
    Integer stopBits = 1;
    @Builder.Default
    Integer parity = 0;

    @Builder.Default
    Map<String, Object> extraParameters = new HashMap<>();
    
    @Builder.Default
    Boolean keepTryingUponFailure = true;
    
    @Builder.Default
    Long retryIntervalInMs = 3000l;
    
    @Builder.Default
    Integer readTimeout = 200;
    
    @Builder.Default
    Integer writeTimeout = 200;
    
    
}
