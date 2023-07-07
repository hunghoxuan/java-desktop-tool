PROCEDURE     UPDATE_ARCHIVE(idnum VARCHAR2, filenum VARCHAR2 := null, add_data varchar2 := null) is
/******************************************************************************
   NAME:       UPDATE_ARCHIVE
   PURPOSE:    Archive successfull messages

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        N/A         Keithrick Buh    1. Created this procedure.
   2.0        N/A         Keithrick Buh    1. Added filenum to the parameters. Required for FDB
   3.0        13/03/15    Keithrick Buh    1. Added ADD_DATA to the parameters. Will be used
                                              to store the ID replied back by clickatell.
   4.0        04/07/2017  Amos Chetcuti    1. Add MESSAGE_FROM to INSERT and SELECT criteria.

   NOTES:

******************************************************************************/
BEGIN
  --
  INSERT INTO MSG_OUTBOX_ARCHIVE(ID, INSTITUTION_NUMBER, EFFECTIVE_TIMESTAMP,
   DELIVERY_METHOD, DELIVERY_STATUS, ADDRESS, EMAIL, CONTACT_NUMBER, RECIPIENT,
   MESSAGE_BODY, MESSAGE_SUBJECT, UNS_ID, FILE_NUMBER, ADD_DATA, MESSAGE_FROM)
  SELECT ID, INSTITUTION_NUMBER, EFFECTIVE_TIMESTAMP, DELIVERY_METHOD,
   '002', ADDRESS, EMAIL, CONTACT_NUMBER, RECIPIENT,
   MESSAGE_BODY, MESSAGE_SUBJECT, UNS_ID, filenum, add_data, MESSAGE_FROM
  FROM MSG_OUTBOX WHERE ID = idnum;
  --
  commit;
  --
  delete msg_outbox where id = idnum;
  --
end;