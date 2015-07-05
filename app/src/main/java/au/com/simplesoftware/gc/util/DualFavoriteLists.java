package au.com.simplesoftware.gc.util;

import java.util.ArrayList;
import java.util.List;

import au.com.simplesoftware.gc.bo.ParseMyFavoriate;

/**
 * Created by steven on 5/07/2015.
 */
public class DualFavoriteLists {
    public List<ParseMyFavoriate> myFavoriatesList = new ArrayList<ParseMyFavoriate>();
    public List<ParseMyFavoriate> myFavoriatesListUpdates = new ArrayList<ParseMyFavoriate>();

    public void addOriginal(ParseMyFavoriate favoriate)
    {
        myFavoriatesList.add(favoriate);
        myFavoriatesListUpdates.add(favoriate);
    }

    public void addAllOriginal(List<ParseMyFavoriate> favoriates)
    {
        for(ParseMyFavoriate favor: favoriates) {
            myFavoriatesList.add(favor);
            myFavoriatesListUpdates.add(favor);
        }
    }

    public  void add(ParseMyFavoriate favoriate)
    {
        myFavoriatesListUpdates.add(favoriate);
    }

    public void remove(ParseMyFavoriate favoriate)
    {
        myFavoriatesListUpdates.remove(favoriate);
    }

    public void saveAllUpdates()
    {
        for(ParseMyFavoriate favor : myFavoriatesListUpdates)
        {
            // its new favorite
            if(!myFavoriatesList.contains(favor))
            {
                favor.saveInBackground();
            }
        }
        for(ParseMyFavoriate favor : myFavoriatesList)
        {
            // its removed favorite
            if(!myFavoriatesListUpdates.contains(favor))
            {
                favor.deleteInBackground();
            }
        }
    }

    public void clear()
    {
        myFavoriatesListUpdates.clear();
        myFavoriatesList.clear();
    }
}
