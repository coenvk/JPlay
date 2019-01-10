package com.arman.jfx;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Song extends File {

    public static final String UNKNOWN = "unknown";
    private static final long serialVersionUID = 1L;
    private final String filePath;
    private final String name;
    private String title;
    private String artist;
    private String album;

    public Song(String filePath) {
        super(filePath);
        this.filePath = filePath;
        this.name = filePath.replaceAll("//", "/");
        initialize();
    }

    private void initialize() {
        try {
            InputStream input = new FileInputStream(new File(filePath));
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseContext = new ParseContext();
            parser.parse(input, handler, metadata, parseContext);
            input.close();
            title = metadata.get("title");
            artist = metadata.get(XMPDM.ALBUM_ARTIST);
            album = metadata.get(XMPDM.ALBUM);
            title = (title == null) ? UNKNOWN : title;
            artist = (artist == null) ? UNKNOWN : artist;
            album = (album == null) ? UNKNOWN : album;
        } catch (SAXException | TikaException | IOException e) {
            title = UNKNOWN;
            artist = UNKNOWN;
            album = UNKNOWN;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return filePath;
    }

    public String getName() {
        return name;
    }

    public String[] getMetaData() {
        return new String[]{title, artist, album};
    }

}
