package io.storj.mobile.domain.settings;

import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;

public interface ISettingsRepository {
    SingleResponse<Settings> get(String id);
    Response update(Settings settings);
    Response insert(Settings settings);

    Response createTable();
}
