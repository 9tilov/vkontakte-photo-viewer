package com.moggot.vkontaktephotoviewer.observer;

import com.vk.sdk.api.model.VKApiPhoto;

/**
 * Created by toor on 22.02.17.
 */

public interface Observer {

    void update(VKApiPhoto photo);

}
