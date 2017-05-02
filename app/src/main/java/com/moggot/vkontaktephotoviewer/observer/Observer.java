package com.moggot.vkontaktephotoviewer.observer;

import com.vk.sdk.api.model.VKApiPhoto;

/**
 * Интерфейс, с помощью которого наблюдатель получает оповещение
 * Реализован паттерн "Наблюдатель"
 */
public interface Observer {

    /**
     * Обновление наблюдателя
     * @param photo - объект фото, загруженный с сервера
     */
    void update(VKApiPhoto photo);

}
