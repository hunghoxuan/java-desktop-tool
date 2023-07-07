select  posting_date
from    sys_posting_date
where   institution_number = '&institution_number'
and     station_number in (
                            select  stationgroup
                            from    sys_user_information
                            where   userid = '999999');
							
select  cbr.billing_cycle as billing_cycle_id,
        bwt.billing_cycle as billing_cycle_name,
        cbr.current_cycle_start,
        cbr.current_cycle_end
from    cbr_billing_cycle cbr,
        bwt_billing_cycle bwt
where   cbr.institution_number = '&institution_number'
and     bwt.institution_number = cbr.institution_number
and     bwt.language = 'USA'
and     bwt.index_field = cbr.billing_cycle;