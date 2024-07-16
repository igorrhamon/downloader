package br.com.torrent;

import br.com.torrent.execptions.DownloaderException;
import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import jakarta.ws.rs.Path;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static io.quarkus.arc.impl.UncaughtExceptions.LOGGER;

@Path("/hello")
public class Downloader {


    /**
     * Downloads a torrent file from the specified URL.
     *
     * @param url The URL of the torrent file to be downloaded.
     * @throws IOException        If an I/O error occurs during the download process.
     * @throws URISyntaxException If the provided URL is not formatted correctly.
     */
    public void downloadTorrent(String url) throws IOException, URISyntaxException, DownloaderException {
        // Convert the URL to a URI
        URI uri = new URI(url);

        // Create an InetAddress object
        InetAddress address = InetAddress.getLocalHost();

        // Create a SharedTorrent object from the URI
        File torrentFile = null;
        try {
            torrentFile = Paths.get(uri).toFile();
        } catch (Exception e) {
            LOGGER.error("Failed to create");
            throw new DownloaderException(url, e);
        }
        SharedTorrent torrent = SharedTorrent.fromFile(torrentFile, new File("."));

        // Create the Client object
        Client client = new Client(address, torrent);

        // Start the client
        client.download();
    }
}