package mw.gov.health.lmis.reports.service.referencedata;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mw.gov.health.lmis.reports.dto.external.ResultDto;
import mw.gov.health.lmis.reports.dto.external.UserDto;
import mw.gov.health.lmis.utils.RequestParameters;

@Service
public class UserReferenceDataService extends BaseReferenceDataService<UserDto> {

  @Override
  protected String getUrl() {
    return "/api/users/";
  }

  @Override
  protected Class<UserDto> getResultClass() {
    return UserDto.class;
  }

  @Override
  protected Class<UserDto[]> getArrayResultClass() {
    return UserDto[].class;
  }

  /**
   * This method retrieves a user with given name.
   *
   * @param name the name of user.
   * @return UserDto containing user's data, or null if such user was not found.
   */
  public UserDto findUser(String name) {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", name);

    Page<UserDto> users = getPage("search", RequestParameters.init(), requestBody);
    return users.getContent().isEmpty() ? null : users.getContent().get(0);
  }

  /**
   * Check if user has a right with certain criteria.
   *
   * @param user     id of user to check for right
   * @param right    right to check
   * @return an instance of {@link ResultDto} with boolean .
   */
  public ResultDto<Boolean> hasRight(UUID user, UUID right) {
    RequestParameters parameters = RequestParameters
        .init()
        .set("rightId", right);
    
    return getResult(user + "/hasRight", parameters, Boolean.class);
  }
}
