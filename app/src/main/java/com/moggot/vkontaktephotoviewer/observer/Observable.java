package com.moggot.vkontaktephotoviewer.observer;

public interface Observable {

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();

}
