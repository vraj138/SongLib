/* Authors: Deep Parekh and Vraj Patel */ 

package View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

/* using Json-Java library to read from and write to Json file */
import org.json.JSONArray;
import org.json.JSONException;

import MainApplication.Song;

public class Controller {
	@FXML ListView<Song> listView;
	@FXML Button edit;
	@FXML Button add;
	@FXML Button delete;
	@FXML Text name;
	@FXML Text artist;
	@FXML Text album;
	@FXML Text year;
	@FXML TextField songField;
	@FXML TextField artistField;
	@FXML TextField albumField;
	@FXML TextField yearField;
	
	
	private ObservableList<Song> obListSongs;
	private boolean	first;

	public void start(Stage mainStage) {
		
		// Open JSON content as string
		try {
			String content = readFile("SongData/songs.json");
			
			
			JSONArray songArray = new JSONArray(content);
			ArrayList<Song> songs = new ArrayList<Song>();
			
			for(int i=0; i<songArray.length(); i++) {
				Song fromstorage = new Song(songArray.getJSONObject(i).getString("name"),songArray.getJSONObject(i).getString("artist"), songArray.getJSONObject(i).getString("album"),songArray.getJSONObject(i).getInt("year"));
				songs.add(fromstorage);
			}
			
			
			// create ObservableList from an ArrayList
			obListSongs = FXCollections.observableArrayList(songs);
			listView.setItems(obListSongs);
			first = true;
			
			// preselect first one
			listView.getSelectionModel().select(0);
			
			// listen for changes in selection
			listView.getSelectionModel().selectedIndexProperty().addListener(
					(obs, oldVal, newVal) -> showItem(mainStage));
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Song firstSong = listView.getSelectionModel().getSelectedItem();
		if(firstSong == null) {
			//no items left
			name.setText("");
			artist.setText("");
			album.setText("");
			year.setText("");
			
		}
		else {
			
			name.setText(firstSong.getName());
			
			if(firstSong.getArtist().equals("")) {
				artist.setText("Not defined");
			}else {
				artist.setText(firstSong.getArtist());
			}
			
			artist.setText(firstSong.getArtist());
			
			if(firstSong.getAlbum().equals("")) {
				album.setText("Not defined");
			}
			else {
				album.setText(firstSong.getAlbum());
			}
			
			if(firstSong.getYear() == -1) {
				year.setText("Not defined");
			}
			else {
				year.setText(Integer.toString(firstSong.getYear()));
			}
		}
		
	}
	
	private String readFile(String path) throws IOException {
		File file = new File(path);
		
		// Build a string from file's contents
		StringBuilder content = new StringBuilder((int)file.length());
		
		try (Scanner scanner = new Scanner(file)) {
			while(scanner.hasNextLine()) {
				content.append(scanner.nextLine() + System.lineSeparator());
			}
			return content.toString();
		} 
	}
	
		
	public void editAction(ActionEvent e) throws IOException {
		if(first) {
			//if its on a new item and edit hasn't been clicked before
			String currentSong = name.getText();
			String currentArtist = artist.getText();
			String currentAlbum = album.getText();
			String currentYear = year.getText();
			songField.setText(currentSong);
			artistField.setText(currentArtist);
			edit.setText("Save");
			add.setDisable(true);
			delete.setDisable(true);
			
			if(currentAlbum.equals("Not defined")) {
				currentAlbum="";
			}
			
			if(currentYear.equals("Not defined")) {
				currentYear="";
			}
			
			albumField.setText(currentAlbum);
			yearField.setText(currentYear);
			first=false;
		}
		else {
			int counter=0;
			if(songField.getText().isEmpty() || artistField.getText().isEmpty()) {
				createErrorAlert("Error During Edit", "Must add song name and artist!");
				return;
			}
			
			while(counter<obListSongs.size()) {
				if(obListSongs.get(counter).getName().equalsIgnoreCase(songField.getText()) &&obListSongs.get(counter).getArtist().equalsIgnoreCase(artistField.getText())&&counter!=obListSongs.indexOf(listView.getSelectionModel().getSelectedItem())) {
					createErrorAlert("Error During Edit", "Already Entered");
					return;
				}
				
				counter++;
			}
			
			if(yearField.getText().isEmpty()) {
				
				Song newadd=new Song(songField.getText().trim(),artistField.getText().trim(),albumField.getText().trim(),-1);
				obListSongs.remove(listView.getSelectionModel().getSelectedItem());
				obListSongs.add(newadd);
				listView.getSelectionModel().clearSelection();
				obListSongs.sort((a,b) -> a.getName().compareToIgnoreCase(b.getName())==0 ? a.getArtist().compareToIgnoreCase(b.getArtist()) : a.getName().compareToIgnoreCase(b.getName()));
				listView.getSelectionModel().select(obListSongs.indexOf(newadd));
			}
			else {
				try{	
					Song newadd=new Song(songField.getText().trim(),artistField.getText().trim(),albumField.getText().trim(),Integer.parseInt(yearField.getText().trim()));
					obListSongs.remove(listView.getSelectionModel().getSelectedItem());
					obListSongs.add(newadd);
					obListSongs.sort((a,b) -> a.getName().compareToIgnoreCase(b.getName())==0 ? a.getArtist().compareToIgnoreCase(b.getArtist()) : a.getName().compareToIgnoreCase(b.getName()));
				
				}
				catch (NumberFormatException f) {
					createErrorAlert("Error During Edit", "Year must be an integer!");
					return;
				}
			}
			
			saveSongs(obListSongs);
			edit.setText("Edit");
			add.setDisable(false);
			delete.setDisable(false);
		}

	}
	
	public void addAction(ActionEvent event) throws IOException {
		int counter = 0;
		if(songField.getText().isEmpty() || artistField.getText().isEmpty()) {
			createErrorAlert("Error During Add", "Must add Song name and Artist name!");
			return;
		}
		
		while(counter < obListSongs.size()) {
			if(obListSongs.get(counter).getName().equalsIgnoreCase(songField.getText()) && obListSongs.get(counter).getArtist().equalsIgnoreCase(artistField.getText())) {
				createErrorAlert("Error During Add", "Song already exists!");
				return;
			}
			counter++;
		}
		
		if(yearField.getText().isEmpty()) {
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure?");
			// alert.showAndWait();
			
			ButtonType buttonTypeOkay = new ButtonType("OK");
			ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

			alert.getButtonTypes().setAll(buttonTypeOkay, buttonTypeCancel);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOkay){
			    // ... user chose "OK"
				Song newadd = new Song(songField.getText().trim(),artistField.getText().trim(),albumField.getText().trim(),-1);
				obListSongs.add(newadd);
				listView.getSelectionModel().clearSelection();
				obListSongs.sort((a,b) -> a.getName().compareToIgnoreCase(b.getName())==0 ? a.getArtist().compareToIgnoreCase(b.getArtist()) : a.getName().compareToIgnoreCase(b.getName()));
				listView.getSelectionModel().select(obListSongs.indexOf(newadd));
				
				saveSongs(obListSongs);
				return;
				
			} else {
			    // ... user chose CANCEL
				alert.close();
			}
		}
		else {
			
				
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setHeaderText(null);
				alert.setContentText("Are you sure?");
				// alert.showAndWait();
				
				ButtonType buttonTypeOkay = new ButtonType("OK");
				ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

				alert.getButtonTypes().setAll(buttonTypeOkay, buttonTypeCancel);

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == buttonTypeOkay){
				    // ... user chose "OK"
					Song newadd = new Song(songField.getText().trim(),artistField.getText().trim(),albumField.getText().trim(),Integer.parseInt(yearField.getText()));
					obListSongs.add(newadd);
					listView.getSelectionModel().clearSelection();
					obListSongs.sort((a,b) -> a.getName().compareToIgnoreCase(b.getName())==0 ? a.getArtist().compareToIgnoreCase(b.getArtist()) : a.getName().compareToIgnoreCase(b.getName()));
					listView.getSelectionModel().select(obListSongs.indexOf(newadd));
					
					saveSongs(obListSongs);
					return;
					
				} else {
				    // ... user chose CANCEL or closed the dialog
					alert.close();
				}
			
			
			saveSongs(obListSongs);
		}
		
		
	}
	
	public void deleteAction(ActionEvent e) throws IOException {
		if(obListSongs.size() == 0) {
			createErrorAlert("Error", "No songs to delete!");
		} 
		
		else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure?");
//			alert.showAndWait();
			
			ButtonType buttonTypeOkay = new ButtonType("OK");
			ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

			alert.getButtonTypes().setAll(buttonTypeOkay, buttonTypeCancel);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOkay){
			    // ... user chose "OK"
				int temp=obListSongs.indexOf(listView.getSelectionModel().getSelectedItem());
				obListSongs.remove(listView.getSelectionModel().getSelectedItem());
				listView.getSelectionModel().select(temp);
				Song item = listView.getSelectionModel().getSelectedItem();
			
				if(item==null) {
					//no items left
					saveSongs(obListSongs);
					name.setText("");
					artist.setText("");
					album.setText("");
					year.setText("");
					return;
				}
				
				first=true;
		
				name.setText(item.getName());
				artist.setText(item.getArtist());
				
				if(item.getAlbum().equals("")) {
					album.setText("Not defined");
				}else {
					album.setText(item.getAlbum());
				}
				
				if(item.getYear()==-1) {
					year.setText("Not defined");
				}else {
					year.setText(Integer.toString(item.getYear()));
				}
							
				saveSongs(obListSongs);
				
			} else {
			    // ... user chose CANCEL or closed the dialog
				alert.close();
			}
			
			
		}
		
	}
	
	
	/* CHANGING DETAILS PANE */
	
	private void showItem(Stage mainStage) {
		Song item = listView.getSelectionModel().getSelectedItem();
		first=true;
		add.setDisable(false);
		delete.setDisable(false);
		edit.setText("Edit");
		
		if(item==null) {
			return;
		}
		
		songField.setText("");
		artistField.setText("");
		albumField.setText("");
		yearField.setText("");
		name.setText(item.getName());
		artist.setText(item.getArtist());
		
		if(item.getAlbum().equals("")) {
			album.setText("Not defined");
		}else {
			album.setText(item.getAlbum());
		}
		
		if(item.getYear()==-1) {
			year.setText("Not defined");
		}else {
			year.setText(Integer.toString(item.getYear()));
		}
	}
	
	/* Error pop-ups */
	public void createErrorAlert(String errorTitle, String errorMessage) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(errorTitle);
		alert.setHeaderText(null);
		alert.setContentText(errorMessage);
		alert.showAndWait();
	}
	
	/*  Confirmation pop-ups */
	public void createConfirmationAlert(String msgTitle, String actualMsg) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(msgTitle);
		alert.setHeaderText(null);
		alert.setContentText(actualMsg);
		alert.showAndWait();
		
		ButtonType buttonTypeOkay = new ButtonType("OK");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOkay, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOkay){
		    // ... user chose "One"
			
		} else {
		    // ... user chose CANCEL or closed the dialog
		}
	}
	
	public void saveSongs(ObservableList<Song> songList) throws IOException {
		JSONArray songs = new JSONArray(obListSongs);
		try (FileWriter file = new FileWriter("SongData/songs.json")) {
			file.write(songs.toString());
//			System.out.println("Successfully Copied JSON Object to File...");
//			System.out.println("\nJSON Object: " + songs);
		}
	}
}