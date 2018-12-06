package fr.enssat.ac.webviewproject;

import java.util.LinkedList;
import java.util.List;

/**
 * Author : Arthur Chevallier
 * Date : 20/11/2018
 * Description : Java Class to manage the links (Class Data)
 */

public class DataManager {

    private List<Data> DataList;

    public DataManager(){
        this.DataList = new LinkedList<>();
    }

    public void add(Data data){
        this.DataList.add(data);
    }

    public void add(int position,String context, String url){
        this.DataList.add(new Data(position,context,url));
    }

    public int getPositionByContext(String context){
        for (Data m:this.DataList){
            if(m.getContext()==context){
                return m.getPosition();
            }
        }
        return -1;
    }

    public String getContextByPosition(int position){
        Data data = this.DataList.get(0);
        for (Data m:this.DataList){
            if(data.getPosition() < m.getPosition() && position > m.getPosition()){
                data = m;
            }
        }
        return data.getContext();
    }

    public String getUrlByPosition(int position){
        Data data = this.DataList.get(0);
        for (Data m:this.DataList){
            if(data.getPosition() < m.getPosition() && position > m.getPosition()){
                data = m;
            }
        }
        return data.getUrl();
    }
}
