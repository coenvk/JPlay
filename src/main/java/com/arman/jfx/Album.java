package com.arman.jfx;

import java.util.*;

public class Album {

	List<Song> songs = new ArrayList<Song>();
	String albumName;
	
	public Album() {
		
	}
	
	public void add(Song song) {
		if (checkAlbum(song)) {
			songs.add(song);
		}
	}
	
	public void add(int index, Song song) {
		if (checkAlbum(song)) {
			songs.add(index, song);
		}
	}
	
	public void set(int index, Song song) {
		if (checkAlbum(song)) {
			songs.set(index, song);
		}
	}
	
	private boolean checkAlbum(Song song) {
		if (albumName == null || albumName.isEmpty()) {
			albumName = song.getAlbum();
			return true;
		}
		if (albumName.equals(song.getAlbum())) {
			return true;
		}
		return false;
	}
	
}
