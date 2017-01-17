This is an AWS lambda for ONS CTP 2017 Test. Its purpose is to receive S3 Event notifications when a json file is dropped into an S3 bucket by the Respondent Home UI. That file contains the personal information submitted by an individual requesting a census questionnaire to be sent to them. 

The lamda collects the json file and parses the content into a business object.
It then calls the UK.Gov Notify service to send an email to the ONS Helpdesk, inserting the personal details collected into the email template.

Having sent the email, it copies the original file into a second bucket whose name is derived from the first bucket, before finally removing the file from the original bucket.

If any errors occur in calling Notify, or in the copy/delete S3 operation, the lambda will throw a runtime exception that will be caught by the AWS lambda execution framework, and event redelivery will be attempted, up to a configurable retry limit.

The Notify API key, template ID, and email recipient address are passed in from AWS as environment variables :

NOTIFY_API_KEY
EMAIL_RECIPIENT_ADDRESS
EMAIL_TEMPLATE_ID


## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics)
