procedure BW_DISABLE_ENABLE_TRIGGER
( p_bwt_name in varchar2
, p_trigger_name in varchar2
, p_procname in varchar2
, p_dbg in number default 0
)
is
  i_count pls_integer := 0;
  n_dbg   number := 1;
  e_disable_trigger exception;
begin
  begin
    --
    if n_dbg <= p_dbg then
      bw_util.dbg('BDET: Started global procedure BW_DISABLE_ENABLE_TRIGGER with process name ['||p_procname||']',0);
    end if;
    --
    -- Disable the trigger
    begin
      execute immediate 'ALTER TRIGGER '||p_trigger_name||' DISABLE';
    exception
      when others then
        raise e_disable_trigger;
    end;
    --
    if n_dbg <= p_dbg then
      bw_util.dbg('BDET: Disabled trigger '||p_trigger_name,0);
    end if;
    --
    loop
    --
      -- Poll every 20 seconds
      dbms_lock.sleep(20);
      --
      if n_dbg <= p_dbg then
        bw_util.dbg('BDET: Polling v$session...',0);
      end if;
      --
      select count(*)
      into   i_count
      from   v$session
      where  module like p_procname||':%';
      --
      if i_count = 0 then
        -- Exit polling state, since job is no longer registered under v$session
        exit;
      end if;
    end loop;
    --
    if n_dbg <= p_dbg then
      bw_util.dbg('BDET: Ended BW_DISABLE_ENABLE_TRIGGER with process name ['||p_procname||']',0);
    end if;
    --
  exception
    when others then
      if n_dbg <= p_dbg then
        bw_util.dbg('BDET: Error caused by process name ['||p_procname||']',0);
        bw_util.dbg('BDET: ['||sqlerrm||']',0);
      end if;
  end;
  --
  -- Re-enable the trigger
  execute immediate 'ALTER TRIGGER '||p_trigger_name||' ENABLE';
  --
  dbms_snapshot.refresh(''''||p_bwt_name||'''');
  --
  if n_dbg <= p_dbg then
    bw_util.dbg('BDET: Enabled '||p_trigger_name,0);
  end if;
  --
exception
  when e_disable_trigger then
    bw_util.dbg('BDET: Error disabling trigger '||p_trigger_name,0);
    bw_util.dbg('BDET: ['||sqlerrm||']',0);
  when others then
    bw_util.dbg('BDET: Unhandled error '||p_trigger_name,0);
    bw_util.dbg('BDET: ['||sqlerrm||']',0);
    bw_util.dbg('BDET: Backtrace: ['||DBMS_UTILITY.format_error_backtrace||']',0);
end BW_DISABLE_ENABLE_TRIGGER;