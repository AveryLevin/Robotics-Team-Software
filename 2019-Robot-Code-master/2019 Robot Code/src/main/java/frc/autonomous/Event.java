package frc.autonomous;

import frc.utilities.SoftwareTimer;

public class Event {

    public enum EventType {
        DRIVE, ARM, SHOOTER; // arm and shooter are examples
    }

    private SoftwareTimer autoTimer;
    private double timedDuration;
    private EventType eventType;

    /**
     * Constructor
     * 
     * @param start start time
     * @param stop stop time
     * @param event type of event
     */
    public Event(double duration, EventType event) {
        timedDuration = duration;
        eventType = event;

    }

    /**
     * 
     * @return if time has expired
     */
    public boolean isExpired(){
        boolean value = false;
        value = autoTimer.isExpired();
        return value;
    }

    /**
     * 
     * @return status of action
     */
    public boolean isDone(){
        boolean value = false;
        return value;
    }

    
}