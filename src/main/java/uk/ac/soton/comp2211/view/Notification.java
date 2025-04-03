package uk.ac.soton.comp2211.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;

public class Notification extends Popup {
	private Text message;
	private final Button closeButton = new Button("Close");
	private final VBox box = new VBox(10);

	public Notification() {
		message = new Text("NO MESSAGE SET");
		var warning = new Label("WARNING");
		warning.setFont(new Font("Haettenschweiler", 40)); warning.setTextFill(Paint.valueOf("Red"));
		box.getChildren().addAll(warning, message, closeButton);
		box.setAlignment(Pos.CENTER);
		box.setPadding(new Insets(15));
		box.setBackground(new Background(new BackgroundFill(Paint.valueOf("White"),new CornerRadii(10), null)));
		box.setBorder(new Border(new BorderStroke(Paint.valueOf("Red"), BorderStrokeStyle.DASHED, new CornerRadii(10), new BorderWidths(2))));
		getContent().add(box);
		closeButton.setOnAction(e -> hide());
	}

	/**
	 * Allows the notification to be easily displayed on a window in a simple function
	 * @param window The window to display the notification on
	 * @param exceptions The text from exceptions to display
	 */
	public void display(Window window, String exceptions) {
		if (window == null) return;
		this.box.getChildren().removeAll(this.message, closeButton);
		this.message = new Text(exceptions);
		this.box.getChildren().addAll(this.message, closeButton);
		show(window);
		centerOnScreen();
	}

	/* Example of use (in a scene):
				Notification notif = new Notification();
				notif.display(this.getWindow(), "An error occurred:\n"+e.getMessage);
				 - OR
				notif.display(this.getScene().getWindow(), "An error occurred:\n"+e.getMessage);
	 */

}