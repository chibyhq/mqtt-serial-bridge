package org.github.chibyhq.msb.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceOutput {
    String port;
    long timestamp;
    String line;
}
