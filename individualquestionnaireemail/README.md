# Individual Questionnaire Email Lambda Function
This is an [AWS Lambda](https://aws.amazon.com/lambda/) function for the ONS 2017 Census Test. Its purpose is to receive S3 event notifications when a JSON file is dropped into an S3 bucket by Respondent Home. That file contains the contact information submitted by an individual requesting an individual census questionnaire to be sent to them.

The Lambda function collects the JSON file and parses the content into a business object.
It then calls the [GOV.UK Notify](https://www.notifications.service.gov.uk/) service to send an email to an ONS Response Management shared mailbox, inserting the contact details collected into the Notify email template.

Having sent the email, it copies the original file into a second bucket whose name is derived from the first bucket, before finally removing the file from the original bucket.

If any errors occur in calling Notify, or in the copy/delete S3 operation, the Lambda function will throw a runtime exception that will be caught by the AWS Lambda execution framework, and event redelivery will be attempted, up to a configurable retry limit.

The Notify API key, template ID, and email recipient address are passed in from AWS as environment variables:

```
NOTIFY_API_KEY
EMAIL_RECIPIENT_ADDRESS
EMAIL_TEMPLATE_ID
```

## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics)
