package org.github.chibyhq.msb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude={"baudRate","open", "descriptor","descriptivePortName","parity", "dsr"})
public class PortInfo {
   boolean open;
   int baudRate;
   String descriptor;
   String descriptivePortName;
   int parity;
   String systemPortName;
   boolean dsr;
}
