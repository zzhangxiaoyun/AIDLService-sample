// IRemoteService.aidl
package com.race604.servicelib;

import com.race604.servicelib.IUpateRemoteViewsCallback;

interface IRemoteService {
    int someOperate(int a, int b);

    void join(IBinder token, String name);
    void leave(IBinder token);
    List<String> getParticipators();

    void registerParticipateCallback(IUpateRemoteViewsCallback cb);
    void unregisterParticipateCallback(IUpateRemoteViewsCallback cb);
}

