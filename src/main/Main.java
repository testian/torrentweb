/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.sun.net.httpserver.*;
import java.net.*;
import java.io.*;
import torrentweb.*;
import java.util.*;
/**
 *
 * @author Dimitri Nüscheler
 */
public class Main {
public static void main(String[] args) {
    Configuration config = new Configuration();
    try {
    config.read(new File("torrentweb.cfg"));
    int port=8080;
    try {
    port = Integer.parseInt(config.get("port"));
    } catch (NumberFormatException ex) {
    }
    catch(EntryException ex) {
    }

    long period=3600000;
    try {
    period = Integer.parseInt(config.get("remove_period")) * 1000;
    } catch (NumberFormatException ex) {
    }
    catch(EntryException ex) {
    }
    

    String internal_root = config.get("internal_root");

    String internal_mirror = config.get("internal_mirror");
    String external_mirror = config.get("external_mirror");
    String announce_url = config.get("announce_url");
    String external_root = config.get("external_root");
    final TorrentManager tm = new TorrentManager(internal_mirror,external_mirror,announce_url,internal_root, external_root);

    HttpServer server = HttpServer.create(new InetSocketAddress(port),20);
    server.createContext("/", new Frontpage(internal_root,tm));
    server.start();
    System.out.println("TorrentWebServer running on port " + port);
    if (period>0) {
    System.out.println("Cleaning torrent directory every " + period/1000 + " seconds");
    new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    tm.clean();
                }

    }, 0, period);}
    } catch (IOException ex) {
    System.err.println(ex);
    }
    catch (EntryException ex) {
    System.err.println(ex.getMessage());
    }
}
}
