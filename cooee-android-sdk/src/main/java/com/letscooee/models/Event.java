package com.letscooee.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Event class is sent as body to server when a user event needs to be tracked.
 *
 * @author Abhishek Taparia
 */
public class Event {

    @SerializedName("name")
    private String eventName;

    @SerializedName("userEventProperties")
    private Map<String, String> eventProperties;

    public Event(String eventName, Map<String, String> eventProperties) {
        this.eventName = eventName;
        this.eventProperties = eventProperties;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Map<String, String> getEventProperties() {
        return eventProperties;
    }

    public void setEventProperties(Map<String, String> eventProperties) {
        this.eventProperties = eventProperties;
    }
}
