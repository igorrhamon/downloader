package br.com.torrent;

import br.com.torrent.execptions.DownloaderException;
import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static io.quarkus.arc.impl.UncaughtExceptions.LOGGER;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DownloaderTest {

    private Downloader downloader;

    @BeforeEach
    public void setUp() {
        downloader = new Downloader();
    }

    @Test
    public void testDownloadTorrentCreatesSharedTorrent() throws IOException, URISyntaxException {
        String url = "file:///path/to/torrent/file.torrent";
        File torrentFile = Paths.get(new URI(url)).toFile();
        SharedTorrent mockTorrent = mock(SharedTorrent.class);
        Client mockClient = mock(Client.class);

        try (MockedStatic<SharedTorrent> sharedTorrentMockedStatic = mockStatic(SharedTorrent.class);
             MockedStatic<InetAddress> inetAddressMockedStatic = mockStatic(InetAddress.class)) {

            sharedTorrentMockedStatic.when(() -> SharedTorrent.fromFile(torrentFile, new File(".")))
                    .thenReturn(mockTorrent);
            inetAddressMockedStatic.when(InetAddress::getLocalHost).thenReturn(mock(InetAddress.class));

            downloader.downloadTorrent(url);

            sharedTorrentMockedStatic.verify(() -> SharedTorrent.fromFile(torrentFile, new File(".")), times(1));
        } catch (DownloaderException e) {
            LOGGER.error("Error downloading torrent");
        }
    }

    @Test
    public void testDownloadTorrentCreatesClient() throws IOException, URISyntaxException {
        String url = "file:///path/to/torrent/file.torrent";
        File torrentFile = Paths.get(new URI(url)).toFile();
        SharedTorrent mockTorrent = mock(SharedTorrent.class);
        InetAddress mockAddress = mock(InetAddress.class);
        Client mockClient = mock(Client.class);

        try (MockedStatic<SharedTorrent> sharedTorrentMockedStatic = mockStatic(SharedTorrent.class);
             MockedStatic<InetAddress> inetAddressMockedStatic = mockStatic(InetAddress.class);
             MockedStatic<Client> clientMockedStatic = mockStatic(Client.class)) {

            sharedTorrentMockedStatic.when(() -> SharedTorrent.fromFile(torrentFile, new File(".")))
                    .thenReturn(mockTorrent);
            inetAddressMockedStatic.when(InetAddress::getLocalHost).thenReturn(mockAddress);
            clientMockedStatic.when(() -> new Client(mockAddress, mockTorrent)).thenReturn(mockClient);

            downloader.downloadTorrent(url);

            clientMockedStatic.verify(() -> new Client(mockAddress, mockTorrent), times(1));
        } catch (DownloaderException e) {
            LOGGER.error("Error downloading torrent");
        }
    }

    @Test
    public void testDownloadTorrentStartsClientDownload() throws IOException, URISyntaxException {
        String url = "file:///path/to/torrent/file.torrent";
        File torrentFile = Paths.get(new URI(url)).toFile();
        SharedTorrent mockTorrent = mock(SharedTorrent.class);
        InetAddress mockAddress = mock(InetAddress.class);
        Client mockClient = mock(Client.class);

        try (MockedStatic<SharedTorrent> sharedTorrentMockedStatic = mockStatic(SharedTorrent.class);
             MockedStatic<InetAddress> inetAddressMockedStatic = mockStatic(InetAddress.class);
             MockedStatic<Client> clientMockedStatic = mockStatic(Client.class)) {

            sharedTorrentMockedStatic.when(() -> SharedTorrent.fromFile(torrentFile, new File(".")))
                    .thenReturn(mockTorrent);
            inetAddressMockedStatic.when(InetAddress::getLocalHost).thenReturn(mockAddress);
            clientMockedStatic.when(() -> new Client(mockAddress, mockTorrent)).thenReturn(mockClient);

            downloader.downloadTorrent(url);

            verify(mockClient, times(1)).download();
        } catch (DownloaderException e) {
            LOGGER.error("Error downloading torrent");
        }
    }

    @Test
    public void testDownloadTorrentHandlesIOException() {
        String url = "file:///path/to/torrent/file.torrent";

        assertThrows(IOException.class, () -> {
            downloader.downloadTorrent(url);
        });
    }

    @Test
    public void testDownloadTorrentHandlesURISyntaxException() {
        String url = "invalid-url";

        assertThrows(DownloaderException.class, () -> {
            downloader.downloadTorrent(url);
        });
    }
}