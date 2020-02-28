package org.github.chibyhq.msb.serial;

public enum ParamEnum {
    BAUD_RATE("baud");
    
    String label;
    
    ParamEnum(String label) {
        this.label = label;
    }
}

