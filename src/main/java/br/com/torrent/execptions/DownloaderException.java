package br.com.torrent.execptions;

public class DownloaderException extends Throwable {
    public DownloaderException(String message) {
        super(message);
    }
    public DownloaderException(String message, Throwable cause) {
        super(message, cause);
    }


}
