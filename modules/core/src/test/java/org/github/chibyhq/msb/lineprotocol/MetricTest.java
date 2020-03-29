package org.github.chibyhq.msb.lineprotocol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MetricTest {

    @Test
    public void testToString() {
        Metric m = Metric.builder().name("myMetric")
                .tag("location", "paris")
                .value("temp", "27")
                .value("light", "20000")
                .timestamp(124200700l)
                .build();
        
        assertEquals("myMetric,location=paris temp=27,light=20000 124200700", m.toString());
    }

}
