package org.commonjava.indy.httprox.jfr;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import jdk.jfr.Event;
import jdk.jfr.Category;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.Description;
import jdk.jfr.StackTrace;
import jdk.jfr.Timespan;
import jdk.jfr.Timestamp;

@Name (ProxyDurationEvent.NAME)
@Label ("Proxy invocation")
@Category ("Proxy")
@Description ("Proxy invocation")
@StackTrace(false)
public class ProxyDurationEvent extends Event
{
   static final String NAME = "o.c.i.h.j.ProxyDurationEvent";
   private static final Clock CLK = Clock.systemDefaultZone();

   @Label("Start Time")
   @Description("Point in time the event started")
   @Timestamp(Timestamp.MILLISECONDS_SINCE_EPOCH)
   public long startTime;

   @Label("Recording Duration")
   @Description("Point in time the event finished")
   @Timespan(Timespan.MILLISECONDS)
   public long duration;

   public ProxyDurationEvent() {
   }

   public void beginTiming() {
       this.startTime = CLK.instant().toEpochMilli();
   }
   public void stopTiming() {
       long end = CLK.instant().toEpochMilli();
       duration = end - startTime;
   }
}
