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
import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        String name = "C:/Users/Coen/Dropbox/myMusic/onPhone/"
                + "Arctic Monkeys - Do I Wanna Know_ (Official Video).mp3";
        try {
            InputStream input = new FileInputStream(new File(name));
            ContentHandler handler = new DefaultHandler();
            File file = new File(name);
            System.out.println(file.getName());
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseContext = new ParseContext();
            parser.parse(input, handler, metadata, parseContext);
            input.close();
            System.out.println(Arrays.toString(metadata.names()));
            System.out.println("Title: " + metadata.get("title"));
            System.out.println("Artists: " + metadata.get(XMPDM.ALBUM_ARTIST));
            System.out.println("Album: " + metadata.get(XMPDM.ALBUM));
        } catch (SAXException | TikaException | IOException e) {
            e.printStackTrace();
        }
    }

}
