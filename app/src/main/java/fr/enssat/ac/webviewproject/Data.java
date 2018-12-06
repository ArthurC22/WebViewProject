package fr.enssat.ac.webviewproject;

/**
 * Author : Arthur Chevallier
 * Date : 20/11/2018
 * Description : Java Class to manage link between chapters, video and wikipedia webpage
 */

public class Data {
    private int Position;
    private String context;
    private String url;

    public Data(int Position, String context, String url){
        if(!url.contains("http")){
            this.url = "https://fr.wikipedia.org/wiki/Big_Buck_Bunny" + url;
        } else {
            this.url = url;
        }
        this.context = context;
        this.Position = Position;
    }

    public int getPosition() {
        return Position;
    }

    public String getContext() {
        return context;
    }

    public String getUrl() {
        return url;
    }
}
