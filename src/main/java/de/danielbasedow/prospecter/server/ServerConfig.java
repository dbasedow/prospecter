package de.danielbasedow.prospecter.server;


public class ServerConfig {
    private int port;
    private String bindInterface;
    private String homeDir;


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBindInterface() {
        return bindInterface;
    }

    public void setBindInterface(String bindInterface) {
        this.bindInterface = bindInterface;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

}
