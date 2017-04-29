package com.moggot.vkontaktephotoviewer.observer;

/**
 * Created by toor on 22.02.17.
 */

public interface Observable {

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();

}
