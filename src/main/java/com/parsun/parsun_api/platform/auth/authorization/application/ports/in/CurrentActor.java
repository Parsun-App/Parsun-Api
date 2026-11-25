package com.parsun.parsun_api.platform.auth.authorization.application.ports.in;

import java.util.UUID;

public interface CurrentActor {
    UUID requireId();
}
