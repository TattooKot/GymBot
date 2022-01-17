package com.example.demo.util;

public enum Commands {

    START("/start"),

//    Admin commands
    GET_ALL("/get_all"),
    CREATE("/create"),
    DELETE("/delete"),
    PAY_SOON("/pay_soon"),
    GET_ALL_ALL("/get_all_all"),
    SEND("/send"),
    ALL_CONNECTED("/all_connected"),
    ADD_PAY("Add pay"),
    NON_ACTIVE("Delete"),
    ACTIVE("Active"),
    PHONE("Phone"),
    COUNT("Count"),
    NAME("Name"),
    PAY("Pay"),

//    User commands
    SHO_TAM("/sho_tam"),
    INFO("/info"),
    RESET("/reset"),
    HELP("/help"),

//    User messages
    DISCLAIMER("Привіт \uD83D\uDD90️\uD83D\uDE0A\n" +
                      "Це повідомлення створено автоматично, і надіслане всім хто підключений до боту\uD83E\uDD16\n" +
                      "Відповідати на нього не треба ❌\n" +
                      "\n");

    private final String command;

    Commands(String s) {
        this.command = s;
    }

    public String getCommand() {
        return command;
    }
}