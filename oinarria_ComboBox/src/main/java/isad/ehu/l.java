package isad.ehu;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photosets.Photoset;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;



public class l extends Application  {


    @Override
    public void start(Stage primaryStage) throws Exception {

        Zeharkatu k = new Zeharkatu();

        primaryStage.setTitle("ComboBox Experiment 1");

        //Lehenik comboBox --> Bildumak
        ComboBox comboBox = new ComboBox();
        ArrayList<Photoset> bildumak = k.bildumakLortu();

        for(int i = 0; i < bildumak.size(); i++) {
             comboBox.getItems().add(bildumak.get(i).getTitle());
        }

        comboBox.setEditable(true);

        ListView<String> list = new ListView<String>();
        comboBox.getSelectionModel().selectFirst();
        PhotoList a = k.bildumarenArgazkiakLortu(String.valueOf(comboBox.getValue()));
        Iterator o = a.iterator();
        while(o.hasNext()){
            Photo pho = (Photo) o.next();
            list.getItems().add(pho.getTitle());
        }


    //COMBOBOX-ARI EMATEAN
        comboBox.setOnAction(e -> {
            list.getItems().clear();
            try {
                PhotoList argazkiak = k.bildumarenArgazkiakLortu(String.valueOf(comboBox.getValue()));
                Iterator i = argazkiak.iterator();
                while(i.hasNext()){
                    Photo pho = (Photo) i.next();
                    list.getItems().add(pho.getTitle());
                }

            } catch (FlickrException ex) {
                ex.printStackTrace();
            }
        });
        list.setVisible(true);

        //label bat sortu eta barnean argazki bat jarri

        Label label = new Label("");

        list.setOnMouseClicked(e-> {
            try {
                FileInputStream input= new FileInputStream("/opt/oinarria/src/main/resources/"+list.getSelectionModel().getSelectedItem()+".jpg");
                Image image = new Image(input);
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                label.setGraphic(imageView);

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        //InputStream is = getClass().getResourceAsStream("/" + location);
        //  BufferedImage reader = ImageIO.read(is);
        //  return SwingFXUtils.toFXImage(reader, null);
        // imageView.setImage(lortuIrudia(fitx /* 48x48 */));


        //comboBox, list eta label pantailaratu
        //VBox = bertikalean jarri nahi baditugu gauzak. HBox= horizontalean jarri nahi baditugu gauzak. Así podemos añadir todos los elementos. Crear un VBox dentro de otro y tal

        VBox hbox = new VBox(comboBox,list,label);
        //VBox hbox = new VBox(comboBox,list);


        //Ondoren pantailaratzeko. Scene bat sortu eta primaryStage-an sartu
        Scene scene = new Scene(hbox, 250, 180);
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
