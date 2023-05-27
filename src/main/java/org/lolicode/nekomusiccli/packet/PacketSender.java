package org.lolicode.nekomusiccli.packet;

import net.minecraft.server.MinecraftServer;

public interface PacketSender {
    void send(MinecraftServer server);
}
