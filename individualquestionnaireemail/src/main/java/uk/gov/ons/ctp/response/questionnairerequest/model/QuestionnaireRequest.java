package uk.gov.ons.ctp.response.questionnairerequest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QuestionnaireRequest {
  @JsonProperty("client_ip")
  private String clientIP;

  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  private String building;
  private String street;
  private String town;
  private String county;
  private String postcode;
  private String mobile;
}
