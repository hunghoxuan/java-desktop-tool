update cis_client_details
set client_status = '004'
where client_number = '00005133';

update cas_client_account
set acct_status = '002'
where client_number = '00005133';

update svc_client_service
set  service_status = '002'
where client_number = '00005133';

update svc_client_service
set  service_status = '002'
where client_number = '00005133';

update cis_client_links
set contract_status = '004'
where client_number = '00005133';

update cas_cycle_book_balance
set processing_status = '015'
where acct_number like '00005133%'
and date_cycle_start >= '20161101';

commit;