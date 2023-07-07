procedure bw_launch_uns_process(
  p_instno in varchar2,
  p_processno in varchar2
)
is
  rec_fact        bw_uns_mdf.typ_rec_fact;
  tab_data        bw_uns_mdf.typtab_fact;
  --
  r_p_log int_process_log%rowtype;
  --
begin
  --
    select *
    into r_p_log
    from int_process_log
    where process_number = p_processno;
    --
    BW_PROCESS_CONTROL.NOTIFY_CLIENTS(
      P_PARENT_PROCESS_LOG      => r_p_log,
      P_DEFAULT_UNS_INST        => '00000001',
      P_UNS_ID                  => '9992'
    );

   /* TC 20150303 -- replaced all the below with a call to the BW_PROCESS_CONTROL.NOTIFY_CLIENT
                     so that all UNS assertions are created

    FOR r_messages IN ( select client_number
                        from   sys_process_message_uns_instr
                        where  institution_number     = '00000001'
                               and applies_to_institution in (p_instno, '00000000')
                        group by client_number
                        order by client_number )
    LOOP
      --
      rec_fact.key         := 'PROCESS_NUMBER';
      rec_fact.value       := p_processno;
      tab_data(rec_fact.key) := rec_fact;
      --
      rec_fact.key         := 'CLIENT_NUMBER';
      rec_fact.value       := r_messages.client_number;
      tab_data(rec_fact.key) := rec_fact;
      --

      bw_uns_mdf.generate_and_queue( p_instno      => '00000001'
                                     , p_uns_id      => '9992'
                                     , p_source_id   => '006'
                                     , p_trigger_id  => '028'
                                     , p_message_id  => '028'
                                     , p_data        => tab_data
                                     , p_generate    => true );

       --
    END LOOP;
  */
end bw_launch_uns_process;