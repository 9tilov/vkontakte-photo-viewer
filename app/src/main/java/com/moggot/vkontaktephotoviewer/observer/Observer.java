package com.moggot.vkontaktephotoviewer.observer;

import com.vk.sdk.api.model.VKApiPhoto;

public interface Observer {

    void update(VKApiPhoto photo);

}
