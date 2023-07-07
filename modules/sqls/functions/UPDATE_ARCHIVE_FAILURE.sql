PROCEDURE     UPDATE_ARCHIVE_FAILURE(idnum VARCHAR2, add_data varchar2, p_status varchar2 DEFAULT '003' ) IS
/******************************************************************************
   NAME:       UPDATE_ARCHIVE_FAILURE
   PURPOSE:    Archive failed messages

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        18/10/2013   Daniel Dalli     1. Created this procedure.
   2.0        13/03/2015   Keithrick Buh    1. Add ADD_DATA to the parameters. Will include
                                               the failure reason when available.
   3.0        28/07/2015   Joseph Merc      1. Add p_status to the parameters. Will set the
                                               DELIVERY_STATUS to the new parameter p_status.

   NOTES:

   Automatically available Auto Replace Keywords:
      Object Name:     UPDATE_ARCHIVE_FAILURE
      Sysdate:         18/10/2013
      Date and Time:   18/10/2013, 09:17:54, and 18/10/2013 09:17:54
      Username:         (set in TOAD Options, Procedure Editor)
      Table Name:       (set in the "New PL/SQL Object" dialog)

******************************************************************************/
BEGIN
  --
  INSERT INTO msg_outbox_error(ID, INSTITUTION_NUMBER, EFFECTIVE_TIMESTAMP,
   DELIVERY_METHOD, DELIVERY_STATUS, ADDRESS, EMAIL, CONTACT_NUMBER, RECIPIENT,
   MESSAGE_BODY, MESSAGE_SUBJECT, UNS_ID, ADD_DATA)
  SELECT ID, INSTITUTION_NUMBER, EFFECTIVE_TIMESTAMP, DELIVERY_METHOD,
   p_status, ADDRESS, EMAIL, CONTACT_NUMBER, RECIPIENT,
   MESSAGE_BODY, MESSAGE_SUBJECT, UNS_ID, ADD_DATA
  FROM MSG_OUTBOX WHERE ID = idnum;
  --
  commit;
  --
  delete msg_outbox where id = idnum;
  --
end;