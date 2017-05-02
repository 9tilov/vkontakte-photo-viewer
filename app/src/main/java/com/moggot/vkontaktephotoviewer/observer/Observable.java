package com.moggot.vkontaktephotoviewer.observer;

/**
 * Интерфейс, определяющий метода для добавления, удаления и оповещение наблюдателей
 */
public interface Observable {

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();

}
