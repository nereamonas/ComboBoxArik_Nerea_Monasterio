package isad.ehu;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.Size;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.flickr4java.flickr.util.AuthStore;
import com.flickr4java.flickr.util.FileAuthStore;
import com.flickr4java.flickr.util.IOUtilities;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuth1Token;
import javafx.application.Application;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * A simple program to backup all of a users private and public photos in a photoset aware manner. If photos are classified in multiple photosets, they will be
 * copied. Its a sample, its not perfect :-)
 * <p>
 * This sample also uses the AuthStore interface, so users will only be asked to authorize on the first run.
 *
 * @author Matthew MacKenzie
 * @version $Id: Backup.java,v 1.6 2009/01/01 16:44:57 x-mago Exp $
 */

public class Zeharkatu {

    private final String nsid;

    private final Flickr flickr;

    private AuthStore authStore;

    public Zeharkatu() throws FlickrException, IOException {

        Properties properties;
        InputStream in = null;
        try {
            in = AuthExample.class.getResourceAsStream("/setup.properties");
            properties = new Properties();
            properties.load(in);
        } finally {
            IOUtilities.close(in);
        }
        this.flickr = new Flickr(properties.getProperty("apiKey"), properties.getProperty("secret"), new REST());
        this.nsid = properties.getProperty("nsid");
        this.authStore = new FileAuthStore(new File(System.getProperty("user.home") + File.separatorChar + ".flickrAuth"));


    }


    private void authorize() throws IOException, FlickrException {
        AuthInterface authInterface = flickr.getAuthInterface();
        OAuth1RequestToken requestToken = authInterface.getRequestToken();

        String url = authInterface.getAuthorizationUrl(requestToken, Permission.READ);
        System.out.println("Follow this URL to authorise yourself on Flickr");
        System.out.println(url);
        System.out.println("Paste in the token it gives you:");
        System.out.print(">>");

        String tokenKey = new Scanner(System.in).nextLine();

        OAuth1Token accessToken = authInterface.getAccessToken(requestToken, tokenKey);

        Auth auth = authInterface.checkToken(accessToken);
        RequestContext.getRequestContext().setAuth(auth);
        this.authStore.store(auth);
        System.out.println("Thanks.  You probably will not have to do this every time.  Now starting backup.");

    }

    public void lortu() throws Exception {

        this.kautotu();

        PhotosetsInterface pi = flickr.getPhotosetsInterface(); // lortu bildumak kudeatzeko interfazea
        PhotosInterface photoInt = flickr.getPhotosInterface(); // lortu argazkiak kudeatzeko interfazea
        Map<String, Collection> allPhotos = new HashMap<String, Collection>(); // sortu datu-egitura bat bildumak gordetzeko

        Iterator sets = pi.getList(this.nsid).getPhotosets().iterator(); // nsid erabiltzailearen bildumak zeharkatzeko iteratzailea lortu

        while (sets.hasNext()) { // bildumak dauden bitartean, zeharkatu
            Photoset set = (Photoset) sets.next(); // bilduma lortu
            PhotoList photos = pi.getPhotos(set.getId(), 500, 1);  // bildumaren lehenengo 500 argazki lortu
            /*Iterator d = photos.iterator();
            for (int u=0;u<photos.size();u++){
                Photo p = (Photo) d.next();
                System.out.println(p.getTitle());
            }
            */
            System.out.println(set.getTitle() + set.getId() + set.getPhotoCount());
            allPhotos.put(set.getTitle(), photos);  // txertatu (bilduma --> bere argazkiak)
        }

        int notInSetPage = 1;  // argazki batzuk bilduma batean sartu gabe egon daitezke...
        Collection notInASet = new ArrayList(); // horiek ere jaso nahi ditugu
        while (true) { // lortu bildumarik gabeko argazkiak, 50naka
            Collection nis = photoInt.getNotInSet(50, notInSetPage);
            notInASet.addAll(nis);
            if (nis.size() < 50) {
                break;
            }
            notInSetPage++;
        }
        allPhotos.put("NotInASet", notInASet); //  txertatu ( NotInASet --> bildumarik gabeko argazkiak)

        Iterator allIter = allPhotos.keySet().iterator(); // datu guztiak ditugu. bildumen izenak zeharkatzeko iteratzailea lortu

        while (allIter.hasNext()) {
            String setTitle = (String) allIter.next();  // bildumaren hurrengo izena lortu
            System.out.println("Bilduma:" + setTitle);
            Collection currentSet = allPhotos.get(setTitle); // bildumaren argazkiak lortu
            Iterator setIterator = currentSet.iterator(); // bildumaren argazkiak zeharkatzeko iteratzailea lortu

            while (setIterator.hasNext()) { // bildumaren argazkiak zeharkatu

                Photo p = (Photo) setIterator.next();
                String title = p.getTitle();
                System.out.println(title);
            }
        }
    }

    public void kautotu() throws IOException, FlickrException {
        RequestContext rc = RequestContext.getRequestContext();

        if (this.authStore != null) {
            Auth auth = this.authStore.retrieve(this.nsid);
            if (auth == null) {
                this.authorize(); // throws Exception
            } else {
                rc.setAuth(auth);
            }
        }
    }

    public ArrayList<String> bildumenIzenakLortu() throws IOException, FlickrException {
        ArrayList<String> bildumak = new ArrayList<String>();

        PhotosetsInterface pi = flickr.getPhotosetsInterface(); // lortu bildumak kudeatzeko interfazea

        Iterator sets = pi.getList(this.nsid).getPhotosets().iterator(); // nsid erabiltzailearen bildumak zeharkatzeko iteratzailea lortu

        while (sets.hasNext()) { // bildumak dauden bitartean, zeharkatu
            Photoset set = (Photoset) sets.next(); //hurrengo bilduma
            String setTitle = set.getTitle(); //titulua soilik hartu nahi dugu
            bildumak.add(setTitle); //bildumaren izena arraylistera gehitu
            System.out.println("Bilduma:" + setTitle); //pantailaratu ikusteko ondo egin dela. Ezabatu daiteke
        }

        return bildumak;
    }

    public ArrayList<Photoset> bildumakLortu() throws FlickrException {
        ArrayList<Photoset> bildumak = new ArrayList<>();
        PhotosetsInterface pi = flickr.getPhotosetsInterface(); // lortu bildumak kudeatzeko interfazea
        Iterator sets = pi.getList(this.nsid).getPhotosets().iterator(); // nsid erabiltzailearen bildumak zeharkatzeko iteratzailea lortu
        while (sets.hasNext()) { // bildumak dauden bitartean, zeharkatu
            Photoset set = (Photoset) sets.next(); //hurrengo bilduma
            bildumak.add(set);
            //System.out.println(set.getTitle());
        }
        return bildumak;
    }

    public Photoset bilatuBildumaIzenaz(String bilduma) throws FlickrException {
        ArrayList<Photoset> bildumak = bildumakLortu();
        Iterator set = bildumak.iterator();
        boolean aurkitua=false;
        Photoset emaitza=null;
        while(!aurkitua && set.hasNext()){
            emaitza = (Photoset) set.next();
            if (emaitza.getTitle()==bilduma){
                aurkitua=true;
            }
        }
        return emaitza;

    }


    public PhotoList bildumarenArgazkiakLortu(String bilduma) throws FlickrException {
        PhotoList photos=null;

        PhotosetsInterface pi = flickr.getPhotosetsInterface(); // lortu bildumak kudeatzeko interfazea
        Iterator sets = pi.getList(this.nsid).getPhotosets().iterator(); // nsid erabiltzailearen bildumak zeharkatzeko iteratzailea lortu
        boolean aurkitua = false;
        while (sets.hasNext() && !aurkitua) { // bildumak dauden bitartean, zeharkatu
            Photoset set = (Photoset) sets.next(); // bilduma lortu
            if (set.getTitle().equals(bilduma)) {
                photos = pi.getPhotos(set.getId(), 500, 1);  // bildumaren lehenengo 500 argazki lortu
                Iterator d = photos.iterator();
                for (int u = 0; u < photos.size(); u++) {
                    Photo p = (Photo) d.next();
                    System.out.println(p.getTitle());
                }
            }
        }

        return photos;

    }


    //public BufferedImage argazkiarenArgazkia(String argazkia,String bilduma) throws FlickrException, IOException {

    public BufferedImage argazkiarenArgazkia(String argazkia,String bilduma) throws FlickrException, IOException {
        PhotosetsInterface pi = flickr.getPhotosetsInterface(); // lortu bildumak kudeatzeko interfazea
        Iterator sets = pi.getList(this.nsid).getPhotosets().iterator(); // nsid erabiltzailearen bildumak zeharkatzeko iteratzailea lortu
        boolean aurkitua = false;
        BufferedImage emaitza =null;

        while (sets.hasNext() && !aurkitua) { // bildumak dauden bitartean, zeharkatu
            Photoset set = (Photoset) sets.next(); // bilduma lortu
            if (set.getTitle().equals(bilduma)) {
                PhotoList photos = pi.getPhotos(set.getId(), 500, 1);  // bildumaren lehenengo 500 argazki lortu
                Iterator d = photos.iterator();

                boolean aurkitua2=false;
                while (!aurkitua2 &&d.hasNext()){
                    Photo p = (Photo) d.next();
                    if (p.getTitle()==argazkia){
                        //emaitza=p.getSmallImage();
                        //p.getOriginalUrl();
                        p.getSmallSquareImage();

                    }
                }
            }
        }
        return emaitza;
    }



    public static void main(String[] args) throws Exception {
       /*if (args.length < 3) {
            System.err.println("Usage: java " + Zeharkatu.class.getName() + " api_key nsid shared_secret");
            System.exit(1);
        }*/
        Zeharkatu bf = new Zeharkatu();
        System.out.println(bf.argazkiarenArgazkia("Orca", "Uretako animalia"));
        //bf.lortu();
        //bf.bildumakLortu();
        //bf.bildumarenArgazkiakLortu("Uretako animaliak");
        System.out.println("----------------------------------------");
        //bf.lortu();

    }
}