package mio.storj.mobile.domain.settings;

import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;

public interface ISettingsRepository {
    SingleResponse<Settings> get(String id);
    Response update(Settings settings);
    Response insert(Settings settings);

    Response createTable();
}
