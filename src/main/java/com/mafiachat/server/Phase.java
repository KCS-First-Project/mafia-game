package com.mafiachat.server;

import static com.mafiachat.util.Constant.THOUSAND_MILLI_SECOND;

public enum Phase {
    LOBBY(0, ""),
    DAY_CHAT(30 * THOUSAND_MILLI_SECOND, "마피아로 의심되는 플레이어에 대해 논의합시다."),
    DAY_FIRST_VOTE(15 * THOUSAND_MILLI_SECOND, "첫 번째 투표 시간입니다."),
    DAY_DEFENSE(15 * THOUSAND_MILLI_SECOND, "마피아로 지목된 플레이어의 최후변론 시간입니다."),
    DAY_SECOND_VOTE(10 * THOUSAND_MILLI_SECOND, "두 번째 투표 시간입니다."),
    NIGHT(10 * THOUSAND_MILLI_SECOND, "밤이 되었습니다.");

    public final int timeLimit;
    public final String announceMessage;

    Phase(int timeLimit, String announceMessage) {
        this.timeLimit = timeLimit;
        this.announceMessage = announceMessage;
    }
}
