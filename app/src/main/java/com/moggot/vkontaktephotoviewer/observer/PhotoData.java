package com.moggot.vkontaktephotoviewer.observer;

import com.vk.sdk.api.model.VKApiPhoto;

import java.util.LinkedList;
import java.util.List;

/**
 * Класс с данными фото, реализующий интерфейс {@link Observable}
 */
public class PhotoData implements Observable {

    private List<Observer> observers;

    private VKApiPhoto photo;

    public PhotoData() {
        observers = new LinkedList<>();
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers)
            observer.update(photo);
    }

    public void setPhoto(VKApiPhoto photo) {
        this.photo = photo;
        notifyObservers();
    }
}
