package com.arman.jfx;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class PlayList {

	private List<Song> songs = new ArrayList<Song>();
	private List<MediaPlayer> players = new ArrayList<MediaPlayer>();
	
	public PlayList() {
		
	}
	
	public PlayList(String[] filePaths) {
		addAll(filePaths);
	}
	
	public PlayList(String directory) {
		addAll(directory);
	}
	
	public void addAll(String[] filePaths) {
		for (String filePath : filePaths) {
			if (filePath.endsWith(".mp3") || filePath.endsWith(".msa")) {
				add(filePath.replaceAll("//", "/"));
			} else {
				System.out.println("Not a supported media file: " + filePath);
			}
		}
	}
	
	public void addAll(String directory) {
		int sizeBefore = players.size();
		final File dir = new File(directory);
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			System.out.println("Cannot find audio source directory: " + dir
					+ " please supply a directory as a command line argument");
			return;
		}
		for (String file : dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				for (String ext : new String[]{".mp3", ".msa"}) {
					if (name.endsWith(ext)) {
						return true;
					}
				}
				return false;
			}
		})) {
			add((dir + "/" + file).replaceAll("//", "/"));
		}
		if (players.size() == sizeBefore) {
			System.out.println("No audio found in: " + dir);
			return;
		}
	}
	
	public void add(String filePath) {
		Song song = new Song(filePath);
		add(song);
	}
	
	public List<Song> getSongs() {
		return songs;
	}
	
	public void setAll(List<Song> newSongs, List<MediaPlayer> newPlayers) {
		this.songs = newSongs;
		this.players = newPlayers;
	}
	
	public List<MediaPlayer> getPlayers() {
		return players;
	}
	
	public Song getSong(int index) {
		return songs.get(index);
	}
	
	public void set(int index, Song song) {
		songs.set(index, song);
		players.set(index, createPlayer(song));
	}
	
	public void add(Song song) {
		songs.add(song);
		players.add(createPlayer(song));
	}
	
	public void add(int index, Song song) {
		songs.add(index, song);
		players.add(index, createPlayer(song));
	}
	
	public MediaPlayer getPlayer(int index) {
		return players.get(index);
	}
	
	private MediaPlayer createPlayer(Song song) {
		final Media m = new Media(song.toURI().toString());
		final MediaPlayer mp = new MediaPlayer(m);
		mp.setOnError(new Runnable() {
			public void run() {
				System.out.println("Media error occurred: " + mp.getError());
			}
		});
		return mp;
	}
	
}
