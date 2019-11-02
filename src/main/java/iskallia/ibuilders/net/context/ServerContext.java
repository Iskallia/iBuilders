package iskallia.ibuilders.net.context;

import iskallia.ibuilders.net.connection.Listener;
import iskallia.ibuilders.net.connection.ServerListener;
import net.minecraft.server.MinecraftServer;

public class ServerContext extends Context {

    public ServerListener serverListener;
    public Listener listener;
    public MinecraftServer minecraftServer;

}
