package iskallia.ibuilders.net;

import iskallia.ibuilders.init.InitConfig;

public class NetworkThread extends Thread {

    public NetworkThread() {

    }

    @Override
    public void run() {
        while(true) {
            for(NetAddress address: InitConfig.CONFIG_CONNECTION.PLOT_SERVERS) {

            }

            for(NetAddress address: InitConfig.CONFIG_CONNECTION.MAIN_SERVERS) {

            }

            this.sleep();
        }
    }

    private void sleep() {
        try {Thread.sleep(50);}
        catch(InterruptedException e) {e.printStackTrace();}
    }

}
