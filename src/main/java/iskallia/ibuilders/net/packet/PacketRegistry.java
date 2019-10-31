package iskallia.ibuilders.net.packet;

import java.util.ArrayList;
import java.util.List;

public class PacketRegistry {

    private static List<Class<? extends Packet>> packetsClasses = new ArrayList<>();

    public static void registerPacket(Class<? extends Packet> packetClass) {
        packetsClasses.add(packetClass);
    }

    public static Class<Packet> getPacketClass(int id) {
        return (Class<Packet>)packetsClasses.get(id);
    }

    public static int getPacketId(Packet packet) {
        return packetsClasses.indexOf(packet.getClass());
    }

}
