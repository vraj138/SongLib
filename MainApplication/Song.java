/* Authors: Deep Parekh and Vraj Patel */

package MainApplication;

public class Song {
	
	public String name;
	public String artist;
	public String album = null;
	public int year = 0;

	public Song(String newName, String newArtist, String newAlum, int newYear) {
		name = newName;
		artist = newArtist;
		album = newAlum;
		year = newYear;
	}
	
	public String toFullString() {
		String fullStr = name + ", " + artist;
		if(album!=null) {
			fullStr = fullStr + ", " + album;
		}
		
		if(year!=-1) {
			fullStr = fullStr + ", " + year;
		}
		
		return fullStr;
	}
	
	public String toString() {
		return name + " by " + artist;
	}
	
	public String getName() {
		return name;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getAlbum() {
		return album;
	}
	
	public int getYear() {
		return year;
	}
	
	public void changeName(String newName) {
		name = newName;
	}
	
	public void changeArtist(String newArtist) {
		artist = newArtist;
	}
	
	public void changeAlbum(String newAlbum) {
		album = newAlbum;
	}
	
	public void changeYear(int newYear) {
		year = newYear;
	}
	
	public int compareTo(Song A, Song B) {
		if(A.getName().compareTo(B.getName())!=0) {
			return A.getName().compareTo(B.getName());
		} else 
			return A.getArtist().compareTo(B.getArtist());
	}
}
