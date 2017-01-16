package uk.gov.ons.ctp.response.questionnairerequest.service;

import java.util.HashMap;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import uk.gov.ons.ctp.response.questionnairerequest.model.QuestionnaireRequest;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationResponse;

/**
 * The service implementation for NotifyService
 */
public class NotifyService {
  private static final String NOTIFY_API_ENV_KEY = "NOTIFY_API_KEY";
  private static final String EMAIL_RECIPIENT_ADDRESS_ENV_KEY = "EMAIL_RECIPIENT_ADDRESS";
  private static final String EMAIL_TEMPLATE_ID_ENV_KEY = "EMAIL_TEMPLATE_ID";
  private static final String BAD_REQUEST = "Status code: 400";

  public static final String EXCEPTION_NOTIFY_SERVICE = "An error occurred contacting GOV.UK Notify: ";
  public static final String NOTIFY_SMS_NOT_SENT = "Notify Sms Not Sent";
  public static final String NOTIFY_SMS_SENT = "Notify Sms Sent";

  public static final String CLIENT_IP_KEY = "client_ip";
  public static final String FIRST_NAME_KEY = "first_name";
  public static final String LAST_NAME_KEY = "last_name";
  public static final String BUILDING_KEY = "building";
  public static final String STREET_KEY = "street";
  public static final String TOWN_KEY = "town";
  public static final String COUNTY_KEY = "county";
  public static final String POSTCODE_KEY = "postcode";
  public static final String MOBILE_KEY = "mobile";

  public static boolean process(QuestionnaireRequest request, LambdaLogger logger) {
    boolean success = false;
    try {
      NotificationClient notificationClient = new NotificationClient(System.getenv(NOTIFY_API_ENV_KEY));
      String templateId = System.getenv(EMAIL_TEMPLATE_ID_ENV_KEY);
      String recipientEmail = System.getenv(EMAIL_RECIPIENT_ADDRESS_ENV_KEY);
      logger.log("api-key:" + System.getenv(NOTIFY_API_ENV_KEY));
      logger.log("templateId:" + templateId);
      logger.log("recipientEmail:" + recipientEmail);

      HashMap<String, String> personalisation = new HashMap<>();
      personalisation.put(CLIENT_IP_KEY, request.getClientIP());
      personalisation.put(FIRST_NAME_KEY, request.getFirstName());
      personalisation.put(LAST_NAME_KEY, request.getLastName());
      personalisation.put(BUILDING_KEY, request.getBuilding());
      personalisation.put(STREET_KEY, request.getStreet());
      personalisation.put(TOWN_KEY, request.getTown());
      personalisation.put(COUNTY_KEY, request.getCounty());
      personalisation.put(POSTCODE_KEY, request.getPostcode());
      personalisation.put(MOBILE_KEY, request.getMobile());

      notificationClient.sendEmail(templateId, recipientEmail, personalisation);
      success = true;
    } catch (NotificationClientException e) {
      logger.log("Ooops:" + e.getMessage());
    }
    return success;
  }
}
