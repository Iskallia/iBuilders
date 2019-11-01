package iskallia.ibuilders.net;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public class NetAddress {

    private String ip;
    private int port;

    public NetAddress(String ip, int port) {
        this.ip = ip.trim();
        this.port = port;
    }

    @Override
    public String toString() {
        return this.ip + ":" + port;
    }

    @Nullable
    public static NetAddress fromString(String address) {
        String[] data = address.split(Pattern.quote(":"));
        if(data.length != 2)return null;

        try {return new NetAddress(data[0], Integer.parseInt(data[1].trim()));}
        catch(Exception e) {return null;}
    }

}
