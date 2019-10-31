package iskallia.ibuilders.net.packet;

import net.minecraftforge.fml.relauncher.Side;

public abstract class Packet {

    public boolean canSendFrom(Side side) {
        if(side == Side.CLIENT) {
            return this instanceof C2SMessage;
        } else if(side == Side.SERVER) {
            return this instanceof S2CMessage;
        }

        return false;
    }

}
