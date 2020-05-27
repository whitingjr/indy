package org.commonjava.indy.httprox.jfr;

import java.time.Duration;
import java.time.Clock;
import java.time.Instant;

import jdk.jfr.Event;
import jdk.jfr.Category;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.Description;
import jdk.jfr.StackTrace;
import jdk.jfr.Timespan;
import jdk.jfr.Timestamp;

@Name (ProxyTunnelDurationEvent.NAME)
@Label ("Proxy tunnel")
@Category ({"Proxy", "Tunnel"})
@Description ( "Proxy tunnel duration")
@StackTrace(false)
public class ProxyTunnelDurationEvent extends Event
{
    static final String NAME = "o.c.i.h.j.ProxyTunnelDurationEvent";
    private static final Clock CLK = Clock.systemDefaultZone();

    @Label("Start Time")
    @Description("Point in time the event started")
    @Timestamp(Timestamp.MILLISECONDS_SINCE_EPOCH)
    public long startTime;

    @Label("Recording Duration")
    @Description("Duration of the proxy tunnel needed to complete")
    @Timespan(Timespan.MILLISECONDS)
    public long duration;

    public ProxyTunnelDurationEvent() {
    }

    public void beginTiming() {
        this.startTime = CLK.instant().toEpochMilli();
    }
    public void stopTiming() {
        long end = CLK.instant().toEpochMilli();
        duration = end - startTime;
    }
}
