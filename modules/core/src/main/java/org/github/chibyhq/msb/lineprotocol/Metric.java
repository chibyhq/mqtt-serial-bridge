package org.github.chibyhq.msb.lineprotocol;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 * Quick implementation of the influx line protocol output format.
 */
@Data
@Builder
public class Metric {

    String name;
    Long timestamp;
    @Singular private final Map<String, String> tags;
    @Singular private final Map<String, String> values;
    
    /**
     * Output a line protocol-compliant representation of the metric.
     * @return influx line protocol output.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if(tags.size()>0) sb.append(",");
        sb.append(tags.keySet().stream().map( key -> key+"="+tags.get(key)).collect(Collectors.joining(",")));
        sb.append(" ");
        sb.append(values.keySet().stream().map( key -> key+"="+values.get(key)).collect(Collectors.joining(",")));
        sb.append(" ");
        sb.append(timestamp);
        
        return sb.toString();
    }
}
