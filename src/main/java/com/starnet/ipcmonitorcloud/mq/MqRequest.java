package com.starnet.ipcmonitorcloud.mq;

import lombok.Data;

/**
 * MqRequest
 *
 * @author wzz
 * @date 2020/8/24 13:36
 **/
@Data
public class MqRequest {
    private int command;

    public MqRequest() {}

    public MqRequest(Command command) {
        this.command = command.getType();
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public enum Command {

        /**
         * 推流命令
         */
        START_MONITOR(1),
        /**
         * 停止推流命令
         */
        STOP_MONITOR(2),
        GET_IPC_LIST(3);

        private int type;

        Command(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public static Command valueOf(int type) {
            for(Command command : values()) {
                if (type == command.type) {
                    return command;
                }
            }
            throw new IllegalArgumentException("No matching constant for [" + type + "]");
        }

        @Override
        public String toString() {
            return "Command{ [" + this.name() +
                    "] type=" + type +
                    '}';
        }
    }
}
