package com.mydemoapp2;

import java.net.URL;

/**
 * Created by selvaanb on 1/10/2018.
 */

public class Connection {

    private String name;
    private String version;

    public Connection(String turl, String tname) {
        name = tname;
        connectionUrl = turl;
    }

    public Connection(){

    }

    public Connection(String name, String version, String connectionUrl) {
        this.name = name;
        this.version = version;
        this.connectionUrl = connectionUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    private String connectionUrl;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
