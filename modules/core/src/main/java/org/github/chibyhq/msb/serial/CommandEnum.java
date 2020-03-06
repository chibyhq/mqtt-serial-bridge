package org.github.chibyhq.msb.serial;

public enum CommandEnum {
   GET_PORTS("getPorts"),
   OPEN_PORT("openPort"),
   CLOSE_PORT("closePort");
   
   String commandText;
   
   CommandEnum(String commandText) {
       this.commandText = commandText;
   }
}
