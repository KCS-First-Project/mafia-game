package com.mafiachat.server;

import static com.mafiachat.util.Constant.THOUSAND_MILLI_SECOND;

public enum Phase {
    LOBBY(0),
    DAY_CHAT(30 * THOUSAND_MILLI_SECOND),
    DAY_FIRST_VOTE(15 * THOUSAND_MILLI_SECOND),
    DAY_DEFENSE(15 * THOUSAND_MILLI_SECOND),
    DAY_SECOND_VOTE(10 * THOUSAND_MILLI_SECOND),
    NIGHT(10 * THOUSAND_MILLI_SECOND);

    public final int timeLimit;

    Phase(int timeLimit) {
        this.timeLimit = timeLimit;
    }
}
