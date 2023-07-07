DECLARE  jobno number;
BEGIN
  for tbs in (
      select mview_name from user_mviews)
  loop
    DBMS_MVIEW.REFRESH(tbs.mview_name);
  end loop;
END;
/


