procedure     create_bwt_views(
  v_cht_in VARCHAR2 DEFAULT 'CHT_%',
  v_Tbs_in VARCHAR2 DEFAULT NULL,
  v_Buffer_in VARCHAR2 DEFAULT NULL)
/*
RS2 Software Group

GEN_BWT_SNAPSHOTS_W_TRIGGERS() was renamed to CREATE_BWT(). This will
be the standard procedure to create BWT. It provides the same functionality
of creating BWT as view and performance of snapshot. Although BWT are still snapshots
it now provides automatic refresh of data, but not of the structure.

How to use:
  1.) To create bwt snapshots for all CHT's assigning tablespace and buffer pool.
  EXEC Create_Bwt(V_TBS_IN=>'BW3SMALL',v_buffer_in=>'KEEP')


Requirements:
    The following must be set in Oracle Db initialization parameter. Values can be higher.
        job_queue_processes = 4
        job_queue_interval = 10 (Upto 8i only)

Changes
=======
c.gevido - 16/12/2003: Change name from GEN_BWT_SNAPSHOTS_W_TRIGGERS() to CREATE_BWT()
c.gevido - 18/12/2003: Add a check for Oracle init parameters
c.gevido - 14/09/2004: Compute statistics of the create BWT
c.gevido - 13/10/2004: Check for required Oracle Init parameter values when Create_BWT() is executed.
gordana  - 14/03/2006: Get PK columns from cht table. Bug fixed, since v_dbversion is character version 8 was greater then 10.
*/
IS
/* Formatted on 21/12/2009 09:01:48 (QP5 v5.115.810.9015) */
v_cht_name     varchar2(30) := null;
v_columns      varchar2(1000) := null;
v_index_field  varchar2(20) := null;
v_Cursor  NUMBER :=0;
v_Ignore  NUMBER :=0;
v_SQL    VARCHAR2(2000) := NULL;
v_DropBWT  VARCHAR2(1000) := NULL;
v_Union_Columns VARCHAR2(1000) := NULL;
v_BWT_name   VARCHAR2(500) := NULL;
v_Constr  VARCHAR2(500) := NULL;
v_PkName  VARCHAR2(30) := NULL;
v_Counter  INTEGER:= 0;
v_SPTabName  VARCHAR2(30) := NULL;
v_Buffer    VARCHAR2(30) := UPPER(Trim(v_Buffer_in));
v_Tbs    VARCHAR2(30) := UPPER(Trim(v_Tbs_in));


INVALID_JOB_QUEUE_PROCESS EXCEPTION;
PRAGMA EXCEPTION_INIT(INVALID_JOB_QUEUE_PROCESS,-20002);

INVALID_JOB_QUEUE_INTERVAL EXCEPTION;
PRAGMA EXCEPTION_INIT(INVALID_JOB_QUEUE_INTERVAL,-20003);

INVALID_COMPATIBLE EXCEPTION;
PRAGMA EXCEPTION_INIT(INVALID_COMPATIBLE,-20001);
  --
  -----------------------------------------------------
  --
  procedure checkRequirements
  is
    v_DBVersion     VARCHAR2(20);
    v_DBCompatible  VARCHAR2(20);
    v_JQP           BINARY_INTEGER;
    v_JQI           BINARY_INTEGER;
    v_ParamType     BINARY_INTEGER;
    v_StringDummy   VARCHAR2(500);
    v_Return        BOOLEAN := TRUE;
    v_ErrorMsg      VARCHAR2(2000);
  begin
    --
    dbms_utility.db_version(v_DBVersion,v_DbCompatible);
    --
    IF      (v_DBCompatible <  '8.1' AND v_DBCompatible NOT LIKE '10%')
        AND (v_DBVersion    >= '8.1' OR v_DBVersion LIKE '10%') THEN
      v_ErrorMsg := 'Important!'||
      'Oracle Initialization Parameter "compatible" must have value 8.1 or higher' ||
      'No BWT was dropped or created.';
      raise_application_error(-20001,v_ErrorMsg);
    END IF;
    --
    v_ParamType := dbms_utility.get_parameter_value('job_queue_processes',v_JQP, v_StringDummy);
    --
    if trim(v_JQP) is null or v_JQP = 0 then
      v_ErrorMsg := 'Important!'||
          'Oracle Initialization Parameter "job_queue_processes" must have value greater than 0.'||
          'No BWT was dropped or created.';
      raise_application_error(-20002,v_ErrorMsg);
    end if;
    --
    if substr(v_DBVersion,1,1) in ('7','8') then
      --Oracle8i and lower versions
      --
      v_ParamType := DBMS_UTILITY.GET_PARAMETER_VALUE('job_queue_interval',
                             v_JQI, v_StringDummy);
      --
      if trim(v_jqi) is null or v_jqi = 0 then
        v_ErrorMsg := 'Important!'||
          'Oracle Initialization Parameter "job_queue_interval" must have value greater than 0.'||
          'No BWT was dropped or created.';
          raise_application_error(-20003,v_ErrorMsg);
      end if;
    --
    END IF;
    --
  END checkRequirements;
  --
  -----------------------------------------------------
  --
  function GetPKColumnList (p_TableName VARCHAR2) RETURN VARCHAR2
  is
    --
    cursor cur_cols is
      select column_name
      from   user_cons_columns cols,
             user_constraints cons
      where  cons.table_name = p_tablename
      and    cons.constraint_type = 'P'
      and    cons.table_name = cols.table_name
      and    cons.constraint_name = cols.constraint_name
      order by position;
    --
    v_PKColumnList VARCHAR2(2000);
    --
  begin
    for rec_cols in cur_cols
    loop
      if cur_cols%rowcount = 1 then
        v_pkcolumnlist :='('||rec_cols.column_name;
      else
        v_pkcolumnlist :=v_pkcolumnlist ||','||rec_cols.column_name;
      end if;
    end loop;
    --
    if v_pkcolumnlist is not null then
      v_pkcolumnlist := v_pkcolumnlist || ')';
    end if;
    --
    return v_pkcolumnlist;
    --
  end GetPKColumnList;
  --
  -----------------------------------------------------
  --
begin
  --
  CheckRequirements;
  --
  v_cht_name := upper(rtrim(ltrim(nvl(v_cht_in,'CHT_%'))));
  --
  if v_Buffer is not null and v_Buffer not in ('KEEP','RECYCLE') then
        --Assert values in the condition
    raise_application_error(-20100,'Invalid BUFFER_POOL parameter value. Can have only values KEEP or RECYCLE. Default value is null.');
  end if;
  --
  for rec in (
    select table_name
    from   user_tables
    where  table_name like 'CHT_%'
    and    table_name not like 'CHT_CACHE%'
    and    table_name like v_cht_name)
  loop
    --
    if rec.table_name = 'CHT_CURRENCY' then
      v_index_field := 'ISO_CODE';
    else
      v_index_field := 'INDEX_FIELD';
    end if;
    --
    v_bwt_name := 'BWT'||substr(rec.table_name, 4);
    --
    -- Drop snapshot if exist.
    --
    begin
      execute immediate 'drop snapshot '||v_bwt_name;
    exception
    when others then
      null;
    end;
    --
    -- Drop table if exist.
    --
    begin
      execute immediate 'drop table '||v_bwt_name;
    exception
    when others then
      null;
    end;
    --
    -- Drop view if exist.
    --
    begin
      execute immediate 'drop view '||v_bwt_name;
    exception
    when others then
      null;
    end;
    --
    -- create view
    --
    v_sql := 'create or replace force view '||v_bwt_name|| ' as select ';
    --
    for rec_col in (
      select lower(column_name) column_name
      from   user_tab_columns
      where  table_name = rec.table_name
      order  by column_id
    ) loop
      --
      v_sql := v_sql||rec_col.column_name||',';
      --
    end loop;
    --
    v_sql := rtrim(v_sql, ',');
    --
    v_sql := replace(v_sql, 'institution_number','sys.institution_number');
    --
    v_sql := v_sql
             ||' from '||rec.table_name||' def, sys_institution_licence sys '
             ||' where sys.institution_number > ''00000000'''
             ||' and   def.'||v_index_field||' >= ''000'''
             ||' and  (def.institution_number = ''00000000'''
                    ||' and not exists (select ''X'''
                                    ||' from '||rec.table_name||' tbl '
                                    ||' where tbl.institution_number = sys.institution_number )'
                   ||' or def.institution_number = sys.institution_number)';
    --
    ---------------------------------------------------------
    -- Create trigger for the original CHT table to automate
    -- the snapshot refresh
    ---------------------------------------------------------
    begin
      --
      for trg_rec in (
        select trigger_name
        from   user_triggers
        where  table_name = rec.table_name
      ) loop
        --
        execute immediate 'DROP TRIGGER ' || trg_rec.trigger_name;
        --
      end loop;
      --
    exception
    when others then
      null;
    end;
    --
    for sp_refresh in (
      select job
      from   user_jobs
      where upper(what) like 'DBMS_REFRESH.REFRESH(%BWT_ACCOUNT_BALANCE_RANGE%);'
    ) loop
      --Remove any existing Refresh Snapshot jobs. If not, the jobs will generate errors on DB alert.log files which
      --can grow at alarming rate.
      DBMS_JOB.REMOVE(sp_refresh.job);
    end loop;
    --
    v_sql := 'CREATE OR REPLACE TRIGGER TRG'||SUBSTR(rec.table_name,1,27)||
        ' AFTER DELETE OR INSERT OR UPDATE ON '||rec.table_name||
        ' DECLARE  jobno number; '||
        ' BEGIN DBMS_JOB.SUBMIT(jobno,''DBMS_SNAPSHOT.REFRESH('''''||v_BWT_name||''''');'',SYSDATE, NULL); END;';
    --
    v_Cursor := DBMS_SQL.OPEN_CURSOR;
    DBMS_SQL.PARSE(v_Cursor, v_sql,dbms_sql.native);
    v_Ignore :=DBMS_SQL.EXECUTE(v_Cursor);
    DBMS_SQL.CLOSE_CURSOR(v_Cursor);
    --
    COMMIT;
    --
    -- Analyze the BWT after creation.
    dbms_stats.gather_table_stats(ownname=>USER,tabname=>v_BWT_name,estimate_percent =>30);
    --
  end loop;
  --
exception
when  invalid_job_queue_process then
  raise invalid_job_queue_process;
when invalid_job_queue_interval then
  raise invalid_job_queue_interval;
when invalid_compatible then
  raise invalid_compatible;
when others then
  if dbms_sql.is_open(v_cursor) then
    dbms_sql.close_cursor(v_cursor);
  end if;
  --
  raise_application_error(-20100,'ERROR IN CREATE_BWT: ['|| v_bwt_name ||'] '||sqlerrm(sqlcode));
  --
end create_bwt_views;