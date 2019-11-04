package iskallia.ibuilders.net.packet.util;

import iskallia.ibuilders.net.packet.Packet;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PacketRegistry {

    private static List<Class<? extends Packet>> packetsClasses = new ArrayList<>();

    public static void registerPacket(Class<? extends Packet> packetClass) {
        if(packetsClasses.contains(packetClass)) {
            throw new InvalidParameterException("Can't register " + packetClass.getName() + " twice");
        }

        packetsClasses.add(packetClass);
    }

    public static Class<Packet> getPacketClass(int id) {
        return (Class<Packet>)packetsClasses.get(id);
    }

    public static int getPacketId(Packet packet) {
        return packetsClasses.indexOf(packet.getClass());
    }

    public static List<String> getRegistry() {
        return packetsClasses.stream().map(Class::getName).collect(Collectors.toList());
    }

}
