package uk.gov.ons.ctp.response.questionnairerequest.lambda;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.ons.ctp.response.questionnairerequest.model.QuestionnaireRequest;
import uk.gov.ons.ctp.response.questionnairerequest.service.NotifyService;

public class QuestionnaireRequestHandler implements RequestHandler<S3Event, Object> {
  
  public static final String PROCESSED_POSTFIX = ".processed";

  public Object handleRequest(S3Event input, Context context) {
    ObjectMapper mapper = new ObjectMapper();
    AmazonS3Client s3Client = new AmazonS3Client();

    for (S3EventNotificationRecord record : input.getRecords()) {
      String bucketName = record.getS3().getBucket().getName();
      String key = record.getS3().getObject().getKey();
      context.getLogger().log("Bucket:" + bucketName + " Key:"+key);

      S3Object object = s3Client.getObject(
          new GetObjectRequest(bucketName, key));

      try (InputStream inputStream = object.getObjectContent()) {
        QuestionnaireRequest questionnaireRequest = mapper.readValue(inputStream, QuestionnaireRequest.class);
        boolean success = NotifyService.process(questionnaireRequest, context.getLogger());
        if (success) {
          s3Client.copyObject(bucketName, key, bucketName + PROCESSED_POSTFIX, key);
          s3Client.deleteObject(bucketName, key);
        } else {
          context.getLogger().log("Failed to send notify email :" + questionnaireRequest);
        }
      } catch (IOException ioe) {
        context.getLogger().log("Failed to deserialize request");
      }
    }
    return null;
  }

}
