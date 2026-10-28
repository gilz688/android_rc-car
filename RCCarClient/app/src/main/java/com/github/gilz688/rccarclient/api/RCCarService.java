package com.github.gilz688.rccarclient.api;

import com.tinder.scarlet.ws.Send;


public interface RCCarService {
    @Send
    void sendCommand(String rcCommand);
}
