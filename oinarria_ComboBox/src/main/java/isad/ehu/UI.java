package isad.ehu;


import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.photosets.Photoset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class UI extends JFrame {

  //JFrame -  BoxLayout - el JLabel y el JComboBox

    public static void main (String[] args){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try{
                    UI ui= new UI();
                    ui.prestatu();
                    ui.dana();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    public void prestatu(){
        this.setVisible(true);
        this.setTitle("UI");
        this.setSize(320,320);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void dana() throws IOException, FlickrException {

        //BoxLayout
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));


        //JLabel
        JLabel label= new JLabel("Bildumak:");
        add(label);

        //JComboBox
        ArrayList<Photoset> bildumak = new Zeharkatu().bildumakLortu();

        String[] array = new String[bildumak.size()];
        for(int i = 0; i < array.length; i++) {
           // array[i] = bildumak.get(i);
        }

        JComboBox<String> box = new JComboBox<String>(array);
        add(box);

    }


}
