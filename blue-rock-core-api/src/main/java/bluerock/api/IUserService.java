package bluerock.api;

import bluerock.params.InitializeParam;
import dataworks.web.commons.ServiceResponse;

public interface IUserService
{
    ServiceResponse<Boolean> initialize(InitializeParam initializeParam);
}
