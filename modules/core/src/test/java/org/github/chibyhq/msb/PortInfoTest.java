package org.github.chibyhq.msb;


import static org.junit.jupiter.api.Assertions.assertTrue;

import org.github.chibyhq.msb.dto.PortInfo;
import org.junit.jupiter.api.Test;

public class PortInfoTest {

    private static final String TTY_ACM0 = "ttyACM0";

    @Test
    public void testEquals() {
        assertTrue(PortInfo.builder().systemPortName(TTY_ACM0).build().equals(PortInfo.builder().systemPortName(TTY_ACM0).baudRate(115200).dsr(true).build()));
    }

}
