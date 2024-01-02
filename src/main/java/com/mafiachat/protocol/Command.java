package main.java.com.mafiachat.protocol;

public enum Command {
    //Chat 관련
    NORMAL,
    SYSTEM,
    INIT_ALIAS,
    USER_LIST,
    ENTER_ROOM,
    EXIT_ROOM,

    //Game 관련
    READY,
    PLAYER_LIST,
    VOTE,
    ACT_ROLE,

    //phase notify 커맨드
    LOBBY,
    DAY_CHAT,
    DAY_FIRST_VOTE,
    DAY_DEFENSE,
    DAY_SECOND_VOTE,
    NIGHT,
    UNKNOWN;
}
