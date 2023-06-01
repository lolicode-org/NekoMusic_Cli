package org.lolicode.nekomusiccli.events;

public class Events {
    public static void register() {
        OnJoinServer.register();
        OnQuitServer.register();
        OnClientStop.register();
        KeyboardEvent.register();
        HudRender.register();
    }
}
