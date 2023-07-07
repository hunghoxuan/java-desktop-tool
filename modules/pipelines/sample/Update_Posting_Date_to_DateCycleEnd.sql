---- Update Posting date------
update sys_posting_date
set posting_date =(SELECT TO_CHAR(SYSDATE, 'YYYYMMDD') FROM dual)
where  INSTITUTION_NUMBER = '00000003' and Station_number = 215;
