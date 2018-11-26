package fr.enssat.lnfl.enrichedvideo;

/**
 * Created by Omar et Thibault on 20/12/2017.
 */

public class Metadata {
    private int sPosition;
    private String context;
    private String url;

    public Metadata(int _sPosition,String _context, String _url){
        if(!_url.contains("http")){
            this.url = "https://en.wikipedia.org/wiki/Big_Buck_Bunny#" + _url;
        } else {
            this.url = _url;
        }
        this.context = _context;
        this.sPosition = _sPosition;
    }

    public int getSPosition() {
        return sPosition;
    }

    public String getContext() {
        return context;
    }

    public String getUrl() {
        return url;
    }
}
