package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06.*;

import java.nio.ByteBuffer;

public class RFC5424FrameClock implements TerminalClock<_RFC5424Frame> {

    private static final _RFC5424FrameStub rfc5424FrameStub = new _RFC5424FrameStub();

    private static final _PriorityStub priorityStub = new _PriorityStub();

    private final PriorityClock priorityClock;
    private final MessageClock messageClock;


    private _Priority priority;

    private _RFC5424Frame rfc5424Frame;


    public RFC5424FrameClock() {

        this.priorityClock = new PriorityClock();
        this.messageClock = new MessageClock();

        this.priority = priorityStub;
        this.rfc5424Frame = rfc5424FrameStub;
    }

    public synchronized void submit(ByteBuffer input) {
        while (input.hasRemaining()) {
            if (priority.isStub()) {
                priority = priorityClock.submit(input);
            }
            // TODO rest
            else {
                // messages are never stub
                messageClock.submit(input);
                break;
            }
        }
    }

    public _RFC5424Frame get() {
        if (rfc5424FrameStub.isStub()) {
            // TODO add if (!structuredData.isStub())
            rfc5424Frame = new _RFC5424FrameImpl(
                    priority,
                    messageClock.get()
            );
        }
        return rfc5424Frame;
    }
}
